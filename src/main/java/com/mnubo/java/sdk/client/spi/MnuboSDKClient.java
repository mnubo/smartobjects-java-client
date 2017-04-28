package com.mnubo.java.sdk.client.spi;

/**
 * Mnubo SDK Client. This interface gives access to all mnubo's sdk clients.
 */
public interface MnuboSDKClient {

    /**
     * Returns SmartObject Client, giving access to handle mnubo smartObjects API(s).
     *
     * @return ObjectSDK interface.
     *
     */
    ObjectsSDK getObjectClient();

    /**
     * Returns Event Client, giving access to handle mnubo events API(s).
     *
     * @return EventSDK interface
     *
     */
    EventsSDK getEventClient();

    /**
     * Returns Owner Client, giving access to handle mnubo Owners API(s).
     *
     * @return OwnerSDK interface
     *
     */
    OwnersSDK getOwnerClient();

    /**
     * Returns Search Client, giving access to handle mnubo Search API(s).
     *
     * @return SearchSDK interface
     *
     */
    SearchSDK getSearchClient();

    /**
     * Returns Model Client, giving access to handle mnubo Model API(s).
     *
     * @return ModelSDK interface
     *
     */
    ModelSDK getModelClient();

}
