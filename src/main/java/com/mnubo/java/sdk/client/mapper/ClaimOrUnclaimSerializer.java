package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mnubo.java.sdk.client.models.ClaimOrUnclaim;

import java.io.IOException;
import java.util.Map;

public class ClaimOrUnclaimSerializer extends StdSerializer<ClaimOrUnclaim> {

    public ClaimOrUnclaimSerializer() { super(ClaimOrUnclaim.class); }

    @Override
    public void serialize(ClaimOrUnclaim value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField(ClaimOrUnclaim.DEVICE_ID, value.getDeviceId());
        jgen.writeObjectField(ClaimOrUnclaim.USERNAME, value.getUsername());

        if (value.getAttributes() != null) {
            for (Map.Entry<String, Object> entry : value.getAttributes().entrySet()) {
                jgen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }

        jgen.writeEndObject();
    }

}
