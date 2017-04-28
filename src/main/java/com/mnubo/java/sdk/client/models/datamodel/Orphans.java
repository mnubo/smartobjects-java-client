package com.mnubo.java.sdk.client.models.datamodel;

import java.util.Set;

public class Orphans {

    private final Set<Timeseries> timeseries;
    private final Set<ObjectAttribute> objectAttributes;

    public Orphans(Set<Timeseries> timeseries, Set<ObjectAttribute> objectAttributes) {
        this.timeseries = timeseries;
        this.objectAttributes = objectAttributes;
    }

    public Set<Timeseries> getTimeseries() {
        return timeseries;
    }

    public Set<ObjectAttribute> getObjectAttributes() {
        return objectAttributes;
    }

    @Override
    public String toString() {
        return "Orphans{" +
                "timeseries=" + timeseries +
                ", objectAttributes=" + objectAttributes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orphans orphans = (Orphans) o;

        if (!timeseries.equals(orphans.timeseries)) return false;
        return objectAttributes.equals(orphans.objectAttributes);
    }

    @Override
    public int hashCode() {
        int result = timeseries.hashCode();
        result = 31 * result + objectAttributes.hashCode();
        return result;
    }
}
