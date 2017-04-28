package com.mnubo.java.sdk.client.models.datamodel;

import java.util.Set;

public class EventType {
    private final String key;
    private final String description;
    private final String origin;
    private final Set<String> timeseriesKeys;

    public EventType(String key, String description, String origin, Set<String> timeseriesKeys) {
        this.key = key;
        this.description = description;
        this.origin = origin;
        this.timeseriesKeys = timeseriesKeys;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getOrigin() {
        return origin;
    }

    public Set<String> getTimeseriesKeys() {
        return timeseriesKeys;
    }

    @Override
    public String toString() {
        return "EventType{" +
                "key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", origin='" + origin + '\'' +
                ", timeseriesKeys=" + timeseriesKeys +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventType eventType = (EventType) o;

        if (!key.equals(eventType.key)) return false;
        if (!description.equals(eventType.description)) return false;
        if (!origin.equals(eventType.origin)) return false;
        return timeseriesKeys.equals(eventType.timeseriesKeys);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + origin.hashCode();
        result = 31 * result + timeseriesKeys.hashCode();
        return result;
    }
}
