package org.someth2say.storm.configuration;

import org.junit.jupiter.api.Test;

public class ConfigurationTest {

 
  @Test
  public void dumpDefaultConfig() throws Exception {

    StormConfiguration configuration = new StormConfiguration();    
    configuration.dumpConfig=true;

    System.out.println(configuration);  

  }
}
