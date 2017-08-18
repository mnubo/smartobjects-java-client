package com.mnubo.java.sdk.client.spi;

import com.mnubo.java.sdk.client.models.datamodel.Model;

/**
 * ModelSDK gives you access to the model currently available in the zone (sandbox or production).
 */
public interface ModelSDK {

    /**
     * Exports the model in the current zone. The current zone is determined by the configuration you use:
     *  - sandbox url, consumer key and consumer secret fetch the sandbox model
     *  - production url, consumer key and consumer secret fetch the production model
     *  @return a representation of the model in the current zone
     */
    Model export();

}