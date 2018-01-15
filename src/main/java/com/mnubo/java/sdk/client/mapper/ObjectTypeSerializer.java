package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mnubo.java.sdk.client.models.datamodel.ObjectType;

import java.io.IOException;

public class ObjectTypeSerializer extends StdSerializer<ObjectType> {

    public ObjectTypeSerializer() { super(ObjectType.class); }

    @Override
    public void serialize(ObjectType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("description", value.getDescription());

        jgen.writeArrayFieldStart("objectAttributesKeys");
        for (String key : value.getObjectAttributeKeys()) {
            jgen.writeString(key);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

}
