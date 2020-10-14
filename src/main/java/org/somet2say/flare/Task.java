package org.somet2say.flare;

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

import org.somet2say.flare.configuration.Configuration;

final class Task implements Callable<ResponseData<String>> {

	private Configuration configuration;
	private Bucket bucket;
	private static AtomicInteger requestCounter = new AtomicInteger();


	public Task(final Bucket rootBucket, final Configuration configuration) {
		this.bucket = rootBucket;
		this.configuration = configuration;
	}

	@Override
	public ResponseData<String> call() {
		final HttpClient httpClient = buildHttpClient();

		ResponseData<String> responseData;
		final URI nextURL = getNextURL();
		final HttpRequest request = createRequest(nextURL);
		responseData = createResponseData(httpClient, request);

		bucket.addResponse(responseData);

		return responseData;
	}

	private ResponseData<String> createResponseData(final HttpClient httpClient, final HttpRequest request) {
		final Instant startTime = Instant.now();
		Instant endTime;
		HttpResponse<String> response = null;
		Exception exception = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			exception = e;
		} finally {
			endTime = Instant.now();
		}

		final ResponseData<String> responseData = new ResponseData<String>(response, startTime, endTime, exception);
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
		final List<URI> urls = configuration.getUrls();
		int index;
		int nextCounter = requestCounter.getAndIncrement();
		switch (configuration.getOrder()) {
			case roundrobin:
				 index = nextCounter % urls.size();
				 break;
			case sequential:
				 double step = ((double)configuration.getRepeat() / urls.size());
				 index = (int)(nextCounter / step);
				 break;
			default:
			case random:
				 index = new Random().nextInt(urls.size());
				 break;
		}
		return urls.get(index);
	}
}