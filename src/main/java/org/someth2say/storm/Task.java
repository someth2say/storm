package org.someth2say.storm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.jboss.logging.Logger;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.configuration.Order;

final class Task implements Callable<ResponseData<String>> {
	private static final Logger LOG = Logger.getLogger(Task.class);

	private final Configuration configuration;
	private final Category bucket;
	private final HttpClient httpClient;

	public Task(final Category rootBucket, final Configuration configuration, final HttpClient httpClient) {
		this.bucket = rootBucket;
		this.configuration = configuration;
		this.httpClient = httpClient;
	}

	@Override
	public ResponseData<String> call() {
		Order order = configuration.order != null ? configuration.order : Order.ROUNDROBIN;
		List<URI> urls = configuration.urls != null ? configuration.urls : Collections.emptyList();
		int repeat = configuration.repeat != null ? configuration.repeat : 1;
		final URI nextURL = order.getNextURL(urls, repeat);

		final HttpRequest request = createRequest(nextURL);

		final ResponseData<String> responseData = executeRequest(httpClient, request);

		bucket.addResponse(responseData);

		performDelay();

		return responseData;
	}

	private void performDelay() {
		if (configuration.delay != null) {
			try {
				Thread.sleep(this.configuration.delay);
			} catch (final InterruptedException e) {
			}
		}
	}

	private ResponseData<String> executeRequest(final HttpClient httpClient, final HttpRequest request) {
		LOG.debugf("Performing request to %s", request.uri());
		final Instant startTime = Instant.now();
		Instant endTime;
		HttpResponse<String> response = null;
		Exception exception = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			exception = e;
			LOG.debugf("Request to %s failed with message %s", request.uri(), e);
		} finally {
			endTime = Instant.now();
		}
		final ResponseData<String> responseData = new ResponseData<String>(request, response, startTime, endTime,
				exception);
		LOG.debugf("Request to %s completed", request.uri());

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