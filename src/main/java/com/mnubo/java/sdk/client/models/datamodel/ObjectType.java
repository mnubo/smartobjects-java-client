package com.mnubo.java.sdk.client.models.datamodel;

import java.util.Set;

public class ObjectType {
    private final String key;
    private final String description;
    private final Set<String> objectAttributeKeys;

    public ObjectType(String key, String description, Set<String> objectAttributeKeys) {
        this.key = key;
        this.description = description;
        this.objectAttributeKeys = objectAttributeKeys;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getObjectAttributeKeys() {
        return objectAttributeKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectType that = (ObjectType) o;

        if (!key.equals(that.key)) return false;
        if (!description.equals(that.description)) return false;
        return objectAttributeKeys.equals(that.objectAttributeKeys);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + objectAttributeKeys.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ObjectType{" +
                "key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", objectAttributeKeys=" + objectAttributeKeys +
                '}';
    }
}
