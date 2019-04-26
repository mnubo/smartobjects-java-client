package com.mnubo.java.sdk.client.services;

import static com.mnubo.java.sdk.client.Constants.CLIENT_VALIDATE_INACTIVITY_SERVER;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.mnubo.java.sdk.client.mapper.GzipRequestInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import com.mnubo.java.sdk.client.mapper.ObjectMapperConfig;

class HttpRestTemplate {
    final String version = "Java/" + SDKVersionInfo.loadVersion();
    private RestTemplate restTemplate;

    HttpRestTemplate(MnuboSDKConfig config) {
        HttpClient httpClient = getHttpClient(config);
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate = new RestTemplate(requestFactory);
        configureMapper(ObjectMapperConfig.genericObjectMapper);

        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                request.getHeaders().add("X-MNUBO-SDK", version);
                return execution.execute(request, body);
            }
        });

        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            private final ResponseErrorHandler defaultErrorHandler = new DefaultResponseErrorHandler();
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return defaultErrorHandler.hasError(response);
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                try {
                    defaultErrorHandler.handleError(response);
                } catch (HttpClientErrorException clientError) {
                    String bodyContent = clientError.getResponseBodyAsString();
                    String bodyMessage = bodyContent != null && !bodyContent.trim().isEmpty() ? " with body: " + bodyContent : "";
                    String message = clientError.getStatusCode().toString() + " "  + clientError.getStatusText() + bodyMessage;

                    throw new RestClientException(message, clientError);
                }
            }
        });

        if(!config.isHttpDisableContentCompression()){
            restTemplate.getInterceptors().add(new GzipRequestInterceptor());
        }
    }

    private void configureMapper(ObjectMapper objectMapper) {
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                jsonConverter.setObjectMapper(objectMapper);
            }
        }
    }

    private HttpClient getHttpClient(MnuboSDKConfig config) {
        // Setting request config
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(config.getHttpDefaultTimeout())
                .setConnectionRequestTimeout(config.getHttpConnectionRequestTimeout())
                .setDecompressionEnabled(!config.isHttpDisableContentCompression())
                .setSocketTimeout(config.getHttpSoketTimeout()).build();

        // Setting pooling management
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getHttpMaxTotalConnection());
        connectionManager.setDefaultMaxPerRoute(config.getHttpMaxConnectionPerRoute());
        connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost(config.getHostName())),
                config.getHttpMaxConnectionPerRoute());
        connectionManager.setValidateAfterInactivity(CLIENT_VALIDATE_INACTIVITY_SERVER);

        // Building httpclient
        HttpClientBuilder httpClientsBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
        if (config.isHttpDisableCockieManagement()) {
            httpClientsBuilder.disableCookieManagement();
        }
        if (config.isHttpDisableRedirectHandling()) {
            httpClientsBuilder.disableRedirectHandling();
        }
        if (config.isHttpDisableAutomaticRetries()) {
            httpClientsBuilder.disableAutomaticRetries();
        }
        if (config.isHttpDisableContentCompression()) {
            httpClientsBuilder.disableContentCompression();
        }
        if (config.isHttpSystemPropertiesEnable()) {
            httpClientsBuilder.useSystemProperties();
        }
        httpClientsBuilder.setMaxConnPerRoute(config.getHttpMaxConnectionPerRoute())
                .setMaxConnTotal(config.getHttpMaxConnectionPerRoute() * 2).setConnectionManager(connectionManager);

        return httpClientsBuilder.build();
    }

    RestTemplate getRestTemplate() {
        return restTemplate;
    }

    private static class SDKVersionInfo {
        private static String loadVersion() {
            try {
                Properties vprops = new Properties();
                vprops.load(HttpRestTemplate.class.getClassLoader().getResourceAsStream("version.properties"));
                return vprops.getProperty("version", "unknown");
            } catch (IOException e) {
                final String packageVersion = SDKVersionInfo.class.getPackage().getImplementationVersion();
                if (packageVersion != null) {
                    return packageVersion;
                } else {
                    return "unknown-error";
                }

            }
        }
    }
}
