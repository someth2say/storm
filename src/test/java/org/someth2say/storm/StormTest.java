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

        // This will only work in JVM mode.
        Storm storm = Arc.container().instance(Storm.class).get();
        storm.run(uri.toString());
    }

    @Test
    public void runAsCategoryTest() throws Exception {

        URI uri = HttpBinContainerTestResourceLifeCycleManager.getURI("http", "/status/200");

        // This will only work in JVM mode.
        Storm storm = Arc.container().instance(Storm.class).get();
        Category result = storm.runAsCategory(uri.toString());
        System.out.println(result);
    }
}
