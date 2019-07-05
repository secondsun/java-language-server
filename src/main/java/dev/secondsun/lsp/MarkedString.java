package dev.secondsun.lsp;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;


public class MarkedString {
    public String language, value;

    public MarkedString() {}

    public MarkedString(String value) {
        this.value = value;
    }

    public MarkedString(String language, String value) {
        this.language = language;
        this.value = value;
    }

    public static class Adapter  implements JsonbDeserializer<MarkedString>, JsonbSerializer<MarkedString> {

        public static Adapter INSTANCE = new Adapter();

        @Override
        public MarkedString deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            throw new UnsupportedOperationException("Deserializing MarkedString's is unsupported.");
        }

        @Override
        public void serialize(MarkedString markedString, JsonGenerator out, SerializationContext ctx) {
            if (markedString.language == null) {
                out.write(markedString.value);
            } else {
                out.writeStartObject();
                out.write("language", markedString.language);
                out.write("value", markedString.value);
                out.writeEnd();
            }
        }
    }
}
