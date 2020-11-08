package org.someth2say.storm;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Validator;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.jboss.logging.Logger;
import org.someth2say.storm.category.Categorizers;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.configuration.PicocliConfigSource;
import org.someth2say.storm.stats.Stats;
import org.someth2say.storm.utils.SerializationUtils;

import io.quarkus.runtime.QuarkusApplication;
import picocli.CommandLine;

@Singleton
public class Storm implements QuarkusApplication {

    private static final Logger LOG = Logger.getLogger(Storm.class);

    @Inject
    Configuration configuration;

    @Inject
    Validator validator;

    @Override
    public int run(final String... args) throws Exception {
        Category rootCategory = runAsCategory(args);
        if (rootCategory == null) {
            return -1;
        }
        System.out.println(rootCategory);
        return 0;
    }

    public Category runAsCategory(String... args) throws Exception {

        CommandLine commandLine = PicocliConfigSource.commandLine;
        if (commandLine.isUsageHelpRequested()) {
            printHelp(commandLine);
        } else if (commandLine.isVersionHelpRequested()) {
            printVersion(commandLine);
        } else if (configuration.dumpConfig) {
            printConfig();
        } else if (configuration.urls == null || configuration.urls.isEmpty()) {
            printHelp(commandLine);
            System.out.println("Please provide at least one URL.");
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

    // private void validateConfiguration() {
    // final Set<ConstraintViolation<Configuration>> violations =
    // validator.validate(configuration);
    // if (!violations.isEmpty()) {
    // violations.stream().map(v -> v.getPropertyPath() + " " +
    // v.getMessage()).forEach(LOG::error);
    // throw new RuntimeException();
    // }
    // }

    public Category main() throws Exception {

        // 0.- Prepare inputs
        final Category rootBucket = new Category(configuration, null);

        // 1.- Execute all tasks and gather independent stats.
        executeRequests(rootBucket);

        // 2.- Perform finalization step for stats computation
        rootBucket.finalizeStats();

        // 3.- Categorize buckets
        rootBucket.categorize(configuration);

        return rootBucket;
    }

    private void executeRequests(final Category rootCategory) throws InterruptedException {
        final HttpClient httpClient = buildHttpClient();
        final ExecutorService pool = configuration.threads != null ? Executors.newFixedThreadPool(configuration.threads)
                : Executors.newCachedThreadPool();
        Integer repeat = configuration.repeat!=null?configuration.repeat:1;
        for (int exec = 0; exec < repeat; exec++) {
            pool.submit(new Task(rootCategory, configuration, httpClient));
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
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
