package org.someth2say.storm;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jboss.logging.Logger;
import org.someth2say.storm.category.Categorizers;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.serialization.SerializationUtils;
import org.someth2say.storm.stats.Stats;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@QuarkusMain
public class Storm implements QuarkusApplication {

    private static final Logger LOG = Logger.getLogger(Storm.class);

    @Inject
    Configuration configuration;

    @Inject
    Validator validator;

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(final String... args) throws Exception {
        CommandLine commandLine;
        try {
            commandLine = parseCommanLine(args);
            validateConfiguration();
        } catch (final Exception e) {
            return -1;
        }

        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            System.out.println();
            System.out.println("Available categorizers: " + List.of(Categorizers.values()));
            System.out.println("Available stats: " + List.of(Stats.values()));
            return 0;
        } else if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return 0;
        } else if (configuration.dumpConfig) {
            System.out.println(SerializationUtils.toYAML(configuration));
            return 0;
        }

        return main();
    }

    private void validateConfiguration() {
        final Set<ConstraintViolation<Configuration>> violations = validator.validate(configuration);
        if (!violations.isEmpty()) {
            violations.stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).forEach(LOG::error);
            throw new RuntimeException();
        }
    }

    private CommandLine parseCommanLine(final String... args) {
        CommandLine commandLine;
        commandLine = new CommandLine(configuration, factory);

        ParseResult parseResults;
        try {
            parseResults = commandLine.parseArgs(args);
            if (!parseResults.errors().isEmpty()) {
                parseResults.errors().forEach(e -> LOG.error(e));
                throw new RuntimeException();
            }

        } catch (final ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            throw new RuntimeException();
        }
        return commandLine;
    }

    public int main() throws Exception {

        // 0.- Prepare inputs
        final Category rootBucket = new Category(configuration, null);

        // 1.- Execute all tasks and gather independent stats.
        executeRequests(rootBucket);
        rootBucket.finalizeStats();

        // 2.- Categorize buckets
        rootBucket.categorize(configuration);

        // 3.- Dump output
        System.out.println(rootBucket);
        return 0;
    }

    private void executeRequests(final Category rootCategory) throws InterruptedException {
        final HttpClient httpClient = buildHttpClient();
        final ExecutorService pool = Executors.newFixedThreadPool(configuration.threads);
        for (int exec = 0; exec < configuration.repeat; exec++) {
            pool.submit(new Task(rootCategory, configuration, httpClient));
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
    }

    private HttpClient buildHttpClient() {
		LOG.debug("Constructing HTTP client");
		final Version httpVersion = Version.HTTP_2;
		final Builder httpClientBuilder = HttpClient.newBuilder();
		configuration.proxy.ifPresent(proxyAddress->httpClientBuilder.proxy(ProxySelector.of(proxyAddress)));
		configuration.connectTimeout.ifPresent(timeout->httpClientBuilder.connectTimeout(Duration.ofMillis(timeout)));
		configuration.redirect.ifPresent(redirect->httpClientBuilder.followRedirects(redirect));

		final HttpClient httpClient = httpClientBuilder.version(httpVersion).build();
		return httpClient;
	}
}
