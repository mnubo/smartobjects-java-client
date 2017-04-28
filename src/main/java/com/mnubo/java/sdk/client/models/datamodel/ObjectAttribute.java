package com.mnubo.java.sdk.client.models.datamodel;

import java.util.Set;

public class ObjectAttribute {
    private final String key;
    private final String displayName;
    private final String description;
    private final String type;
    private final String containerType;
    private final Set<String> objectTypeKeys;

    public ObjectAttribute(String key, String displayName, String description, String type, String containerType, Set<String> objectTypeKeys) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.containerType = containerType;
        this.objectTypeKeys = objectTypeKeys;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getContainerType() {
        return containerType;
    }

    public Set<String> getObjectTypeKeys() {
        return objectTypeKeys;
    }

    @Override
    public String toString() {
        return "ObjectAttribute{" +
                "key='" + key + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", containerType='" + containerType + '\'' +
                ", objectTypeKeys=" + objectTypeKeys +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectAttribute that = (ObjectAttribute) o;

        if (!key.equals(that.key)) return false;
        if (!displayName.equals(that.displayName)) return false;
        if (!description.equals(that.description)) return false;
        if (!type.equals(that.type)) return false;
        if (!containerType.equals(that.containerType)) return false;
        return objectTypeKeys.equals(that.objectTypeKeys);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + displayName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + containerType.hashCode();
        result = 31 * result + objectTypeKeys.hashCode();
        return result;
    }
}
