package org.someth2say.storm;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.List;

import com.github.tomakehurst.wiremock.WireMockServer;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.someth2say.storm.category.CategorizerBuilder;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.Configuration;

public class StormTest {

  private static WireMockServer wireMockServer;

  @BeforeAll
  public static void initWiremock() {
    wireMockServer = new WireMockServer();
    wireMockServer.start();
  }

  @AfterAll
  public static void stopWireMock() {
    if (null != wireMockServer) {
      wireMockServer.stop();
    }
  }

  @Test
  public void defaultConfigAllGood() throws Exception {

    /*
     * stubFor(get(urlEqualTo("/v2/name/GR"))
     * .willReturn(aResponse().withHeader("Content-Type", "application/json")
     * .withBody("[{" + "\"name\": \"Ελλάδα\"," + "\"capital\": \"Αθήνα\"" +
     * "}]")));
     * 
     * stubFor(get(urlMatching(".*")).atPriority(10).willReturn(aResponse().
     * proxiedFrom("https://restcountries.eu/rest")));
     */

    // URI wiremockURI =
    // ConfigProvider.getConfig().getValue(WiremockTestResourceLifeCycleManager.WIREMOCK_BASE_URL,
    // URI.class);
    URI wiremockURI = new URIBuilder(wireMockServer.baseUrl()).build();
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
    stubFor(get(statusURI.getPath()).willReturn(ok()));

    Configuration configuration = new Configuration();
    configuration.urls = List.of(statusURI);
    configuration.categorizerBuilderParams=List.of(CategorizerBuilder.HTTPCODE.name());

    Category category = Storm.main(configuration);
    assertNotNull(category);

  }

}
