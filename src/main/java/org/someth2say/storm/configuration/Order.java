package org.someth2say.storm.configuration;

import java.net.URI;
import java.util.List;
import java.util.Random;

public enum Order implements URLProvider {
    RANDOM {
        public URI getNextURI(final List<URI> urls, double repeat, int count) {
            return urls.get(new Random().nextInt(urls.size()));
        }
    },
    SEQUENTIAL {
        public URI getNextURI(final List<URI> urls, double repeat, int count) {
            return urls.get((int) (count / (repeat / urls.size())) % urls.size());
        }
    },
    ROUNDROBIN {
        public URI getNextURI(final List<URI> urls, double repeat, int count) {
            return urls.get(count % urls.size());
        }
    };


}