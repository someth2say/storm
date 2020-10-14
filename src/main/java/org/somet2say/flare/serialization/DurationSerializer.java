package org.somet2say.flare.serialization;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DurationSerializer extends StdSerializer<Duration> {
     /**
         *
         */
        private static final long serialVersionUID = 2314074059146769767L;

        public DurationSerializer() {
            this(null);
        }

        public DurationSerializer(Class<Duration> t) {
            super(t);
        }

        @Override
        public void serialize(Duration value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
}
