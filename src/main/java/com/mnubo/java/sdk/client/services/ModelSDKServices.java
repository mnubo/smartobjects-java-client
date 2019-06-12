package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.models.datamodel.*;
import com.mnubo.java.sdk.client.spi.ModelSDK;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

class ModelSDKServices implements ModelSDK {

    private final SDKService sdkCommonServices;

    private final SandboxEntityOps<Timeseries> tsOps;
    private final String tsBasePath = "/timeseries";

    private final SandboxEntityOps<ObjectAttribute> objectsOps;
    private final String objBasePath = "/objectAttributes";

    private final SandboxEntityOps<OwnerAttribute> ownersOps;
    private final String ownersBasePath = "/ownerAttributes";

    private final SandboxTypeOps<ObjectType> objectTypesOps;
    private final String objectTypesBasePath = "/objectTypes";

    private final SandboxTypeOps<EventType> eventTypesOps;
    private final String eventTypesBasePath = "/eventTypes";


    private final SandboxOnlyOps sandboxOnlyOps;
    private final ResetOps resetOps;


    ModelSDKServices(SDKService sdkCommonServices) {
        this.sdkCommonServices = sdkCommonServices;

        tsOps = new SandboxEntityOpsImpl<>(this.sdkCommonServices, tsBasePath);
        objectsOps = new SandboxEntityOpsImpl<>(this.sdkCommonServices, objBasePath);
        ownersOps = new SandboxEntityOpsImpl<>(this.sdkCommonServices, ownersBasePath);

        objectTypesOps = new SandboxTypeOpsImpl<>(this.sdkCommonServices, objectTypesBasePath, objBasePath);
        eventTypesOps = new SandboxTypeOpsImpl<>(this.sdkCommonServices, eventTypesBasePath, tsBasePath);

        resetOps = new ResetOpsImpl(this.sdkCommonServices);

        sandboxOnlyOps = new SandboxOnlyOps() {
            @Override
            public SandboxEntityOps<Timeseries> timeseriesOps() {
                return tsOps;
            }

            @Override
            public SandboxEntityOps<ObjectAttribute> objectAttributesOps() {
                return objectsOps;
            }

            @Override
            public SandboxEntityOps<OwnerAttribute> ownerAttributesOps() {
                return ownersOps;
            }

            @Override
            public SandboxTypeOps<ObjectType> objectTypesOps() {
                return objectTypesOps;
            }

            @Override
            public SandboxTypeOps<EventType> eventTypesOps() {
                return eventTypesOps;
            }

            @Override
            public ResetOps resetOps() {
                return resetOps;
            }

        };
    }

    @Override
    public Model export() {
        return this.sdkCommonServices.getRequest(
            this.sdkCommonServices.getModelBaseUri().path("/export").toUriString(),
            Model.class
        );
    }

    @Override
    public SandboxOnlyOps sandboxOps() {
        return sandboxOnlyOps;
    }

    @Override
    public Set<Timeseries> getTimeseries() {
        return this.sdkCommonServices.getRequest(
            this.sdkCommonServices.getModelBaseUri().path(tsBasePath).toUriString(),
                new ParameterizedTypeReference<Set<Timeseries>>() {}
        );
    }

    @Override
    public Set<ObjectAttribute> getObjectAttributes() {
        return this.sdkCommonServices.getRequest(
            this.sdkCommonServices.getModelBaseUri().path(objBasePath).toUriString(),
                new ParameterizedTypeReference<Set<ObjectAttribute>>() {}
        );
    }

    @Override
    public Set<OwnerAttribute> getOwnerAttributes() {
        return this.sdkCommonServices.getRequest(
            this.sdkCommonServices.getModelBaseUri().path(ownersBasePath).toUriString(),
                new ParameterizedTypeReference<Set<OwnerAttribute>>() {}
        );
    }

    @Override
    public Set<ObjectType> getObjectTypes() {
        return this.sdkCommonServices.getRequest(
            this.sdkCommonServices.getModelBaseUri().path(objectTypesBasePath).toUriString(),
                new ParameterizedTypeReference<Set<ObjectType>>() {}
        );
    }

    @Override
    public Set<EventType> getEventTypes() {

        return this.sdkCommonServices.getRequest(
                this.sdkCommonServices.getModelBaseUri().path(eventTypesBasePath).toUriString(),
                new ParameterizedTypeReference<Set<EventType>>() {}
        );
    }
    private class SandboxEntityOpsImpl<A> implements ModelSDK.SandboxEntityOps<A> {

        private final SDKService sdkCommonServices;
        private final String basePath;

        SandboxEntityOpsImpl(SDKService sdkCommonServices, String basePath) {
            this.sdkCommonServices = sdkCommonServices;
            this.basePath = basePath;
        }

        @Override
        public void create(Set<A> value) {
            this.sdkCommonServices.postRequest(
                this.sdkCommonServices.getModelBaseUri().path(this.basePath).toUriString(),
                value.toArray()
            );
        }

        @Override
        public void createOne(A value) {
            this.create(Collections.singleton(value));
        }

        @Override
        public void update(String key, UpdateEntity update) {
            this.sdkCommonServices.putRequest(
                this.sdkCommonServices.getModelBaseUri().path(this.basePath).path("/" + key).toUriString(),
                update
            );
        }

        @Override
        public String generateDeployCode(String key) {
            final Map<String, String> body = this.sdkCommonServices.postRequest(
                this.sdkCommonServices.getModelBaseUri()
                    .path(this.basePath)
                    .path("/" + key)
                    .path("/deploy")
                    .toUriString(),
                new ParameterizedTypeReference<Map<String, String>>() {},
                null
            );
            if (!body.containsKey("code"))
                throw new IllegalStateException("The response json should contain a code.");
            return body.get("code");
        }

        @Override
        public void applyDeployCode(String key, String code) {
            this.sdkCommonServices.postRequest(
                this.sdkCommonServices.getModelBaseUri()
                    .path(this.basePath)
                    .path("/" + key)
                    .path("/deploy")
                    .path("/" + code)
                    .toUriString(),
                null
            );
        }

        @Override
        public void deploy(String key) {
            final String code = generateDeployCode(key);
            applyDeployCode(key, code);
        }
    }
    private class SandboxTypeOpsImpl<A> implements ModelSDK.SandboxTypeOps<A> {

        private final SDKService sdkCommonServices;
        private final String basePath;
        private final String entityBasePath;

        SandboxTypeOpsImpl(SDKService sdkCommonServices, String basePath, String entityBasePath) {
            this.sdkCommonServices = sdkCommonServices;
            this.basePath = basePath;
            this.entityBasePath = entityBasePath;
        }

        @Override
        public void create(Set<A> value) {
            this.sdkCommonServices.postRequest(
                this.sdkCommonServices.getModelBaseUri().path(this.basePath).toUriString(),
                value.toArray()
            );
        }

        @Override
        public void createOne(A value) {
            this.create(Collections.singleton(value));
        }

        @Override
        public void update(String key, A update) {
            this.sdkCommonServices.putRequest(
                this.sdkCommonServices.getModelBaseUri().path(this.basePath).path("/" + key).toUriString(),
                update
            );
        }

        @Override
        public void delete(String key) {
            this.sdkCommonServices.deleteRequest(
                this.sdkCommonServices.getModelBaseUri().path(this.basePath).path("/" + key).toUriString()
            );
        }

        @Override
        public A addRelation(String typeKey, String entityKey) {
            return this.sdkCommonServices.postRequest(
                this.sdkCommonServices.getModelBaseUri()
                    .path(this.basePath)
                    .path("/" + typeKey)
                    .path(entityBasePath)
                    .path("/" + entityKey)
                    .toUriString(),
                new ParameterizedTypeReference<A>() {},
                null
            );
        }

        @Override
        public void removeRelation(String typeKey, String entityKey) {
            this.sdkCommonServices.deleteRequest(
                this.sdkCommonServices.getModelBaseUri()
                    .path(this.basePath)
                    .path("/" + typeKey)
                    .path(entityBasePath)
                    .path("/" + entityKey)
                    .toUriString()
            );
        }
    }

    private class ResetOpsImpl implements ModelSDK.ResetOps {

        private final SDKService sdkCommonServices;

        ResetOpsImpl(SDKService sdkCommonServices) {
            this.sdkCommonServices = sdkCommonServices;
        }

        @Override
        public String generateResetCode() {
            final Map<String, String> body = this.sdkCommonServices.postRequest(
                    this.sdkCommonServices.getModelBaseUri()
                            .path("/reset")
                            .toUriString(),
                    new ParameterizedTypeReference<Map<String, String>>() {},
                    null
            );
            if (!body.containsKey("code"))
                throw new IllegalStateException("The response json should contain a code.");
            return body.get("code");
        }

        @Override
        public void applyResetCode(String code) {
            this.sdkCommonServices.postRequest(
                    this.sdkCommonServices.getModelBaseUri()
                        .path("/reset")
                        .path("/" + code)
                        .toUriString(),
                    null
            );
        }

        @Override
        public void reset() {
            final String code = generateResetCode();
            applyResetCode(code);
        }
    }
}