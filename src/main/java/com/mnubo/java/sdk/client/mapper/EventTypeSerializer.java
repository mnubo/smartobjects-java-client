package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mnubo.java.sdk.client.models.datamodel.EventType;

import java.io.IOException;

public class EventTypeSerializer extends StdSerializer<EventType> {

    public EventTypeSerializer() { super(EventType.class); }

    @Override
    public void serialize(EventType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("description", value.getDescription());
        jgen.writeStringField("origin", value.getOrigin());
        jgen.writeArrayFieldStart("timeseriesKeys");
        for (String key : value.getTimeseriesKeys()) {
            jgen.writeString(key);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

}
