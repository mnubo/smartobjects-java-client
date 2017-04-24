package com.mnubo.java.sdk.client.models;

import com.mnubo.java.sdk.client.utils.ValidationUtils;

import java.util.Collections;
import java.util.Map;

public class ClaimOrUnclaim {
    /**
     * Constant key used during the json deserialization and serialization.
     */
    public static final String USERNAME = "username";

    /**
     * Constant key used during the json deserialization and serialization.
     */
    public static final String DEVICE_ID = "x_device_id";

    private final String username;

    private final String deviceId;

    private final Map<String, Object> attributes;

    public ClaimOrUnclaim(String username, String deviceId) {
        this(username, deviceId, Collections.<String, Object>emptyMap());
    }

    public ClaimOrUnclaim(String username, String deviceId, Map<String, Object> attributes) {
        ValidationUtils.notBlank(username, "username must not be blank");
        ValidationUtils.notBlank(deviceId, "deviceId must not be blank");

        this.username = username;
        this.deviceId = deviceId;
        this.attributes = attributes;
    }

    public String getUsername() {
        return username;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
