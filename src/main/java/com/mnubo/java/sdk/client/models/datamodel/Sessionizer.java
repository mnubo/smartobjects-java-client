package com.mnubo.java.sdk.client.models.datamodel;

public class Sessionizer {
    private final String key;
    private final String displayName;
    private final String description;
    private final String startEventTypeKey;
    private final String endEventTypeKey;

    public Sessionizer(String key, String displayName, String description, String startEventTypeKey, String endEventTypeKey) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.startEventTypeKey = startEventTypeKey;
        this.endEventTypeKey = endEventTypeKey;
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

    public String getStartEventTypeKey() {
        return startEventTypeKey;
    }

    public String getEndEventTypeKey() {
        return endEventTypeKey;
    }

    @Override
    public String toString() {
        return "Sessionizer{" +
                "key='" + key + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", startEventTypeKey='" + startEventTypeKey + '\'' +
                ", endEventTypeKey='" + endEventTypeKey + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sessionizer that = (Sessionizer) o;

        if (!key.equals(that.key)) return false;
        if (!displayName.equals(that.displayName)) return false;
        if (!description.equals(that.description)) return false;
        if (!startEventTypeKey.equals(that.startEventTypeKey)) return false;
        return endEventTypeKey.equals(that.endEventTypeKey);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + displayName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + startEventTypeKey.hashCode();
        result = 31 * result + endEventTypeKey.hashCode();
        return result;
    }
}
