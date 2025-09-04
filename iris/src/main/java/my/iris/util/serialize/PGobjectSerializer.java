package my.iris.util.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.postgresql.util.PGobject;

import java.io.IOException;

public class PGobjectSerializer extends JsonSerializer<PGobject> {
    @Override
    public void serialize(PGobject pGobject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(pGobject.getValue());
    }
}
