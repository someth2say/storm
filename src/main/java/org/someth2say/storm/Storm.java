package org.someth2say.storm;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.jboss.logging.Logger;
import org.someth2say.storm.category.Categorizers;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.configuration.PicocliConfigSource;
import org.someth2say.storm.stat.Stats;
import org.someth2say.storm.utils.SerializationUtils;

import io.quarkus.runtime.QuarkusApplication;
import picocli.CommandLine;

import static org.someth2say.storm.utils.ConsoleColors.*;

@Singleton
public class Storm implements QuarkusApplication {

    private static final Logger LOG = Logger.getLogger(Storm.class);
    public static final String RESET = "\033[0m";  // Text Reset

    @Inject
    Configuration configuration;

    @Override
    public int run(final String... args) throws Exception {
        Category rootCategory = runAsCategory(args);
        if (rootCategory == null) {
            return -1;
        }
        System.out.println(rootCategory);
        return 0;
    }

    private boolean nullOrEmpty(final Collection collectio) {
        return collectio == null || collectio.isEmpty();
    }

    public Category runAsCategory(String... args) throws Exception {

        CommandLine commandLine = PicocliConfigSource.commandLine;
        if (commandLine.isUsageHelpRequested()) {
            printHelp(commandLine);
        } else if (commandLine.isVersionHelpRequested()) {
            printVersion(commandLine);
        } else if (configuration.dumpConfig) {
            printConfig();
        } else if (nullOrEmpty(configuration.urls)) {
            printHelp(commandLine);
            println(RED, "Please provide at least ", RED_BOLD, "one URL.");
        } else if (nullOrEmpty(configuration.categorizers) && nullOrEmpty(configuration.stats)) {
            printHelp(commandLine);
            println(RED, "Please provide at least ", RED_BOLD, "one stat", RED, " or ", RED_BOLD, "one categorizer", RED, ".");
        } else {
            return main();
        }

        return null;
    }

    private void printVersion(CommandLine commandLine) {
        commandLine.printVersionHelp(System.out);
    }

    private void printConfig() throws JsonProcessingException {
        System.out.println(SerializationUtils.toYAML(configuration));
    }

    private void printHelp(CommandLine commandLine) {
        commandLine.usage(System.out);
        System.out.println();
        System.out.println("Available categorizers: " + List.of(Categorizers.values()));
        System.out.println("Available stats: " + List.of(Stats.values()));
    }

    public Category main() throws Exception {
        LOG.debugf("Starting main thread");
        // 0.- Prepare inputs
        final Category rootBucket = new Category(configuration, null);

        // 1.- Execute all tasks and gather independent stats.
        LOG.debugf("Starting worker threads");
        executeRequests(rootBucket);

        // 2.- Perform finalization step for stats computation
        LOG.debugf("Computing reesponse stats");
        rootBucket.finalizeStats();

        // 3.- Categorize buckets
        LOG.debugf("Creating category hierarchy");
        rootBucket.categorize(configuration);

        return rootBucket;
    }

    private void executeRequests(final Category rootCategory) throws InterruptedException {
        final HttpClient httpClient = buildHttpClient();
        final ExecutorService pool = Executors.newFixedThreadPool(configuration.threads != null?configuration.threads:Runtime.getRuntime().availableProcessors());

        int repeat;
        int time;
        if (configuration.count==null && configuration.duration==null){
            LOG.warn("No count nor duration provided. No request will be sent.");
            repeat=0;
            time=0;
        } else {
            repeat = configuration.count!=null?configuration.count:Integer.MAX_VALUE;
            time = configuration.duration!=null?configuration.duration:Integer.MAX_VALUE;
        }

        long end = System.currentTimeMillis() + time;
        LOG.warnf("Starting at %d, planned end at %s", System.currentTimeMillis(), end);
        int maxQueueSize=0;
        while (System.currentTimeMillis() < end && repeat-->0) {
            int queueSize = ((ThreadPoolExecutor) pool).getQueue().size();
            maxQueueSize=Math.max(maxQueueSize, queueSize);
            if (queueSize>1000) {
                LOG.warnf("Queue size too big! %d", queueSize);
                Thread.sleep(100);
            }
            pool.submit(new Task(rootCategory, configuration, httpClient));
        }
        pool.shutdown();
        long await = Math.max(end - System.currentTimeMillis(), 0);
        LOG.warnf("Shutdown at %s. Awaiting for termination %d millis", System.currentTimeMillis(), await);
        if (!pool.awaitTermination(await, TimeUnit.MILLISECONDS)) {
            LOG.warnf("Termination failed. Shutting down now! %d", System.currentTimeMillis());
            pool.shutdownNow();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }
        LOG.warnf("Stopping at %d. Max queue size: %d", System.currentTimeMillis(), maxQueueSize);
    }

    private HttpClient buildHttpClient() {
        LOG.debug("Constructing HTTP client");
        final Builder httpClientBuilder = HttpClient.newBuilder();

        if (configuration.httpVersion != null)
            httpClientBuilder.version(configuration.httpVersion);
        if (configuration.proxy != null)
            httpClientBuilder.proxy(ProxySelector.of(configuration.proxy));
        if (configuration.connectTimeout != null)
            httpClientBuilder.connectTimeout(Duration.ofMillis(configuration.connectTimeout));
        if (configuration.redirect != null)
            httpClientBuilder.followRedirects(configuration.redirect);

        return httpClientBuilder.build();
    }
}
