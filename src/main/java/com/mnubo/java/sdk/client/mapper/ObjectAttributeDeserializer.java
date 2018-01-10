package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mnubo.java.sdk.client.models.datamodel.ObjectAttribute;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ObjectAttributeDeserializer extends StdDeserializer<ObjectAttribute> {

    public ObjectAttributeDeserializer() {
        super(ObjectAttribute.class);
    }

    @Override
    public ObjectAttribute deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jp.getCodec().readTree(jp);
        return fromNode(root, objectTypeKeys(root));
    }

    private Set<String> objectTypeKeys(JsonNode rawEt) {
        HashSet<String> keys = new HashSet<>();
        final JsonNode objectTypeKeys = rawEt.get("objectTypeKeys");
        if(objectTypeKeys != null) {
            for (JsonNode rawKey : objectTypeKeys) {
                if(!rawKey.isTextual())
                    throw new IllegalArgumentException("objectTypeKeys should be an array of json string");
                keys.add(rawKey.asText());
            }
        }
        return keys;
    }

    static ObjectAttribute fromNode(JsonNode rawObj, Set<String> otKeys) {
        if (!rawObj.isObject())
            throw new IllegalArgumentException("Expecting an object to build an object attribute");

        final String objKey = rawObj.get("key").asText();
        final String objDisplayName = rawObj.get("displayName").asText();
        final String objDescription = rawObj.get("description").asText();

        final JsonNode type = rawObj.get("type");
        final String objHighLevelType = type.get("highLevelType").asText();
        final String objContainerType = type.get("containerType").asText();

        return new ObjectAttribute(
                objKey, objDisplayName, objDescription, objHighLevelType,
                objContainerType, otKeys
        );
    }
}
