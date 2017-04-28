package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.models.datamodel.Model;
import com.mnubo.java.sdk.client.spi.ModelSDK;

class ModelSDKServices implements ModelSDK {

    private final SDKService sdkCommonServices;

    ModelSDKServices(SDKService sdkCommonServices) {
        this.sdkCommonServices = sdkCommonServices;
    }

    @Override
    public Model export() {
        return this.sdkCommonServices.getRequest(
                this.sdkCommonServices.getModelBaseUri().path("/export").toUriString(),
                Model.class
        );
    }
}