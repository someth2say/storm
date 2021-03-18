package org.someth2say.storm.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class HttpBinContainerTestResourceLifeCycleManager implements QuarkusTestResourceLifecycleManager {

    public static final String IP = "httpbin.address";
    public static final String PORT = "httpbin.port";

    private static GenericContainer<?> httpBinContainer = new GenericContainer<>("kennethreitz/httpbin").withExposedPorts(80);

    @Override
    public Map<String, String> start() {
        httpBinContainer.start();
        return  Collections.unmodifiableMap(Map.of(
            PORT, httpBinContainer.getMappedPort(80).toString(),
            IP, httpBinContainer.getContainerIpAddress())
        );
    }

    @Override
    public void stop() {
        httpBinContainer.stop();
    }

    public static URI getURI(final String scheme, final String path) {
        final String host = httpBinContainer.getContainerIpAddress();// System.getProperty(HttpBinContainerTestResourceLifeCycleManager.IP);
        final int port = httpBinContainer.getMappedPort(80); // Integer.valueOf(System.getProperty(HttpBinContainerTestResourceLifeCycleManager.PORT));
        try {
            final String userInfo = null;
            final String query = null;
            final String fragment = null;

            return new URI(scheme, userInfo, host, port, path, query, fragment);

        } catch (final URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
