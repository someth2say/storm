package org.someth2say.storm;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.someth2say.storm.configuration.StormConfiguration;

public class RequestBuilder {
	private static final Logger LOG = Logger.getLogger(RequestBuilder.class);

    private final StormConfiguration configuration;
    private final AtomicInteger requestCounter = new AtomicInteger();
    private final Map<URI, HttpRequest.Builder> builderCache;

    public RequestBuilder(final StormConfiguration configuration) {
        this.configuration = configuration;
        builderCache = new ConcurrentHashMap<>(configuration.urls.size());
    }

    public RequestData getNextRequest() {

        List<URI> urls = configuration.urls;
        int count = requestCounter.getAndIncrement();
        double repeat = configuration.count;
        URI nextURL = this.configuration.order.getNextURI(urls, repeat, count);

        HttpRequest.Builder httpRequestBuilder = builderCache.computeIfAbsent(nextURL, this::buildHttpRequestBuilder);
        RequestData requestData = new RequestData(count, httpRequestBuilder.build());
        return requestData;
    }

    private HttpRequest.Builder buildHttpRequestBuilder(final URI uri) {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        if (configuration.requestTimeout != null)
            httpRequestBuilder.timeout(Duration.ofMillis(configuration.requestTimeout));
        
        httpRequestBuilder
            .uri(uri)
            // .headers(headers)  // TODO: configure headers
            .GET();               // TODO: configure methods 
        return httpRequestBuilder;
    }

}
