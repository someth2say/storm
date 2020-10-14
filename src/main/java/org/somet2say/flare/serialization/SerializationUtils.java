package org.somet2say.flare.serialization;

import java.time.Duration;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class SerializationUtils {

	public static String toYAML(final Object obj) {
	    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	    mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_ABSENT);
		mapper.setSerializationInclusion(Include.NON_NULL);
	
	    SimpleModule module = new SimpleModule();
		module.addSerializer(Temporal.class, new TemporalSerializer());
		module.addSerializer(Duration.class, new DurationSerializer());
	    mapper.registerModule(module);
	
	    try {
	        return mapper.writeValueAsString(obj);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }
	    return "ups";
	}
    
}
