package com.mnubo.java.sdk.client.models.datamodel;

public class OwnerAttribute {
    private final String key;
    private final String displayName;
    private final String description;
    private final String type;
    private final String containerType;

    public OwnerAttribute(String key, String displayName, String description, String type, String containerType) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.containerType = containerType;
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

    @Override
    public String toString() {
        return "OwnerAttribute{" +
                "key='" + key + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", containerType='" + containerType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OwnerAttribute that = (OwnerAttribute) o;

        if (!key.equals(that.key)) return false;
        if (!displayName.equals(that.displayName)) return false;
        if (!description.equals(that.description)) return false;
        if (!type.equals(that.type)) return false;
        return containerType.equals(that.containerType);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + displayName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + containerType.hashCode();
        return result;
    }
}
