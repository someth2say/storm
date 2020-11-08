package org.someth2say.storm;

import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import io.quarkus.arc.Arc;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(HttpBinContainerTestResourceLifeCycleManager.class)
public class StormTest {

    @Test
    public void containerStartTest() throws Exception {

        URI uri = HttpBinContainerTestResourceLifeCycleManager.getURI("http", "/status/200");

        //TODO: Actually start Quarkus in command mode.
      
    }
}
