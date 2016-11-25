package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.mapper.ObjectMapperConfig;
import com.mnubo.java.sdk.client.mapper.StringExistsResultDeserializer;
import com.mnubo.java.sdk.client.models.SmartObject;
import com.mnubo.java.sdk.client.models.result.Result;
import com.mnubo.java.sdk.client.spi.ObjectsSDK;

import java.io.IOException;
import java.util.*;

import static com.mnubo.java.sdk.client.Constants.OBJECT_PATH;
import static com.mnubo.java.sdk.client.utils.ValidationUtils.notBlank;
import static com.mnubo.java.sdk.client.utils.ValidationUtils.validNotNull;
import static java.util.Arrays.asList;

class ObjectsSDKServices implements ObjectsSDK {
    private static final String OBJECT_PATH_EXITS = OBJECT_PATH + "/exists";

    private final SDKService sdkCommonServices;

    ObjectsSDKServices(SDKService sdkCommonServices) {
        this.sdkCommonServices = sdkCommonServices;
    }

    @Override
    public void create(SmartObject object) {
        validNotNull(object, "Object body");
        notBlank(object.getObjectType(), "x_object_type cannot be blank.");
        notBlank(object.getDeviceId(), "x_device_id cannot be blank.");

        final String url = sdkCommonServices.getIngestionBaseUri()
                                            .path(OBJECT_PATH)
                                            .build().toString();

        sdkCommonServices. postRequest(url, SmartObject.class, object);
    }

    @Override
    public void update(SmartObject object, String deviceId) {
        notBlank(deviceId, "x_device_id cannot be blank.");
        validNotNull(object, "Object body");

        final String url = sdkCommonServices.getIngestionBaseUri()
                                            .path(OBJECT_PATH)
                                            .pathSegment(deviceId)
                                            .build().toString();

        sdkCommonServices.putRequest(url, object);
    }

    @Override
    public void delete(String deviceId) {
        notBlank(deviceId, "x_device_id cannot be blank.");

        final String url = sdkCommonServices.getIngestionBaseUri()
                                            .path(OBJECT_PATH)
                                            .pathSegment(deviceId)
                                            .build().toString();

        sdkCommonServices.deleteRequest(url);
    }

    @Override
    public List<Result> createUpdate(List<SmartObject> objects) {
        validNotNull(objects, "List of smart objects body");

        final String url = sdkCommonServices.getIngestionBaseUri()
                                            .path(OBJECT_PATH)
                                            .build().toString();

        Result[] results = sdkCommonServices.putRequest(url, objects, Result[].class);
        return results == null ? new ArrayList<Result>() : Arrays.asList(results);
    }

    @Override
    public List<Result> createUpdate(SmartObject... objects) {
        return createUpdate(asList(objects));
    }

    @Override
    public Map<String, Boolean> objectsExist(List<String> deviceIds) {
        validNotNull(deviceIds, "List of the deviceIds cannot be null.");

        final String url = sdkCommonServices.getIngestionBaseUri()
                .path(OBJECT_PATH_EXITS)
                .build().toString();

        String unparsed = sdkCommonServices.postRequest(url, String.class, deviceIds);

        try {
            return ObjectMapperConfig.stringExistsObjectMapper.readValue(unparsed, StringExistsResultDeserializer.targetClass);
        }
        catch(IOException ioe) {
            throw new RuntimeException("Cannot deserialize server's response", ioe);
        }

    }

    @Override
    public Boolean objectExists(String deviceId) {
        notBlank(deviceId, "deviceId cannot be blank.");

        final Map<String, Boolean> subResults = objectsExist(Collections.singletonList(deviceId));

        return subResults.get(deviceId);
    }
}
