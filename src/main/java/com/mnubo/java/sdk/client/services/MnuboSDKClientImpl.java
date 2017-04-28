package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.spi.*;
import org.springframework.web.client.RestTemplate;

import com.mnubo.java.sdk.client.config.MnuboSDKConfig;

final class MnuboSDKClientImpl implements MnuboSDKClient {
    private final ObjectsSDK objectCLient;
    private final OwnersSDK ownerCLient;
    private final EventsSDK eventCLient;
    private final SearchSDK searchCLient;
    private final ModelSDK modeClient;
    private final SDKService sdkService;

    MnuboSDKClientImpl(MnuboSDKConfig config, RestTemplate restTemplate, CredentialHandler credentials) {

        // creating SDK service instance
        sdkService = new SDKService(restTemplate, credentials, config);

        // creating specific clients
        objectCLient = new ObjectsSDKServices(sdkService);
        eventCLient = new EventsSDKServices(sdkService);
        ownerCLient = new OwnersSDKServices(sdkService);
        searchCLient = new SearchSDKServices(sdkService);
        modeClient = new ModelSDKServices(sdkService);
    }

    @Override
    public ObjectsSDK getObjectClient() {
        return objectCLient;
    }

    @Override
    public EventsSDK getEventClient() {
        return eventCLient;
    }

    @Override
    public OwnersSDK getOwnerClient() {
        return ownerCLient;
    }

    @Override
    public SearchSDK getSearchClient() {
        return searchCLient;
    }

    @Override
    public ModelSDK getModelClient() {
        return modeClient;
    }

    public SDKService getSdkService() {
        return sdkService;
    }
}
