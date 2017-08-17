package com.mnubo.java.sdk.client.spi;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mnubo.java.sdk.client.models.Event;
import com.mnubo.java.sdk.client.models.result.Result;

/**
 * Event SDK Client. This interface gives access to handle events in specific objects.
 */
public interface EventsSDK {

    /**
     * Allows post events to an unique object. Note that in this case all Event.
     *
     * @param deviceId device Id to send the events
     * @param events list the events to be posted.
     * 
     * @return the list of result for all the events with corresponding id, status and
     * message.
     */
    List<Result> send(String deviceId, List<Event> events);

    /**
     * Allows post events to several objects. In this case, the device id is taken
     * directly from each event. Thus, a post will be sent by each event in the list.
     *
     * @param events list the events to be posted.
     * 
     * @return the list of result for all the events with corresponding id, status and
     * message.
     */
    List<Result> send(List<Event> events);

    /**
     * @see EventsSDK#send(List)
     */
    List<Result> send(Event... events);

    /**
     * Check if events with the given ids exists
     * @param eventIds The list of event Ids to check if exists. ["8bcece4c-b449-4229-8c7a-6d21aaff6a6f", "05a2f92d-8475-4b8b-a188-5452d1a5933"]
     * @return The list of event ids with an existing boolean, true if it exists, false if it does not exist. [{"8bcece4c-b449-4229-8c7a-6d21aaff6a6f": false}, {"05a2f92d-8475-4b8b-a188-5452d1a5933": true}]. Map is ordered, keys are sorted in the same order as the given event ids.
     */
    Map<UUID, Boolean> eventsExist(List<UUID> eventIds);

    /**
     * Check if an event with the given id exists
     * @param eventId The event Id to check if exists. "eventA"
     * @return Existing boolean value. false
     */
    Boolean eventExists(UUID eventId);

    /**
     * @deprecated Please use `eventExists(eventId)` instead. Will be removed in a future version
     */
    @Deprecated
    Boolean isEventExists(UUID eventId);
}
