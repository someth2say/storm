package org.someth2say.storm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.someth2say.storm.configuration.Configuration;

final class Task implements Callable<ResponseData<String>> {
	private static final Logger LOG = Logger.getLogger(Task.class);

	private Configuration configuration;
	private Category bucket;
	private static AtomicInteger requestCounter = new AtomicInteger();

	public Task(final Category rootBucket, final Configuration configuration) {
		this.bucket = rootBucket;
		this.configuration = configuration;
	}

	@Override
	public ResponseData<String> call() {
		final HttpClient httpClient = buildHttpClient();
		final URI nextURL = getNextURL();

		final HttpRequest request = createRequest(nextURL);

		ResponseData<String> responseData = createResponseData(httpClient, request);

		bucket.addResponse(responseData);

		performDelay();

		return responseData;
	}

	private void performDelay() {
		if (this.configuration.delay > 0) {
			try {
				Thread.sleep(this.configuration.delay);
			} catch (InterruptedException e) {
			}
		}
	}

	private ResponseData<String> createResponseData(final HttpClient httpClient, final HttpRequest request) {
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
		final ResponseData<String> responseData = new ResponseData<String>(request, response, startTime, endTime, exception);
		LOG.debugf("Request to %s completed", request.uri());

		return responseData;
	}

	private HttpRequest createRequest(final URI nextURL) {

		// String headers;
		// Duration requestTimeout;
		HttpRequest request;
		request = HttpRequest.newBuilder().uri(nextURL)
				// .headers(headers)
				// .timeout(requestTimeout)
				.GET().build();

		return request;
	}

	private HttpClient buildHttpClient() {
		final Duration connectTimeout = Duration.ofSeconds(20);
		// Executor executor;

		final ProxySelector proxy = ProxySelector.of(new InetSocketAddress("proxy.example.com", 80));
		final Version httpVersion = Version.HTTP_1_1;

		final HttpClient httpClient = HttpClient.newBuilder().version(httpVersion).connectTimeout(connectTimeout)
				// .executor(executor)
				.followRedirects(Redirect.NORMAL)
				// .proxy(proxy)
				.build();
		return httpClient;
	}

	private URI getNextURL() {
		final List<URI> urls = configuration.urls;
		int index;
		int nextCounter = requestCounter.getAndIncrement();
		switch (configuration.order) {
			case roundrobin:
				index = nextCounter % urls.size();
				break;
			case sequential:
				double step = ((double) configuration.repeat / urls.size());
				index = (int) (nextCounter / step);
				break;
			default:
			case random:
				index = new Random().nextInt(urls.size());
				break;
		}
		return urls.get(index);
	}
}