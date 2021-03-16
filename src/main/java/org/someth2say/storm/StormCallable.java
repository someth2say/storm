package org.someth2say.storm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.StormConfiguration;

public final class StormCallable implements Callable<ResponseData> {
	private static final Logger LOG = Logger.getLogger(StormCallable.class);
    private final static AtomicInteger requestCounter = new AtomicInteger();

	private final StormConfiguration configuration;
	private final Category category;
	private final HttpClient httpClient;

	public StormCallable(final Category rootBucket, final StormConfiguration configuration, final HttpClient httpClient) {
		this.category = rootBucket;
		this.configuration = configuration;
		this.httpClient = httpClient;
	}

	@Override
	public ResponseData call() {
		try {

			int count = requestCounter.getAndIncrement();
			final URI nextURL = configuration.order.getNextURL(configuration.urls, configuration.count, count);

			final HttpRequest request = createRequest(nextURL);

			final ResponseData responseData = executeRequest(httpClient, request, count);

			category.addResponse(responseData);

			performDelay();

			return responseData;
		} catch (InterruptedException e){
			LOG.warnf("Tread %s interrupted", Thread.currentThread().getName());
			return null;
		}
	}

	private void performDelay() throws InterruptedException {
		if (configuration.delay != null) {
			Thread.sleep(this.configuration.delay);
		}
	}

	private ResponseData executeRequest(final HttpClient httpClient, final HttpRequest request,
			int count) {
		LOG.debugf("Performing request %010d to %s", count, request.uri());
		final Instant startTime = Instant.now();
		Instant endTime;
		HttpResponse<String> response = null;
		Exception exception = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			exception = e;
			LOG.debugf("Request %010d to %s failed with message %s", count, request.uri(), e);
		} finally {
			endTime = Instant.now();
		}
		final ResponseData responseData = new ResponseData(request, response, startTime, endTime,
				exception, count);

		LOG.debugf("Request %010d to %s completed", count, request.uri());

		return responseData;
	}

	private HttpRequest createRequest(final URI uri) {

		// String headers;
		final java.net.http.HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

		if (configuration.requestTimeout != null)
			requestBuilder.timeout(Duration.ofMillis(configuration.requestTimeout));
		return requestBuilder.uri(uri)
				// .headers(headers)
				.GET().build();
	}

}