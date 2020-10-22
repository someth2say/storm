package org.someth2say.storm.configuration;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public enum Order implements URLProvider {
    RANDOM {
        public URI getNextURL(final List<URI> urls, double repeat) {
            requestCounter.getAndIncrement();
            return urls.get(new Random().nextInt(urls.size()));
        }
    },
    SEQUENTIAL {
        public URI getNextURL(final List<URI> urls, double repeat) {
            return urls.get((int) (requestCounter.getAndIncrement() / (repeat / urls.size())));
        }
    },
    ROUNDROBIN {
        public URI getNextURL(final List<URI> urls, double repeat) {
            return urls.get(requestCounter.getAndIncrement() % urls.size());
        }
    };

    private static AtomicInteger requestCounter = new AtomicInteger();

}