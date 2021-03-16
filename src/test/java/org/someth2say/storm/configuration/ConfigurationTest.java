package org.someth2say.storm.configuration;

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

public class ConfigurationTest {

 
  @Test
  public void dumpDefaultConfig() throws Exception {

    Configuration configuration = new Configuration();    
    configuration.dumpConfig=true;

    System.out.println(configuration);  

  }
}
