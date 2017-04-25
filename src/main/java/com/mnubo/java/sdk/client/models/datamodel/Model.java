package com.mnubo.java.sdk.client.models.datamodel;

import java.util.Set;

public class Model {
    private final Set<EventType> eventTypes;
    private final Set<ObjectType> objectTypes;
    private final Set<Timeseries> timeseries;
    private final Set<ObjectAttribute> objectAttributes;
    private final Set<OwnerAttribute> ownerAttributes;
    private final Set<Sessionizer> sessionizers;
    private final Orphans orphans;

    public Model(Set<EventType> eventTypes, Set<ObjectType> objectTypes, Set<Timeseries> timeseries,
                 Set<ObjectAttribute> objectAttributes, Set<OwnerAttribute> ownerAttributes,
                 Set<Sessionizer> sessionizers, Orphans orphans) {
        this.eventTypes = eventTypes;
        this.objectTypes = objectTypes;
        this.timeseries = timeseries;
        this.objectAttributes = objectAttributes;
        this.ownerAttributes = ownerAttributes;
        this.sessionizers = sessionizers;
        this.orphans = orphans;
    }

    public Set<EventType> getEventTypes() {
        return eventTypes;
    }

    public Set<ObjectType> getObjectTypes() {
        return objectTypes;
    }

    public Set<Timeseries> getTimeseries() {
        return timeseries;
    }

    public Set<ObjectAttribute> getObjectAttributes() {
        return objectAttributes;
    }

    public Set<OwnerAttribute> getOwnerAttributes() {
        return ownerAttributes;
    }

    public Set<Sessionizer> getSessionizers() {
        return sessionizers;
    }

    public Orphans getOrphans() {
        return orphans;
    }

    @Override
    public String toString() {
        return "Model{" +
                "eventTypes=" + eventTypes +
                ", objectTypes=" + objectTypes +
                ", timeseries=" + timeseries +
                ", objectAttributes=" + objectAttributes +
                ", ownerAttributes=" + ownerAttributes +
                ", sessionizers=" + sessionizers +
                ", orphans=" + orphans +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model model = (Model) o;

        if (!eventTypes.equals(model.eventTypes)) return false;
        if (!objectTypes.equals(model.objectTypes)) return false;
        if (!timeseries.equals(model.timeseries)) return false;
        if (!objectAttributes.equals(model.objectAttributes)) return false;
        if (!ownerAttributes.equals(model.ownerAttributes)) return false;
        if (!sessionizers.equals(model.sessionizers)) return false;
        return orphans.equals(model.orphans);
    }

    @Override
    public int hashCode() {
        int result = eventTypes.hashCode();
        result = 31 * result + objectTypes.hashCode();
        result = 31 * result + timeseries.hashCode();
        result = 31 * result + objectAttributes.hashCode();
        result = 31 * result + ownerAttributes.hashCode();
        result = 31 * result + sessionizers.hashCode();
        result = 31 * result + orphans.hashCode();
        return result;
    }
}
