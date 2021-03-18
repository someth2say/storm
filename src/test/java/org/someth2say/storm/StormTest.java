package org.someth2say.storm;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.WireMockServer;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.someth2say.storm.category.CategorizerIndex;
import org.someth2say.storm.category.CategorizerIndex.CategorizerBuilderParams;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.StormConfiguration;
import org.someth2say.storm.stat.StatIndex;
import org.someth2say.storm.stat.StatIndex.StatBuilderParams;

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
  public void singleCategorizer() throws Exception {

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
    URI errorURI = new URIBuilder(wiremockURI).setPath("/error").build();
    stubFor(get(errorURI.getPath()).willReturn(serverError()));

    StormConfiguration configuration = new StormConfiguration();
    configuration.urls = List.of(statusURI, errorURI);
    configuration.categorizers=List.of(new CategorizerBuilderParams(CategorizerIndex.HTTPCODE));

    Category category = Storm.main(configuration);
    assertNotNull(category);
    System.out.println(category);

  }

  @Test
  public void parameterizedCategorizer() throws Exception {

    URI wiremockURI = new URIBuilder(wireMockServer.baseUrl()).build();
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
    stubFor(get(statusURI.getPath()).willReturn(ok()));

    StormConfiguration configuration = new StormConfiguration();
    configuration.urls = List.of(statusURI);
    configuration.count=1000;
    configuration.categorizers=List.of(new CategorizerBuilderParams(CategorizerIndex.TIMEHISTOGRAM,"1"));
    configuration.stats = List.of(new StatBuilderParams(StatIndex.COUNT));
    Category category = Storm.main(configuration);
    assertNotNull(category);
    System.out.println(category);

  }

  @Test
  public void singleStat() throws Exception {

    URI wiremockURI = new URIBuilder(wireMockServer.baseUrl()).build();
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
    stubFor(get(statusURI.getPath()).willReturn(ok()));

    StormConfiguration configuration = new StormConfiguration();
    configuration.urls = List.of(statusURI);
    configuration.stats = List.of(new StatBuilderParams(StatIndex.COUNT));
    Category category = Storm.main(configuration);
    assertNotNull(category);
    System.out.println(category);

  }

  @Test
  public void singleStatWithParams() throws Exception {

    URI wiremockURI = new URIBuilder(wireMockServer.baseUrl()).build();
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
    stubFor(get(statusURI.getPath()).willReturn(ok()));

    StormConfiguration configuration = new StormConfiguration();
    configuration.count=1000;
    configuration.urls = List.of(statusURI);
    configuration.stats = List.of(new StatBuilderParams(StatIndex.DURATION));
    Category category = Storm.main(configuration);
    assertNotNull(category);

  }

  @Test
  public void severalStatsWithSeveralValues() throws Exception {

    URI wiremockURI = new URIBuilder(wireMockServer.baseUrl()).build();
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
    stubFor(get(statusURI.getPath()).willReturn(ok()));

    StormConfiguration configuration = new StormConfiguration();
    configuration.urls = List.of(statusURI);
    configuration.stats = List.of(
      new StatBuilderParams(StatIndex.TIME), 
      new StatBuilderParams(StatIndex.DURATION),
      new StatBuilderParams(StatIndex.ID));
    Category category = Storm.main(configuration);
    assertNotNull(category);
    System.out.println(category);

  }


  @Test
  public void allStats() throws Exception {

    URI wiremockURI = new URIBuilder(wireMockServer.baseUrl()).build();
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
    stubFor(get(statusURI.getPath()).willReturn(ok()));

    StormConfiguration configuration = new StormConfiguration();
    configuration.urls = List.of(statusURI);
    configuration.stats = List.of(StatIndex.values()).stream().map(sb->new StatBuilderParams(sb)).collect(Collectors.toList());
    Category category = Storm.main(configuration);

    assertNotNull(category);
    System.out.println(category);

  }

  @Test
  public void twoURLs() throws Exception {

    URI wiremockURI = new URIBuilder(wireMockServer.baseUrl()).build();
    URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
    URI errorURI = new URIBuilder(wiremockURI).setPath("/error").build();
    stubFor(get(statusURI.getPath()).willReturn(ok()));
    stubFor(get(errorURI.getPath()).willReturn(serverError()));

    StormConfiguration configuration = new StormConfiguration();
    configuration.urls = List.of(statusURI, errorURI);
    configuration.stats = List.of(StatIndex.values()).stream().map(sb->new StatBuilderParams(sb)).collect(Collectors.toList());
    Category category = Storm.main(configuration);
    assertNotNull(category);
    System.out.println(category);

  }


}
