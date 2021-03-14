package org.someth2say.storm;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.Wire;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.someth2say.storm.category.Categorizers;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.stat.Stats;

import io.quarkus.arc.Arc;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import static com.github.tomakehurst.wiremock.client.WireMock.*; 

@QuarkusTest
//@QuarkusTestResource(HttpBinContainerTestResourceLifeCycleManager.class)
@QuarkusTestResource(WiremockTestResourceLifeCycleManager.class)
public class StormTest {

    @Test
    public void containerStartTest() throws Exception {

/*      stubFor(get(urlEqualTo("/v2/name/GR"))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withBody("[{" + "\"name\": \"Ελλάδα\"," + "\"capital\": \"Αθήνα\"" + "}]")));

        stubFor(get(urlMatching(".*")).atPriority(10)
        .willReturn(aResponse().proxiedFrom("https://restcountries.eu/rest"))); */
        
        URI wiremockURI = ConfigProvider.getConfig().getValue(WiremockTestResourceLifeCycleManager.WIREMOCK_BASE_URL, URI.class);
        URI statusURI = new URIBuilder(wiremockURI).setPath("/status").build();
        
        stubFor(get(statusURI.getPath()).willReturn(aResponse().withStatus(200)));
        
        Configuration configuration = new Configuration();
        configuration.urls=List.of(statusURI);
        configuration.categorizers=List.of(Categorizers.HTTPCODE.name());
        configuration.stats=List.of(Stats.COUNT.name(), Stats.URLS.name());
        //configuration.dumpConfig=true;
        Category category = Storm.main(configuration);
        assertNotNull(category);
        System.out.println(category);
      
    }
}
