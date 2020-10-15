package org.somet2say.flare;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.somet2say.flare.configuration.Configuration;
import org.somet2say.flare.serialization.SerializationUtils;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Flare implements QuarkusApplication {

    @Inject
    Configuration configuration;

    @Override
    public int run(String... args) throws Exception {

        System.out.println(SerializationUtils.toYAML(configuration));

        // 0.- Prepare inputs
        Bucket rootBucket = new Bucket(configuration);

        // 1.- Execute all tasks and gather independent stats.
        executeRequests(rootBucket);

        // 2.- Categorize buckets
        rootBucket.categorize(configuration);

        // 3.- Dump output
        System.out.println(rootBucket);
        return 0;
    }

    private void executeRequests(Bucket rootBucket) throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(configuration.threads);
        System.out.println("Pool size: " + pool.getParallelism());
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
