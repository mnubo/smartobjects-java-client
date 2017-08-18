package com.mnubo.java.sdk.client.config;

import static com.mnubo.java.sdk.client.Constants.*;
import static com.mnubo.java.sdk.client.utils.ValidationUtils.*;

public class MnuboSDKConfig {

    // default values
    public final static String DEFAULT_SCOPE = "ALL";
    public final static boolean DEFAULT_DISABLE_COOKIE_MANAGEMENT = true;
    public final static boolean DEFAULT_SYSTEM_PROPERTIES_ENABLE = true;

    // mandatory variables
    private final String hostName;

    // client and secret or token are valid
    private final String securityConsumerKey;
    private final String securityConsumerSecret;
    private final String token;

    // optional variables
    private final int platformPort;
    private final int restitutionPort;
    private final int authenticationPort;
    private final String scope = DEFAULT_SCOPE;
    private final String httpProtocol;
    private final boolean httpDisableCockieManagement = DEFAULT_DISABLE_COOKIE_MANAGEMENT;
    private final boolean httpDisableRedirectHandling;
    private final boolean httpDisableAutomaticRetries;
    private final boolean httpSystemPropertiesEnable = DEFAULT_SYSTEM_PROPERTIES_ENABLE;
    private final boolean httpDisableContentCompression;
    private final int httpMaxTotalConnection;
    private final int httpMaxConnectionPerRoute;
    private final int httpDefaultTimeout;
    private final int httpConnectionTimeout;
    private final int httpConnectionRequestTimeout;
    private final int httpSocketTimeout;
    private final String httpBasePath;
    private final ExponentialBackoffConfig exponentialBackoffConfig;

    private MnuboSDKConfig(String hostName, String securityConsumerKey, String securityConsumerSecret, String token, int platformPort,
                           int restitutionPort, int authenticationPort, String httpProtocol, int httpDefaultTimeout,
                           boolean httpDisableRedirectHandling, String httpBasePath,
                           boolean httpDisableAutomaticRetries, int httpSocketTimeout,
                           int httpMaxTotalConnection, int httpMaxConnectionPerRoute, int httpConnectionTimeout,
                           int httpConnectionRequestTimeout, boolean httpDisableContentCompression, ExponentialBackoffConfig exponentialBackoffConfig) {

        this.hostName = parseAsString(hostName, HOST_NAME);
        this.securityConsumerKey = securityConsumerKey;
        this.securityConsumerSecret = securityConsumerSecret;
        this.token = token;
        this.platformPort = parseAsPort(Integer.toString(platformPort), INGESTION_PORT);
        this.restitutionPort = parseAsPort(Integer.toString(restitutionPort), RESTITUTION_PORT);
        this.authenticationPort = parseAsPort(Integer.toString(authenticationPort), AUTHENTICATION_PORT);
        this.httpProtocol = parseAsHttpProtocol(httpProtocol);
        this.httpDefaultTimeout = parseAsInteger(Integer.toString(httpDefaultTimeout), CLIENT_DEFAULT_TIMEOUT);
        this.httpDisableRedirectHandling = parseAsBoolean(Boolean.toString(httpDisableRedirectHandling),
                CLIENT_DISABLE_REDIRECT_HANDLING);
        this.httpBasePath = parseAsString(httpBasePath, CLIENT_BASE_PATH);
        this.httpDisableAutomaticRetries = parseAsBoolean(Boolean.toString(httpDisableAutomaticRetries),
                CLIENT_DISABLE_AUTOMATIC_RETRIES);
        this.httpMaxTotalConnection = parseAsInteger(Integer.toString(httpMaxTotalConnection),
                CLIENT_MAX_TOTAL_CONNECTION);
        this.httpConnectionRequestTimeout = parseAsInteger(Integer.toString(httpConnectionRequestTimeout),
                CLIENT_CONNECTION_REQUEST_TIMEOUT);
        this.httpMaxConnectionPerRoute = parseAsInteger(Integer.toString(httpMaxConnectionPerRoute),
                CLIENT_MAX_CONNECTIONS_PER_ROUTE);
        this.httpDisableContentCompression = parseAsBoolean(Boolean.toString(httpDisableContentCompression), CLIENT_DISABLE_CONTENT_COMPRESSION);
        this.httpConnectionTimeout = parseAsInteger(Integer.toString(httpConnectionTimeout), CLIENT_CONNECT_TIMEOUT);
        this.httpSocketTimeout = parseAsInteger(Integer.toString(httpSocketTimeout), CLIENT_SOCKET_TIMEOUT);
        this.exponentialBackoffConfig = exponentialBackoffConfig;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPlatformPort() {
        return platformPort;
    }

    public int getRestitutionPort() {
        return restitutionPort;
    }

    public int getAuthenticationPort() {
        return authenticationPort;
    }

    public String getSecurityConsumerKey() {
        return securityConsumerKey;
    }

    public String getSecurityConsumerSecret() {
        return securityConsumerSecret;
    }

    public String getScope() {
        return scope;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public boolean isHttpDisableCockieManagement() {
        return httpDisableCockieManagement;
    }

    public boolean isHttpDisableRedirectHandling() {
        return httpDisableRedirectHandling;
    }

    public boolean isHttpDisableAutomaticRetries() {
        return httpDisableAutomaticRetries;
    }

    public boolean isHttpDisableContentCompression() {
        return httpDisableContentCompression;
    }

    public boolean isHttpSystemPropertiesEnable() {
        return httpSystemPropertiesEnable;
    }

    public int getHttpMaxConnectionPerRoute() {
        return httpMaxConnectionPerRoute;
    }

    public int getHttpDefaultTimeout() {
        return httpDefaultTimeout;
    }

    public int getHttpConnectionRequestTimeout() {
        return httpConnectionRequestTimeout;
    }

    /**
     * @see com.mnubo.java.sdk.client.config.MnuboSDKConfig#getHttpSocketTimeout()
     */
    @Deprecated
    public int getHttpSoketTimeout() {
        return httpSocketTimeout;
    }

    public int getHttpSocketTimeout() {
        return httpSocketTimeout;
    }

    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    public int getHttpMaxTotalConnection() {
        return httpMaxTotalConnection;
    }

    public String getHttpBasePath() {
        return httpBasePath;
    }

    public ExponentialBackoffConfig getExponentialBackoffConfig() {
        return exponentialBackoffConfig;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getToken() {
        return token;
    }

    public static class Builder {
        // default values
        public final static int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 200;
        public final static int DEFAULT_MAX_TOTAL_CONNECTIONS = 200;
        public final static int DEFAULT_TIMEOUT = 50000;
        public final static int DEFAULT_HOST_PORT = 443;
        public final static String DEFAULT_CLIENT_PROTOCOL = "https";
        public final static String DEFAULT_BASE_PATH = "/api/v3";
        public final static boolean DEFAULT_DISABLE_REDIRECT_HANDLING = false;
        public final static boolean DEFAULT_DISABLE_AUTOMATIC_RETRIES = false;
        public final static boolean DEFAULT_DISABLE_CONTENT_COMPRESSION = false;

        private String hostName;
        private String securityConsumerKey;
        private String securityConsumerSecret;
        private String token;
        private int platformPort = DEFAULT_HOST_PORT;
        private int restitutionPort = DEFAULT_HOST_PORT;
        private int authenticationPort = DEFAULT_HOST_PORT;
        private String httpProtocol = DEFAULT_CLIENT_PROTOCOL;
        private boolean httpDisableRedirectHandling = DEFAULT_DISABLE_REDIRECT_HANDLING;
        private boolean httpDisableAutomaticRetries = DEFAULT_DISABLE_AUTOMATIC_RETRIES;
        private boolean httpDisableContentCompression = DEFAULT_DISABLE_CONTENT_COMPRESSION;
        private int httpMaxTotalConnection = DEFAULT_MAX_TOTAL_CONNECTIONS;
        private int httpMaxConnectionPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
        private int httpDefaultTimeout = DEFAULT_TIMEOUT;
        private int httpConnectionTimeout = DEFAULT_TIMEOUT;
        private int httpConnectionRequestTimeout = DEFAULT_TIMEOUT;
        private int httpSocketTimeout = DEFAULT_TIMEOUT;
        private String basePath = DEFAULT_BASE_PATH;
        private ExponentialBackoffConfig exponentialBackoffConfig;

        public Builder withHostName(String platformServer) {
            this.hostName = parseAsString(platformServer, HOST_NAME);
            return this;
        }

        public Builder withIngestionPort(String ingestionPort) {
            this.platformPort = parseAsPort(ingestionPort, INGESTION_PORT);
            return this;
        }

        public Builder withRestitutionPort(String restitutionPort) {
            this.restitutionPort = parseAsPort(restitutionPort, RESTITUTION_PORT);
            return this;
        }

        public Builder withAuthenticationPort(String authenticationPort) {
            this.authenticationPort = parseAsPort(authenticationPort, AUTHENTICATION_PORT);
            return this;
        }

        public Builder withSecurityConsumerKey(String securityConsumerkey) {
            this.securityConsumerKey = parseAsString(securityConsumerkey, SECURITY_CONSUMER_KEY);
            return this;
        }

        public Builder withSecurityConsumerSecret(String securityConsumerSecret) {
            this.securityConsumerSecret = parseAsString(securityConsumerSecret, SECURITY_CONSUMER_SECRET);
            return this;
        }

        public Builder withToken(String token) {
            notBlank(token, "The token should not be empty");
            this.token = token;
            return this;
        }

        public Builder withHttpProtocol(String httpProtocol) {
            this.httpProtocol = parseAsHttpProtocol(httpProtocol);
            return this;
        }

        public Builder withHttpDisableRedirectHandling(String httpDisableRedirectHandling) {
            this.httpDisableRedirectHandling =
                    parseAsBoolean(httpDisableRedirectHandling, CLIENT_DISABLE_REDIRECT_HANDLING);
            return this;
        }

        public Builder withHttpDisableContentCompression(String httpDisableContentCompression) {
            this.httpDisableContentCompression = parseAsBoolean(httpDisableContentCompression,
                    CLIENT_DISABLE_CONTENT_COMPRESSION);
            return this;
        }

        public Builder withHttpDisableAutomaticRetries(String httpDisableAutomaticRetries) {
            this.httpDisableAutomaticRetries = parseAsBoolean(httpDisableAutomaticRetries,
                    CLIENT_DISABLE_AUTOMATIC_RETRIES);
            return this;
        }

        public Builder withHttpMaxConnectionPerRoute(String maxConnectionPerRoute) {
            this.httpMaxConnectionPerRoute = parseAsInteger(maxConnectionPerRoute, CLIENT_MAX_CONNECTIONS_PER_ROUTE);
            return this;
        }

        public Builder withHttpDefaultTimeout(String httpDefaultTimeout) {
            this.httpDefaultTimeout = parseAsInteger(httpDefaultTimeout, CLIENT_DEFAULT_TIMEOUT);
            return this;
        }

        public Builder withHttpConnectionTimeout(String httpConnectionTiemout) {
            this.httpConnectionTimeout = parseAsInteger(httpConnectionTiemout, CLIENT_CONNECT_TIMEOUT);
            return this;
        }

        public Builder withHttpConnectionRequestTimeout(String httpConnectionRequestTimeout) {
            this.httpConnectionRequestTimeout = parseAsInteger(httpConnectionRequestTimeout,
                    CLIENT_CONNECTION_REQUEST_TIMEOUT);
            return this;
        }

        public Builder withHttpMaxTotalConnection(String maxTotalConnection) {
            this.httpMaxTotalConnection = parseAsInteger(maxTotalConnection, CLIENT_MAX_TOTAL_CONNECTION);
            return this;
        }

        public Builder withHttpSocketTimeout(String httpSocketTimeout) {
            this.httpSocketTimeout = parseAsInteger(httpSocketTimeout, CLIENT_SOCKET_TIMEOUT);
            return this;
        }

        public Builder withHttpBasePath(String basePath) {
            this.basePath = parseAsString(basePath, CLIENT_BASE_PATH);
            return this;
        }

        public Builder withExponentionalBackoffConfig(ExponentialBackoffConfig config) {
            this.exponentialBackoffConfig = config;
            return this;
        }

        public MnuboSDKConfig build() {
            return new MnuboSDKConfig(hostName, securityConsumerKey, securityConsumerSecret, token, platformPort, restitutionPort,
                    authenticationPort, httpProtocol, httpDefaultTimeout, httpDisableRedirectHandling, basePath,
                    httpDisableAutomaticRetries, httpSocketTimeout, httpMaxTotalConnection, httpMaxConnectionPerRoute,
                    httpConnectionTimeout, httpConnectionRequestTimeout, httpDisableContentCompression, exponentialBackoffConfig);
        }
    }

}
