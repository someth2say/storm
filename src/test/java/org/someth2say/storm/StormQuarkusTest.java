package org.someth2say.storm;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.someth2say.storm.configuration.StormConfiguration;
import org.someth2say.storm.resources.HttpBinContainerTestResourceLifeCycleManager;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(HttpBinContainerTestResourceLifeCycleManager.class)
public class StormQuarkusTest {

  @Inject
  StormConfiguration configuration;

  @Inject
  StormQuarkusApplication storm;

  @Test
  public void quarkusDefaultConfigAllGood() throws Exception {

    //URI httpBinURI = HttpBinContainerTestResourceLifeCycleManager.getURI("https", "/");
    //URI getURI = new URIBuilder(httpBinURI).setPath("/get").build();
    System.out.println(configuration);
    storm.run();
  }
}
