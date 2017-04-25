package com.mnubo.java.sdk.client.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mnubo.java.sdk.client.models.datamodel.*;

import java.io.IOException;
import java.util.*;

public class ModelDeserializer extends StdDeserializer<Model> {

    public ModelDeserializer() { super(Model.class); }

    @Override
    public Model deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode root = jp.getCodec().readTree(jp);

        Set<ObjectType> objectTypes = new HashSet<>();
        Map<String, ObjectAttribute> objectAttributeByKey = new HashMap<>();
        Map<TypeKeyPair, ObjectAttribute> allObjectAttributes = new HashMap<>();

        extractObjectTypesAndObjectAttributes(
                root.get("objectTypes"),
                objectTypes,
                objectAttributeByKey,
                allObjectAttributes
        );

        Set<EventType> eventTypes = new HashSet<>();
        Map<String, Timeseries> timeseriesByKey = new HashMap<>();
        Map<TypeKeyPair, Timeseries> allTimeseries = new HashMap<>();

        extractEventTypesAndTimeseries(
                root.get("eventTypes"),
                eventTypes,
                timeseriesByKey,
                allTimeseries
        );

        Set<OwnerAttribute> ownerAttributes = extractOwnerAttributes(root.get("ownerAttributes"));

        Set<Sessionizer> sessionizers = extractSessionizers(root.get("sessionizers"));

        final Orphans orphans = extractOrphans(
                root.get("orphans")
        );

        return new Model(
                eventTypes, objectTypes,
                new HashSet<>(timeseriesByKey.values()),
                new HashSet<>(objectAttributeByKey.values()),
                ownerAttributes, sessionizers,
                orphans
        );
    }

    private Orphans extractOrphans(JsonNode orphansNode) {
        Set<Timeseries> orphanTimeseries = new HashSet<>();
        Set<ObjectAttribute> orphanObjectAttributes = new HashSet<>();

        if(orphansNode != null) {
            final JsonNode timeseriesNode = orphansNode.get("timeseries");
            if (timeseriesNode != null) {
                for (JsonNode rawTs : timeseriesNode) {
                    final String tsKey = rawTs.get("key").asText();
                    final String tsDisplayName = rawTs.get("displayName").asText();
                    final String tsDescription = rawTs.get("description").asText();

                    final String tsHighLevelType = rawTs.get("type").get("highLevelType").asText();

                    orphanTimeseries.add(new Timeseries(
                            tsKey, tsDisplayName, tsDescription, tsHighLevelType, Collections.<String>emptySet()
                    ));
                }
            }
            final JsonNode objectAttributesNode = orphansNode.get("objectAttributes");
            if (objectAttributesNode != null) {
                for (JsonNode rawObj : objectAttributesNode) {
                    final String objKey = rawObj.get("key").asText();
                    final String objDisplayName = rawObj.get("displayName").asText();
                    final String objDescription = rawObj.get("description").asText();

                    final JsonNode type = rawObj.get("type");
                    final String objHighLevelType = type.get("highLevelType").asText();
                    final String objContainerType = type.get("containerType").asText();

                    orphanObjectAttributes.add(new ObjectAttribute(
                            objKey, objDisplayName, objDescription, objHighLevelType,
                            objContainerType, Collections.<String>emptySet()
                    ));
                }
            }
        }

        return new Orphans(orphanTimeseries, orphanObjectAttributes);
    }

    private Set<Sessionizer> extractSessionizers(JsonNode sessionizersNode) {
        Set<Sessionizer> sessionizers = new HashSet<>();

        if (sessionizersNode != null) {
            for (JsonNode rawSessionizer : sessionizersNode) {
                final String key = rawSessionizer.get("key").asText();
                final String displayName = rawSessionizer.get("displayName").asText();
                final String description = rawSessionizer.get("description").asText();
                final String startEventTypeKey = rawSessionizer.get("startEventTypeKey").asText();
                final String endEventTypeKey = rawSessionizer.get("endEventTypeKey").asText();

                sessionizers.add(new Sessionizer(key, displayName, description, startEventTypeKey, endEventTypeKey));
            }
        }

        return sessionizers;
    }

    private Set<OwnerAttribute> extractOwnerAttributes(JsonNode ownerAttributesNode) {
        Set<OwnerAttribute> ownerAttributes = new HashSet<>();

        if (ownerAttributesNode != null) {
            for (JsonNode rawOwner : ownerAttributesNode) {
                final String key = rawOwner.get("key").asText();
                final String displayName = rawOwner.get("displayName").asText();
                final String description = rawOwner.get("description").asText();

                final JsonNode type = rawOwner.get("type");
                final String highLevelType = type.get("highLevelType").asText();
                final String containerType = type.get("containerType").asText();

                ownerAttributes.add(new OwnerAttribute( key, displayName, description, highLevelType, containerType));
            }
        }

        return ownerAttributes;
    }

    private void extractEventTypesAndTimeseries(
            JsonNode eventTypesNode,
            Set<EventType> eventTypes,
            Map<String, Timeseries> timeseriesByKey,
            Map<TypeKeyPair, Timeseries> allTimeseries) {

        if (eventTypesNode != null) {
            for (JsonNode rawEt : eventTypesNode) {
                final String key = rawEt.get("key").asText();
                final String description = rawEt.get("description").asText();
                final String origin = rawEt.get("origin").asText();

                Set<String> timeseriesKeys = new HashSet<>();
                final JsonNode timeseriesNode = rawEt.get("timeseries");
                if(timeseriesNode != null) {
                    for (JsonNode rawTs : timeseriesNode) {
                        final String tsKey = rawTs.get("key").asText();
                        final String tsDisplayName = rawTs.get("displayName").asText();
                        final String tsDescription = rawTs.get("description").asText();

                        final String tsHighLevelType = rawTs.get("type").get("highLevelType").asText();

                        final Timeseries ts = new Timeseries(
                                tsKey, tsDisplayName, tsDescription, tsHighLevelType, new HashSet<>(Collections.singletonList(key))
                        );

                        allTimeseries.put(new TypeKeyPair(key, ts.getKey()), ts);
                        timeseriesKeys.add(ts.getKey());
                    }
                }

                eventTypes.add(new EventType(key, description, origin, timeseriesKeys));
            }
        }


        for (Map.Entry<TypeKeyPair, Timeseries> typeKeyPairTimeseriesEntry : allTimeseries.entrySet()) {
            final String typeKey = typeKeyPairTimeseriesEntry.getKey().typeKey;
            final String key = typeKeyPairTimeseriesEntry.getKey().key;

            final Timeseries exists = timeseriesByKey.get(key);
            if(exists == null) {
                timeseriesByKey.put(key, typeKeyPairTimeseriesEntry.getValue());
            } else {
                exists.getEventTypeKeys().add(typeKey);
            }
        }
    }

    private void extractObjectTypesAndObjectAttributes(
            JsonNode objectTypesRootNode,
            Set<ObjectType> objectTypes,
            Map<String, ObjectAttribute> objectAttributeByKey,
            Map<TypeKeyPair, ObjectAttribute> allObjectAttribute) {
        if (objectTypesRootNode != null) {
            for (JsonNode rawOt : objectTypesRootNode) {
                final String key = rawOt.get("key").asText();
                final String description = rawOt.get("description").asText();

                Set<String> objectAttributeKeys = new HashSet<>();
                final JsonNode objectAttributesNode = rawOt.get("objectAttributes");
                if(objectAttributesNode != null) {
                    for (JsonNode rawObj : objectAttributesNode) {
                        final String objKey = rawObj.get("key").asText();
                        final String objDisplayName = rawObj.get("displayName").asText();
                        final String objDescription = rawObj.get("description").asText();

                        final JsonNode type = rawObj.get("type");
                        final String objHighLevelType = type.get("highLevelType").asText();
                        final String objContainerType = type.get("containerType").asText();

                        final ObjectAttribute obj = new ObjectAttribute(
                                objKey, objDisplayName, objDescription, objHighLevelType,
                                objContainerType, new HashSet<>(Collections.singletonList(key))
                        );

                        allObjectAttribute.put(new TypeKeyPair(key, obj.getKey()), obj);
                        objectAttributeKeys.add(obj.getKey());
                    }
                }

                objectTypes.add(new ObjectType(key, description, objectAttributeKeys));
            }
        }


        for (Map.Entry<TypeKeyPair, ObjectAttribute> typeKeyPairObjectAttributeEntry : allObjectAttribute.entrySet()) {
            final String typeKey = typeKeyPairObjectAttributeEntry.getKey().typeKey;
            final String key = typeKeyPairObjectAttributeEntry.getKey().key;

            final ObjectAttribute exists = objectAttributeByKey.get(key);
            if(exists == null) {
                objectAttributeByKey.put(key, typeKeyPairObjectAttributeEntry.getValue());
            } else {
                exists.getObjectTypeKeys().add(typeKey);
            }
        }
    }


    private static class TypeKeyPair {
        private final String typeKey;
        private final String key;

        public TypeKeyPair(String typeKey, String key) {
            this.typeKey = typeKey;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeKeyPair that = (TypeKeyPair) o;

            if (!typeKey.equals(that.typeKey)) return false;
            return key.equals(that.key);
        }

        @Override
        public int hashCode() {
            int result = typeKey.hashCode();
            result = 31 * result + key.hashCode();
            return result;
        }
    }
}
