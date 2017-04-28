package com.mnubo.java.sdk.client.models.datamodel;

import java.util.Set;

public class Timeseries {
    private final String key;
    private final String displayName;
    private final String description;
    private final String type;
    private final Set<String> eventTypeKeys;

    public Timeseries(String key, String displayName, String description, String type, Set<String> eventTypeKeys) {
        this.key = key;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.eventTypeKeys = eventTypeKeys;
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

    public Set<String> getEventTypeKeys() {
        return eventTypeKeys;
    }

    @Override
    public String toString() {
        return "Timeseries{" +
                "key='" + key + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", eventTypeKeys=" + eventTypeKeys +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timeseries that = (Timeseries) o;

        if (!key.equals(that.key)) return false;
        if (!displayName.equals(that.displayName)) return false;
        if (!description.equals(that.description)) return false;
        if (!type.equals(that.type)) return false;
        return eventTypeKeys.equals(that.eventTypeKeys);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + displayName.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + eventTypeKeys.hashCode();
        return result;
    }
}
