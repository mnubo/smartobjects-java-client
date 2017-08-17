package com.mnubo.java.sdk.client.spi;

import java.util.List;
import java.util.Map;

import com.mnubo.java.sdk.client.models.SmartObject;
import com.mnubo.java.sdk.client.models.result.Result;

/**
 * Event SDK Client. This interface gives access to handle objects.
 */
public interface ObjectsSDK {

    /**
     * Allows create SmartObjects.
     *
     * @param object, SmartObject bean to be created.
     */
    void create(SmartObject object);

    /**
     * Allows update an existing SmartObjects.
     *
     * @param object, SmartObject bean to be updated.
     * @param deviecId, device id to be updated.
     *
     */
    void update(SmartObject object, String deviecId);

    /**
     * Allows delete an existing SmartObjects.
     *
     * @param deviceId, Object's "deviceId" to be deleted.
     */
    void delete(String deviceId);

    /**
     * Add or update a list of objects.  
     * If an object doesn't exist, it will be created, otherwise it will be updated.
     *
     * @param objects, list of SmartObject bean to be created or updated.
     * 
     * @return the list of result for all the objects with corresponding id, status and
     * message.
     */
    List<Result> createUpdate(List<SmartObject> objects);

    /**
     * @see ObjectsSDK#createUpdate(List)
     */
    List<Result> createUpdate(SmartObject... objects);

    /**
     * It checks if objects with the given device ids exist
     * @param deviceIds The list of device Ids to check if exists. ["objectA", "objectB"]
     * @return The list of device ids with an existing boolean, true if it exists, false if it does not exist. Map is ordered, keys are sorted in the same order as the given device ids. [{"objectA": false}, {"objectB": true}]
     */
    Map<String, Boolean> objectsExist(List<String> deviceIds);

    /**
     * It checks if an object with the given device id exists
     * @param deviceId The device Id to check if exists. "objectA"
     * @return Existing boolean value. false
     */
    Boolean objectExists(String deviceId);
}
