package org.someth2say.storm.serialization;

import java.time.Duration;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class SerializationUtils {

	

	public static String toYAML(final Object obj) {
		YAMLFactory yamlFactory = new YAMLFactory();
		yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES); //removes quotes from strings
		yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);//gets rid of -- at the start of the file.

		ObjectMapper mapper = new ObjectMapper(yamlFactory);
	    mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_ABSENT);
		mapper.setSerializationInclusion(Include.NON_NULL);
	
	    SimpleModule module = new SimpleModule();
		module.addSerializer(Temporal.class, new ToStringSerializer());
		module.addSerializer(Duration.class, new ToStringSerializer());
	    mapper.registerModule(module);
	
	    try {
	        return mapper.writeValueAsString(obj);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }
	    return "ups";
	}
    
}
