package com.mnubo.java.sdk.client.services;

import static com.mnubo.java.sdk.client.Constants.*;
import static com.mnubo.java.sdk.client.utils.ValidationUtils.notBlank;
import static com.mnubo.java.sdk.client.utils.ValidationUtils.validIsFile;
import static com.mnubo.java.sdk.client.utils.ValidationUtils.validNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.mnubo.java.sdk.client.config.ExponentialBackoffConfig;
import org.springframework.web.client.RestTemplate;

import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import com.mnubo.java.sdk.client.spi.MnuboSDKClient;

/**
 * MnuboSDKFactory. This build a MnuboSDKClient instance.
 */
public abstract class MnuboSDKFactory {

    //@formatter:off
    /**
     * returns a mnubo sdk client to send request to mnubo servers from a basic
     * configuration. This uses default parameters
     *
     * This is the list of default parameters set:
     * <b>client.config.platform-port</b>: 443.
     * <b>client.config.autentication-port</b>: 443.
     * <b>client.http.client.disable-redirect-handling</b>: false.
     * <b>client.http.client.disable-automatic-retries</b>: false.
     * <b>client.http.client.disable-content-compression</b>: false.
     * <b>client.http.client.max-connections-per-route</b>: 200.
     * <b>client.http.client.http.client.default-timeout</b>: 15000 ms.
     * <b>client.http.client.http.client.connect-timeout</b>: 15000 ms.
     * <b>client.http.client.connection-request-timeout</b>: 15000 ms.
     * <b>client.http.client.http.client.socket-timeout</b>: 15000 ms.
     * <b>client.http.client.max-total-connection</b>: 200.
     * <b>client.http.client.base-path</b>: /api/v3.
     * <b>client.config.http-protocol</b>: https.
     *
     * @param hostName: mnubo's servers.
     * @param securityConsumerKey: unique identity key provided by mnubo.
     * @param securityConsumerSecret: secret key provided by mnubo.
     * @return MnuboSDKClient: mnubo sdk client instance.
     *
     */
    //@formatter:on
    public static MnuboSDKClient getClient(String hostName, String securityConsumerKey, String securityConsumerSecret) {
        return getClient(hostName, securityConsumerKey, securityConsumerSecret, null);
    }

    public static MnuboSDKClient getClient(String hostName, String securityConsumerKey, String securityConsumerSecret, ExponentialBackoffConfig exponentialBackoffConfig) {
        MnuboSDKConfig.Builder configBuilder = MnuboSDKConfig.builder();

        notBlank(hostName, "hostname property cannot be empty or null.");
        notBlank(securityConsumerKey, "securityConsumerKey property cannot be empty or null.");
        notBlank(securityConsumerSecret, "securityConsumerSecret property cannot be empty or null.");

        configBuilder.withHostName(hostName);
        configBuilder.withSecurityConsumerKey(securityConsumerKey);
        configBuilder.withSecurityConsumerSecret(securityConsumerSecret);
        configBuilder.withExponentionalBackoffConfig(exponentialBackoffConfig);

        return generateClients(configBuilder.build());
    }

    //@formatter:off
    /**
     * returns a mnubo sdk client to send request to mnubo servers from a property
     * instance.
     *
     * This is the list of parameters to set:
     * <b>client.config.hostname</b>: mnubo's server name. For example: "rest.sandbox.mnubo.com".
     * <b>client.security.consumer-key</b>: The unique client identity key.
     * <b>client.security.consumer-secret</b>: The secret key which is used in conjunction with the consumer key to access the mnubo server.
     * <b>client.config.platform-port</b>: mnubo's server port.
     * <b>client.config.autentication-port</b>: Authentication server port.
     * <b>client.http.client.disable-redirect-handling</b>: Disable automatic redirect handling
     * <b>client.http.client.disable-automatic-retries</b>: Disables automatic request recovery and re-execution.
     * <b>client.http.client.disable-content-compression</b>: Disables (gzip) compression of the request content.
     * <b>client.http.client.max-connections-per-route</b>: The maximum number of connections per route.
     * <b>client.http.client.http.client.default-timeout</b>: The number of seconds a session can be idle before it is abandoned.
     * <b>client.http.client.http.client.connect-timeout</b>: This is timeout in seconds until  a connection is established.
     * <b>client.http.client.connection-request-timeout</b>: This is timeout in seconds used when requesting a connection from the connection manager.
     * <b>client.http.client.http.client.socket-timeout</b>: After a connection has been established this is the maximum period inactivity between two consecutive data packets.
     * <b>client.http.client.max-total-connection</b>: This is the maximum number of connections allowed across all routes.
     * <b>client.http.client.base-path</b>: This is the base common path in all request.
     * <b>client.config.http-protocol</b>: Use "http" for unsecure connection and "https" for secure connections.
     *
     * @param properties: property instance.
     * @return MnuboSDKClient: mnubo sdk client instance.
     *
     */
    //@formatter:on
    public static MnuboSDKClient getAdvanceClient(Properties properties) {
        MnuboSDKConfig.Builder configBuilder = MnuboSDKConfig.builder();

        validNotNull(properties, "Configuration properties instance");
        if (properties.containsKey(HOST_NAME)) {
            configBuilder.withHostName(properties.getProperty(HOST_NAME));
        }
        if (properties.containsKey(INGESTION_PORT)) {
            configBuilder.withIngestionPort(properties.getProperty(INGESTION_PORT));
        }
        if (properties.containsKey(RESTITUTION_PORT)) {
            configBuilder.withRestitutionPort(properties.getProperty(RESTITUTION_PORT));
        }
        if (properties.containsKey(AUTHENTICATION_PORT)) {
            configBuilder.withAuthenticationPort(properties.getProperty(AUTHENTICATION_PORT));
        }
        if (properties.containsKey(SECURITY_CONSUMER_KEY)) {
            configBuilder.withSecurityConsumerKey(properties.getProperty(SECURITY_CONSUMER_KEY));
        }
        if (properties.containsKey(SECURITY_CONSUMER_SECRET)) {
            configBuilder.withSecurityConsumerSecret(properties.getProperty(SECURITY_CONSUMER_SECRET));
        }
        if (properties.containsKey(HTTP_PROTOCOL)) {
            configBuilder.withHttpProtocol(properties.getProperty(HTTP_PROTOCOL));
        }
        if (properties.containsKey(CLIENT_DISABLE_REDIRECT_HANDLING)) {
            configBuilder.withHttpDisableRedirectHandling(properties.getProperty(CLIENT_DISABLE_REDIRECT_HANDLING));
        }
        if (properties.containsKey(CLIENT_DISABLE_AUTOMATIC_RETRIES)) {
            configBuilder.withHttpDisableAutomaticRetries(properties.getProperty(CLIENT_DISABLE_AUTOMATIC_RETRIES));
        }
        if (properties.containsKey(CLIENT_DISABLE_CONTENT_COMPRESSION)) {
            configBuilder.withHttpDisableContentCompression(properties.getProperty(CLIENT_DISABLE_CONTENT_COMPRESSION));
        }
        if (properties.containsKey(CLIENT_MAX_CONNECTIONS_PER_ROUTE)) {
            configBuilder.withHttpMaxConnectionPerRoute(properties.getProperty(CLIENT_MAX_CONNECTIONS_PER_ROUTE));
        }
        if (properties.containsKey(CLIENT_DEFAULT_TIMEOUT)) {
            configBuilder.withHttpDefaultTimeout(properties.getProperty(CLIENT_DEFAULT_TIMEOUT));
        }
        if (properties.containsKey(CLIENT_CONNECT_TIMEOUT)) {
            configBuilder.withHttpConnectionTimeout(properties.getProperty(CLIENT_CONNECT_TIMEOUT));
        }
        if (properties.containsKey(CLIENT_CONNECTION_REQUEST_TIMEOUT)) {
            configBuilder.withHttpConnectionRequestTimeout(properties.getProperty(CLIENT_CONNECTION_REQUEST_TIMEOUT));
        }
        if (properties.containsKey(CLIENT_SOCKET_TIMEOUT)) {
            configBuilder.withHttpSocketTimeout(properties.getProperty(CLIENT_SOCKET_TIMEOUT));
        }
        if (properties.containsKey(CLIENT_MAX_TOTAL_CONNECTION)) {
            configBuilder.withHttpMaxTotalConnection(properties.getProperty(CLIENT_MAX_TOTAL_CONNECTION));
        }
        if (properties.containsKey(CLIENT_BASE_PATH)) {
            configBuilder.withHttpBasePath(properties.getProperty(CLIENT_BASE_PATH));
        }

        return generateClients(configBuilder.build());
    }

    //@formatter:off
    /**
     * returns a mnubo sdk client to send request to mnubo servers from a InputStream
     * instance.
     *
     * This is the list of parameters to set:
     * <b>client.config.hostname</b>: mnubo's server name. For example: "rest.sandbox.mnubo.com".
     * <b>client.security.consumer-key</b>: The unique client identity key.
     * <b>client.security.consumer-secret</b>: The secret key which is used in conjunction with the consumer key to access the mnubo server.
     * <b>client.config.platform-port</b>: mnubo's server port.
     * <b>client.config.autentication-port</b>: Authentication server port.
     * <b>client.http.client.disable-redirect-handling</b>: Disable automatic redirect handling
     * <b>client.http.client.disable-automatic-retries</b>: Disables automatic request recovery and re-execution.
     * <b>client.http.client.disable-content-compression</b>: Disables (gzip) compression of the request content.
     * <b>client.http.client.max-connections-per-route</b>: The maximum number of connections per route.
     * <b>client.http.client.http.client.default-timeout</b>: The number of seconds a session can be idle before it is abandoned.
     * <b>client.http.client.http.client.connect-timeout</b>: This is timeout in seconds until  a connection is established.
     * <b>client.http.client.connection-request-timeout</b>: This is timeout in seconds used when requesting a connection from the connection manager.
     * <b>client.http.client.http.client.socket-timeout</b>: After a connection has been established this is the maximum period inactivity between two consecutive data packets.
     * <b>client.http.client.max-total-connection</b>: This is the maximum number of connections allowed across all routes.
     * <b>client.http.client.base-path</b>: This is the base common path in all request.
     * <b>client.config.http-protocol</b>: Use "http" for unsecure connection and "https" for secure connections.
     *
     * @param config: InputStream instance.
     * @return MnuboSDKClient: mnubo sdk client instance.
     * @throws IOException if any exception occurs.
     *
     */
    //@formatter:on
    public static MnuboSDKClient getAdvanceClient(InputStream config) throws IOException {
        validNotNull(config, "configuration streaming instance");
        Properties properties = new Properties();
        properties.load(config);
        return getAdvanceClient(properties);

    }

    //@formatter:off
    /**
     * returns a mnubo sdk client to send request to mnubo servers from a File instance.
     *
     * This is the list of parameters to set:
     * <b>client.config.hostname</b>: mnubo's server name. For example: "rest.sandbox.mnubo.com".
     * <b>client.security.consumer-key</b>: The unique client identity key.
     * <b>client.security.consumer-secret</b>: The secret key which is used in conjunction with the consumer key to access the mnubo server.
     * <b>client.config.platform-port</b>: mnubo's server port.
     * <b>client.config.autentication-port</b>: Authentication server port.
     * <b>client.http.client.disable-redirect-handling</b>: Disable automatic redirect handling
     * <b>client.http.client.disable-automatic-retries</b>: Disables automatic request recovery and re-execution.
     * <b>client.http.client.disable-content-compression</b>: Disables (gzip) compression of the request content.
     * <b>client.http.client.max-connections-per-route</b>: The maximum number of connections per route.
     * <b>client.http.client.http.client.default-timeout</b>: The number of seconds a session can be idle before it is abandoned.
     * <b>client.http.client.http.client.connect-timeout</b>: This is timeout in seconds until  a connection is established.
     * <b>client.http.client.connection-request-timeout</b>: This is timeout in seconds used when requesting a connection from the connection manager.
     * <b>client.http.client.http.client.socket-timeout</b>: After a connection has been established this is the maximum period inactivity between two consecutive data packets.
     * <b>client.http.client.max-total-connection</b>: This is the maximum number of connections allowed across all routes.
     * <b>client.http.client.base-path</b>: This is the base common path in all request.
     * <b>client.config.http-protocol</b>: Use "http" for unsecure connection and "https" for secure connections.
     *
     * @param configFile: File instance.
     * @return MnuboSDKClient: mnubo sdk client instance.
     * @throws IOException if any exception occurs.
     *
     */
    //@formatter:on
    public static MnuboSDKClient getAdvanceClient(File configFile) throws IOException {
        validIsFile(configFile);
        FileInputStream config = new FileInputStream(configFile);
        return getAdvanceClient(config);
    }

    private static MnuboSDKClient generateClients(MnuboSDKConfig config) {

        // configurating custom restTemplate
        RestTemplate restTemplate = new HttpRestTemplate(config).getRestTemplate();

        // credential handler
        CredentialHandler credentials = new CredentialHandler(config, restTemplate);

        return new MnuboSDKClientImpl(config, restTemplate, credentials);
    }

}
