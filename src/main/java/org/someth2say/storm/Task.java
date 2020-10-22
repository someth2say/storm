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
import java.util.concurrent.Callable;

import org.jboss.logging.Logger;
import org.someth2say.storm.configuration.Configuration;

final class Task implements Callable<ResponseData<String>> {
	private static final Logger LOG = Logger.getLogger(Task.class);

	private Configuration configuration;
	private Category bucket;

	public Task(final Category rootBucket, final Configuration configuration) {
		this.bucket = rootBucket;
		this.configuration = configuration;
	}

	@Override
	public ResponseData<String> call() {
		final HttpClient httpClient = buildHttpClient();
		final URI nextURL = configuration.order.getNextURL(configuration.urls, configuration.repeat);

		final HttpRequest request = createRequest(nextURL);

		ResponseData<String> responseData = executeRequest(httpClient, request);

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
		final ResponseData<String> responseData = new ResponseData<String>(request, response, startTime, endTime, exception);
		LOG.debugf("Request to %s completed", request.uri());

		return responseData;
	}

	private HttpRequest createRequest(final URI uri) {

		// String headers;
		// Duration requestTimeout;
		HttpRequest request;
		request = HttpRequest.newBuilder().uri(uri)
				// .headers(headers)
				// .timeout(requestTimeout)
				.GET().build();

		return request;
	}

	private HttpClient buildHttpClient() {
		LOG.debug("Constructing HTTP client");
		//final Duration connectTimeout = Duration.ofSeconds(20);
		// Executor executor;

		final ProxySelector proxy = ProxySelector.of(new InetSocketAddress("proxy.example.com", 80));
		final Version httpVersion = Version.HTTP_2;

		final HttpClient httpClient = HttpClient.newBuilder().version(httpVersion)
				//.connectTimeout(connectTimeout)
				// .executor(executor)
				.followRedirects(Redirect.NORMAL)
				// .proxy(proxy)
				.build();
		return httpClient;
	}
}