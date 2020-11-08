package org.someth2say.storm.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

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

		Jdk8Module jdk8Module = new Jdk8Module();
		jdk8Module.configureAbsentsAsNulls(true);
		mapper.registerModule(jdk8Module);

		final SimpleModule module = new SimpleModule();
		module.addSerializer(Temporal.class, ToStringSerializer.instance);
		module.addSerializer(Duration.class, ToStringSerializer.instance);
		mapper.registerModule(module);
	}

	public static String toYAML(final Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	public static <T> T fromYAML(final String yaml, Class<T> valueType) throws IOException {
		return mapper.createParser(yaml).readValueAs(valueType);
	}

	public static <T> T fromYAML(final InputStream yaml, Class<T> valueType) throws IOException {
		return mapper.createParser(yaml).readValueAs(valueType);
	}
}
