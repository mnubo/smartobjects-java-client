package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mnubo.java.sdk.client.models.datamodel.ObjectType;
import com.mnubo.java.sdk.client.models.datamodel.OwnerAttribute;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ObjectTypeDeserializer extends StdDeserializer<ObjectType> {

    public ObjectTypeDeserializer() {
        super(ObjectType.class);
    }

    @Override
    public ObjectType deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jp.getCodec().readTree(jp);
        return fromNode(root, objectAttributeKeys(root));
    }

    private Set<String> objectAttributeKeys(JsonNode rawOt) {
        HashSet<String> keys = new HashSet<>();
        final JsonNode objectAttributeKeysNode = rawOt.get("objectAttributesKeys");
        if(objectAttributeKeysNode != null) {
            for (JsonNode rawKey : objectAttributeKeysNode) {
                if(!rawKey.isTextual())
                    throw new IllegalArgumentException("objectAttributesKeys should be an array of json string");
                keys.add(rawKey.asText());
            }
        }
        return keys;
    }

    static ObjectType fromNode(JsonNode rawOt, Set<String> objectAttributeKeys) {
        if (!rawOt.isObject())
            throw new IllegalArgumentException("Expecting an object to build an object type");

        final String key = rawOt.get("key").asText();
        final String description = rawOt.get("description").asText();

        return new ObjectType(key, description, objectAttributeKeys);
    }
}
