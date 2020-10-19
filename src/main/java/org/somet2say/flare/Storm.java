package org.somet2say.flare;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.somet2say.flare.category.Categorizers;
import org.somet2say.flare.configuration.Configuration;
import org.somet2say.flare.serialization.SerializationUtils;
import org.somet2say.flare.stats.Stats;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.runtime.configuration.QuarkusConfigFactory;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.config.SmallRyeConfigFactory;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@QuarkusMain
public class Storm implements QuarkusApplication {

    @Inject
    Configuration configuration;

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) throws Exception {
        try {
            CommandLine commandLine = new CommandLine(configuration, factory);
            ParseResult parseResults = commandLine.parseArgs(args);
            if (!parseResults.errors().isEmpty()) {
                System.err.println(parseResults.errors());
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

        } catch (ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            return -1;
        }
        return main();
    }

    public int main() throws Exception {

        // 0.- Prepare inputs
        Category rootBucket = new Category(configuration, null);

        // 1.- Execute all tasks and gather independent stats.
        executeRequests(rootBucket);
        rootBucket.finalizeStats();

        // 2.- Categorize buckets
        rootBucket.categorize(configuration);

        // 3.- Dump output
        System.out.println(rootBucket);
        return 0;
    }

    private void executeRequests(Category rootBucket) throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(configuration.threads);
        Collection<ForkJoinTask<ResponseData<String>>> fjTasks = new HashSet<>();
        for (int exec = 0; exec < configuration.repeat; exec++) {
            Task task = new Task(rootBucket, configuration);
            var fjTask = pool.submit(task);
            fjTasks.add(fjTask);
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
    }
}
