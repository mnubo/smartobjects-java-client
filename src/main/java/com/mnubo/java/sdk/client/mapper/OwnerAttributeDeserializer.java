package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mnubo.java.sdk.client.models.datamodel.ObjectAttribute;
import com.mnubo.java.sdk.client.models.datamodel.OwnerAttribute;

import java.io.IOException;
import java.util.HashSet;

public class OwnerAttributeDeserializer extends StdDeserializer<OwnerAttribute> {

    public OwnerAttributeDeserializer() {
        super(OwnerAttribute.class);
    }

    @Override
    public OwnerAttribute deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jp.getCodec().readTree(jp);
        return fromNode(root);
    }

    static OwnerAttribute fromNode(JsonNode rawOwner) {
        if (!rawOwner.isObject())
            throw new IllegalArgumentException("Expecting an object to build an owner attribute");

        final String ownerKey = rawOwner.get("key").asText();
        final String ownerDisplayName = rawOwner.get("displayName").asText();
        final String ownerDescription = rawOwner.get("description").asText();

        final JsonNode type = rawOwner.get("type");
        final String ownerHighLevelType = type.get("highLevelType").asText();
        final String ownerContainerType = type.get("containerType").asText();

        return new OwnerAttribute(
            ownerKey, ownerDisplayName, ownerDescription, ownerHighLevelType,ownerContainerType
        );
    }
}
