package org.someth2say.storm;

import static org.someth2say.storm.utils.ConsoleColors.RED;
import static org.someth2say.storm.utils.ConsoleColors.RED_BOLD;
import static org.someth2say.storm.utils.ConsoleColors.println;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.configuration.Order;
import org.someth2say.storm.utils.SerializationUtils;

import io.smallrye.mutiny.Multi;

public class Storm {

    private static final Logger LOG = Logger.getLogger(Storm.class);

    public static Category main(Configuration configuration) throws Exception {
        sanityChecks(configuration);
        if (configuration.dumpConfig) {
            System.out.println(SerializationUtils.toYAML(configuration));
        } 

        LOG.debugf("Starting main thread");
        // 0.- Prepare inputs
        final Category rootBucket = new Category(configuration, null);

        // 1.- Execute all tasks and gather independent stats.
        LOG.debugf("Starting worker threads");
        executeRequests(rootBucket, configuration);
        // executeReactiveRequests(rootBucket);

        // 2.- Perform finalization step for stats computation
        LOG.debugf("Computing reesponse stats");
        rootBucket.finalizeStats();

        // 3.- Categorize buckets
        LOG.debugf("Creating category hierarchy");
        rootBucket.categorize(configuration);

        return rootBucket;
    }

    private static void sanityChecks(Configuration configuration) {
        // Sane defaults
        configuration.order = configuration.order != null ? configuration.order : Order.ROUNDROBIN;
        configuration.urls = configuration.urls != null ? configuration.urls : Collections.emptyList();
        configuration.count = configuration.count != null ? configuration.count : 10;

        if (nullOrEmpty(configuration.urls)) {
            println(RED, "Please provide at least ", RED_BOLD, "one URL.");
        } else if (nullOrEmpty(configuration.categorizers) && nullOrEmpty(configuration.stats)) {
            println(RED, "Please provide at least ", RED_BOLD, "one stat", RED, " or ", RED_BOLD, "one categorizer",
                    RED, ".");
        }

    }

    private static boolean nullOrEmpty(final Collection<?> collectio) {
        return collectio == null || collectio.isEmpty();
    }

    private static HttpClient buildHttpClient(Configuration configuration) {
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

    private static void executeRequests(final Category rootCategory, final Configuration configuration)
            throws InterruptedException {
        final HttpClient httpClient = buildHttpClient(configuration);
        final int nThreads = configuration.threads != null ? configuration.threads
                : Runtime.getRuntime().availableProcessors();
        final ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        int repeat;
        int time;
        if (configuration.count == null && configuration.duration == null) {
            LOG.warn("No count nor duration provided. No request will be sent.");
            repeat = 0;
            time = 0;
        } else {
            repeat = configuration.count != null ? configuration.count : Integer.MAX_VALUE;
            time = configuration.duration != null ? configuration.duration : Integer.MAX_VALUE;
        }

        final long end = System.currentTimeMillis() + time;
        LOG.warnf("Starting at %d, planned end at %s", System.currentTimeMillis(), end);
        int maxQueueSize = 0;
        while (System.currentTimeMillis() < end && repeat-- > 0) {
            final int queueSize = ((ThreadPoolExecutor) pool).getQueue().size();
            maxQueueSize = Math.max(maxQueueSize, queueSize);
            if (queueSize > 1000) {
                LOG.warnf("Queue size too big! %d", queueSize);
                Thread.sleep(100);
            }
            pool.submit(new StormCallable(rootCategory, configuration, httpClient));
        }
        pool.shutdown();

        final long await = Math.max(end - System.currentTimeMillis(), 0);
        LOG.warnf("Shutdown at %s. Awaiting for termination %d millis", System.currentTimeMillis(), await);
        if (!pool.awaitTermination(await, TimeUnit.MILLISECONDS)) {
            LOG.warnf("Termination failed. Shutting down now! %d", System.currentTimeMillis());
            pool.shutdownNow();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }
        LOG.warnf("Stopping at %d. Max queue size: %d", System.currentTimeMillis(), maxQueueSize);
    }

    private void executeReactiveRequests(final Category rootCategory, final Configuration configuration) {
        final HttpClient httpClient = buildHttpClient(configuration);
        final int nThreads = configuration.threads != null ? configuration.threads
                : Runtime.getRuntime().availableProcessors();

        int repeat;
        int time;
        if (configuration.count == null && configuration.duration == null) {
            LOG.warn("No count nor duration provided. No request will be sent.");
            repeat = 0;
            time = 0;
        } else {
            repeat = configuration.count != null ? configuration.count : Integer.MAX_VALUE;
            time = configuration.duration != null ? configuration.duration : Integer.MAX_VALUE;
        }

        LOG.warnf("Performing %d request for at most %d millis with %d workers, starting at %d", repeat, time, nThreads,
                System.currentTimeMillis());

        final ArrayList<Boolean> done = new ArrayList<>();
        final long start = System.currentTimeMillis();
        final long end = System.currentTimeMillis() + time;
        // Multi<Task> multi =
        Subscriber<Integer> subs = Multi.createFrom().range(0, repeat)
                // .map(i -> new Task(rootCategory, configuration, httpClient))
                .filter(t -> System.currentTimeMillis() < end).onItem()
                .invoke(i -> LOG.warn(Thread.currentThread().getName() + " Emitting " + i + " at "
                        + (System.currentTimeMillis() - start)))
                .onCompletion().invoke((() -> {
                    LOG.warn("I'm done after " + (System.currentTimeMillis() - start) + "milis");
                    done.add(true);
                })).emitOn(Executors.newFixedThreadPool(nThreads))
                // .runSubscriptionOn(Executors.newFixedThreadPool(nThreads))
                .onOverflow().buffer(1).subscribe().withSubscriber(new Subscriber<Integer>() {
                    Subscription subscription;

                    @Override
                    public void onComplete() {
                        LOG.warn(Thread.currentThread().getName() + " got completion");
                    }

                    @Override
                    public void onError(final Throwable arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onNext(final Integer arg0) {
                        LOG.warn(Thread.currentThread().getName() + " busy (" + arg0 + ") at "
                                + (System.currentTimeMillis() - start));
                        try {
                            Thread.sleep(arg0 * 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LOG.warn(Thread.currentThread().getName() + " now free at "
                                + (System.currentTimeMillis() - start));
                        subscription.request(1);
                    }

                    @Override
                    public void onSubscribe(final Subscription arg0) {
                        LOG.warn(Thread.currentThread().getName() + " Subcribing at "
                                + (System.currentTimeMillis() - start));

                        subscription = arg0;
                        arg0.request(1);
                    }
                })
        /*
         * .with(t -> { LOG.warn("Calling " + t + " on " +
         * Thread.currentThread().getName()); try { Thread.sleep(t); } catch
         * (InterruptedException e) { e.printStackTrace(); } // t.call(); })
         */
        ;

        /*
         * multi.subscribe().with(Task::call); multi.subscribe().with(Task::call);
         */

        try {
            Thread.sleep(10000);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LOG.warnf("Multi completed at %d", System.currentTimeMillis());
    }
}
