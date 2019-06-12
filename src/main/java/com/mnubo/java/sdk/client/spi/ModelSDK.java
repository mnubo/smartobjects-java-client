package com.mnubo.java.sdk.client.spi;

import com.mnubo.java.sdk.client.models.datamodel.*;

import java.util.Objects;
import java.util.Set;

/**
 * ModelSDK gives you access to the data model in the target environment.
 *
 * The target environment is determined by the configuration you use:
 *  - sandbox: sandbox url, consumer key and consumer secret
 *  - production: production url, consumer key and consumer secret
 *
 *  @see <a href="https://smartobjects.mnubo.com/documentation/api_modeler.html">
 *      API Modeler online documentation
 *  </a>
 */
public interface ModelSDK {

    /**
     * Exports the model in the current zone.
     *  @return a representation of the data model
     */
    Model export();

    /**
     * Access to operations only available in the sandbox environment.
     * @return an instance implementing SandboxOnlyOps
     */
    SandboxOnlyOps sandboxOps();

    /**
     * All timeseries in the target environment.
     * @return a Set of Timeseries
     * @see Timeseries
     */
    Set<Timeseries> getTimeseries();
    /**
     * All object attributes in the target environment.
     * @return a Set of ObjectAttributes
     * @see ObjectAttribute
     */
    Set<ObjectAttribute> getObjectAttributes();
    /**
     * All owner attributes in the target environment.
     * @return a Set of OwnerAttributes
     * @see OwnerAttribute
     */
    Set<OwnerAttribute> getOwnerAttributes();
    /**
     * All object types in the target environment.
     * @return a Set of ObjectTypes
     * @see ObjectType
     */
    Set<ObjectType> getObjectTypes();
    /**
     * All event types in the target environment.
     * @return a Set of EventTypes
     * @see EventType
     */
    Set<EventType> getEventTypes();

    /**
     * Updates operations are only available in sandbox. If you call methods on this interface when your
     * client is configured to hit the production environment, you'll get undefined behaviour:
     *  - 404 Not Found
     *  - Bad Request
     *  - etc.
     */
    interface SandboxOnlyOps {
        /**
         * Sandbox timeseries operations
         * @return an instance of SandboxEntityOps for timeseries
         */
        SandboxEntityOps<Timeseries> timeseriesOps();
        /**
         * Sandbox object attributes operations
         * @return an instance of SandboxEntityOps for object attributes
         */
        SandboxEntityOps<ObjectAttribute> objectAttributesOps();
        /**
         * Sandbox owner attributes operations
         * @return an instance of SandboxEntityOps for owner attributes
         */
        SandboxEntityOps<OwnerAttribute> ownerAttributesOps();

        /**
         * Sandbox object types operations
         * @return an instance of SandboxTypeOps for object types
         */
        SandboxTypeOps<ObjectType> objectTypesOps();

        /**
         * Sandbox event types operations
         * @return an instance of SandboxTypeOps for event types
         */
        SandboxTypeOps<EventType> eventTypesOps();

        /**
         * Reset the sandbox data model
         */
        ResetOps resetOps();

    }

    /**
     * Create operations for the following types:
     *  - Object Attribute
     *  - Owner Attribute
     *  - Timeseries
     *  - Event Types
     *  - Object Types
     */
    interface CreateOps<A> {
        /**
         * Creates an instance of A
         * @param value is a non null instance of A
         */
        void create(Set<A> value);

        /**
         * Creates multiple instances of A
         * @param value is a non null instance of A
         */
        void createOne(A value);
    }

    /**
     * Update operations for the following types:
     *  - Object Attribute
     *  - Owner Attribute
     *  - Timeseries
     *  - Event Types
     *  - Object Types
     */
    interface UpdateOps<A> {
        /**
         * Update an instance of A that has the matching key
         * @param key of the instance of type A to update
         * @param update the description and display name of the entity
         */
        void update(String key, A update);
    }

    /**
     * Operations to deploy one of the following entity type:
     *  - Object Attribute
     *  - Owner Attribute
     *  - Timeseries
     */
    interface DeployOps {
        /**
         * Initiate the deploy process of an instance A that has the matching key
         * @param key of the instance of type A to initiate deploy
         * @return a code to be used to complete the deploy process
         *
         * @see #applyDeployCode(String, String)
         */
        String generateDeployCode(String key);

        /**
         * Completes the deploy process of an instance A with the matching key
         *
         * This method will throw a HttpClientErrorException if the provided code is invalid
         *
         * @param key of the instance of type A to deploy
         * @param code the code received in the first part of the process
         *
         * @see #generateDeployCode(String)
         * @see org.springframework.web.client.HttpClientErrorException
         */
        void applyDeployCode(String key, String code);

        /**
         * Runs the complete deploy process of an instance A with the matching key.
         * @param key of the instance of type A to deploy
         *
         * @see #applyDeployCode(String, String)
         * @see #generateDeployCode(String)
         */
        void deploy(String key);
    }

    /**
     * Operations related to the reset process of the sandbox data model
     */
    interface ResetOps {
        /**
         * Initiate the reset process of the sandbox data model
         * @return a code to be used to complete the reset process
         *
         * @see #applyResetCode(String)
         */
        String generateResetCode();

        /**
         * Completes the reset process of the sandbox data model
         *
         * This method will throw a HttpClientErrorException if the provided code is invalid
         *
         * @param code the code received in the first part of the process
         *
         * @see #generateResetCode()
         * @see org.springframework.web.client.HttpClientErrorException
         */
        void applyResetCode(String code);

        /**
         * Runs the complete reset process of the sandbox data model
         *
         * @see #applyResetCode(String)
         * @see #generateResetCode()
         */
        void reset();
    }

    /**
     * Restricted updates operations specialized to one of the following entity type:
     *  - Object Attribute
     *  - Owner Attribute
     *  - Timeseries
     */
    interface SandboxEntityOps<A> extends CreateOps<A>, UpdateOps<UpdateEntity>, DeployOps { }

    /**
     * Restricted updates operations specialized to one of the following entity type:
     *  - Object Types
     *  - Event Types
     */
    interface SandboxTypeOps<A> extends CreateOps<A>, UpdateOps<A> {
        /**
         * Delete an instance A that has the matching key
         * @param key of the instance of type A to delete
         */
        void delete(String key);

        /**
         * Add a relation to the entity identified by `key`.
         *
         * - Object Types => `entityKey` is an object attribute key
         * - Event Types => `entityKey` is a timeseries key
         *
         * @param typeKey identifier of the instance of type A
         * @param entityKey identifier of the instance to add a relation to
         */
        A addRelation(String typeKey, String entityKey);

        /**
         * Remove a relation to the entity identified by `key`.
         *
         * - Object Types => key is an object attribute key
         * - Event Types => key is a timeseries key
         *
         * @param typeKey identifier of the instance of type A
         * @param entityKey identifier of the instance to remove a relation to
         */
        void removeRelation(String typeKey, String entityKey);
    }

    /**
     * A simple data class to be serialized as the payload to update requests.
     */
    class UpdateEntity {
        private final String displayName;
        private final String description;

        public UpdateEntity(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UpdateEntity that = (UpdateEntity) o;
            return Objects.equals(displayName, that.displayName) &&
                    Objects.equals(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(displayName, description);
        }
    }
}