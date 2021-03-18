package org.someth2say.storm;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;

import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.Test;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.StormConfiguration;
import org.someth2say.storm.resources.HttpBinContainerTestResourceLifeCycleManager;
import org.someth2say.storm.resources.WiremockTestResourceLifeCycleManager;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockTestResourceLifeCycleManager.class)
@QuarkusTestResource(HttpBinContainerTestResourceLifeCycleManager.class)
public class StormQuarkusTest {

  @Inject
  StormConfiguration configuration;

  @Inject
  StormQuarkusApplication storm;

  @Test
  public void quarkusDefaultConfigAllGood() throws Exception {

    /*
     * stubFor(get(urlEqualTo("/v2/name/GR"))
     * .willReturn(aResponse().withHeader("Content-Type", "application/json")
     * .withBody("[{" + "\"name\": \"Ελλάδα\"," + "\"capital\": \"Αθήνα\"" +
     * "}]")));
     * 
     * stubFor(get(urlMatching(".*")).atPriority(10).willReturn(aResponse().
     * proxiedFrom("https://restcountries.eu/rest")));
     */

    URI wiremockURI = ConfigProvider.getConfig()
      .getValue(WiremockTestResourceLifeCycleManager.WIREMOCK_BASE_URL,URI.class);
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();

    stubFor(get(statusURI.getPath()).willReturn(ok()));
    System.out.println(configuration);
    Category category = storm.main();
    assertNotNull(category);
    System.out.println(category);
  }
}
