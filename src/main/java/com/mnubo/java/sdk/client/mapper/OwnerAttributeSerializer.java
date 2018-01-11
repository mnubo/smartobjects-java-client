package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mnubo.java.sdk.client.models.datamodel.OwnerAttribute;

import java.io.IOException;

public class OwnerAttributeSerializer extends StdSerializer<OwnerAttribute> {

    public OwnerAttributeSerializer() { super(OwnerAttribute.class); }

    @Override
    public void serialize(OwnerAttribute value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("displayName", value.getDisplayName());
        jgen.writeStringField("description", value.getDescription());

        jgen.writeObjectFieldStart("type");
        jgen.writeStringField("highLevelType", value.getType());
        jgen.writeStringField("containerType", value.getContainerType());
        jgen.writeEndObject();

        jgen.writeEndObject();
    }

}
