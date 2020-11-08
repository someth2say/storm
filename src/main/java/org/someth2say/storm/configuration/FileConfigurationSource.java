package org.someth2say.storm.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import io.smallrye.config.source.yaml.YamlConfigSource;

public class FileConfigurationSource implements ConfigSource {
    private static final Logger LOG = Logger.getLogger(FileConfigurationSource.class);
    private static YamlConfigSource yamlConfigSource;


    public static void init(File configFile, Class<?> configClass) throws IOException {
      yamlConfigSource = new YamlConfigSource("yaml-config-source", new FileInputStream(configFile));
      LOG.debugf("Loaded YAML properties %s",yamlConfigSource.getProperties());
    }

    @Override
    public int getOrdinal() {
        return 400;
    }

    @Override
    public Map<String, String> getProperties() {
        if (yamlConfigSource != null)
            return yamlConfigSource.getProperties();

        return Collections.emptyMap();
    }

    @Override
    public String getValue(String propertyName) {

        String prop = propertyName.startsWith(".")?propertyName.substring(1):propertyName;
        String value = yamlConfigSource!=null?yamlConfigSource.getValue(prop):null;
        //LOG.debugf("Property %s with value %s", propertyName, value);
        return value;
    }

    @Override
    public String getName() {
        return "file-config-source";
    }

    // private static String getConfigPrefixFromConfigClass(final Class<?> commandClass) {
    //     ConfigProperties configAnnotation = commandClass.getAnnotation(ConfigProperties.class);
    //     if (configAnnotation != null) {
    //         String prefix = configAnnotation.prefix();
    //         if (prefix.equals("<< unset >>"))
    //             prefix = "";
    //         LOG.debug("Detected config prefix as '" + prefix  + "'");
    //         return prefix;
    //     }
    //     return "";
    // }
    
}
