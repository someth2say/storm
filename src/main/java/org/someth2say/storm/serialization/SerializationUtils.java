package org.someth2say.storm.serialization;

import java.io.IOException;
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

	private static final ObjectMapper mapper;

	static {
		final YAMLFactory yamlFactory = new YAMLFactory();

		yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES); // removes quotes from strings
		yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);// gets rid of -- at the start of the file.

		mapper = new ObjectMapper(yamlFactory);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_ABSENT);
		mapper.setSerializationInclusion(Include.NON_NULL);

		final SimpleModule module = new SimpleModule();
		module.addSerializer(Temporal.class, new ToStringSerializer());
		module.addSerializer(Duration.class, new ToStringSerializer());

		mapper.registerModule(module);
	}

	public static String toYAML(final Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	public static <T> T fromAML(final String yaml, Class<T> valueType) throws IOException {
		return mapper.createParser(yaml).readValueAs(valueType);
	}

}
