package org.somet2say.flare.serialization;

import java.io.IOException;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class TemporalSerializer extends StdSerializer<Temporal> {
     /**
         *
         */
        private static final long serialVersionUID = 2314074059146769767L;

        public TemporalSerializer() {
            this(null);
        }

        public TemporalSerializer(Class<Temporal> t) {
            super(t);
        }

        @Override
        public void serialize(Temporal value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
}
