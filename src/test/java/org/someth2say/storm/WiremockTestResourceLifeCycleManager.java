package org.someth2say.storm;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockTestResourceLifeCycleManager implements QuarkusTestResourceLifecycleManager {

	public static final String WIREMOCK_BASE_URL = "wiremock.baseUrl";
	private WireMockServer wireMockServer;

	@Override
	public Map<String, String> start() {
		wireMockServer = new WireMockServer();
		wireMockServer.start();
		return Collections.singletonMap(WIREMOCK_BASE_URL, wireMockServer.baseUrl());
	}

	@Override
	public void stop() {
		if (null != wireMockServer) {
			wireMockServer.stop();
		}
	}
}