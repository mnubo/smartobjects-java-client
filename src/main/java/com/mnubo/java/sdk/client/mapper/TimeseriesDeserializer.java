package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mnubo.java.sdk.client.models.datamodel.Timeseries;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TimeseriesDeserializer extends StdDeserializer<Timeseries> {

    public TimeseriesDeserializer() {
        super(Timeseries.class);
    }

    @Override
    public Timeseries deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jp.getCodec().readTree(jp);
        return fromNode(root, eventTypeKeys(root));
    }

    private Set<String> eventTypeKeys(JsonNode rawEt) {
        HashSet<String> keys = new HashSet<>();
        final JsonNode eventTypeKeys = rawEt.get("eventTypeKeys");
        if(eventTypeKeys != null) {
            for (JsonNode rawKey : eventTypeKeys) {
                if(!rawKey.isTextual())
                    throw new IllegalArgumentException("eventTypeKeys should be an array of json string");
                keys.add(rawKey.asText());
            }
        }
        return keys;
    }

    static Timeseries fromNode(JsonNode rawTs, Set<String> etKeys) {
        if (!rawTs.isObject())
            throw new IllegalArgumentException("Expecting an object to build a timeseries");

        final String tsKey = rawTs.get("key").asText();
        final String tsDisplayName = rawTs.get("displayName").asText();
        final String tsDescription = rawTs.get("description").asText();

        final String tsHighLevelType = rawTs.get("type").get("highLevelType").asText();

        return new Timeseries(tsKey, tsDisplayName, tsDescription, tsHighLevelType, etKeys);
    }
}
