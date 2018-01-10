package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mnubo.java.sdk.client.models.datamodel.EventType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EventTypeDeserializer extends StdDeserializer<EventType> {

    public EventTypeDeserializer() {
        super(EventType.class);
    }

    @Override
    public EventType deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jp.getCodec().readTree(jp);
        return fromNode(root, timeseriesKeys(root));
    }

    private Set<String> timeseriesKeys(JsonNode rawEt) {
        HashSet<String> keys = new HashSet<>();
        final JsonNode timerseriesKeys = rawEt.get("timeseriesKeys");
        if(timerseriesKeys != null) {
            for (JsonNode rawKey : timerseriesKeys) {
                if(!rawKey.isTextual())
                    throw new IllegalArgumentException("timeseriesKeys should be an array of json string");
                keys.add(rawKey.asText());
            }
        }
        return keys;
    }

    static EventType fromNode(JsonNode rawEt, Set<String> timeseriesKeys) {
        if (!rawEt.isObject())
            throw new IllegalArgumentException("Expecting an object to build an event type");

        final String key = rawEt.get("key").asText();
        final String description = rawEt.get("description").asText();
        final String origin = rawEt.get("origin").asText();

        return new EventType(key, description, origin, timeseriesKeys);
    }
}
