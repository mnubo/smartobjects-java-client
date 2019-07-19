package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.config.ExponentialBackoffConfig;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

class SDKService {

    private RestTemplate template;
    private final CredentialHandler credential;
    private final MnuboSDKConfig config;
    private final RetryTemplate retry;

    SDKService(RestTemplate template, CredentialHandler credential, MnuboSDKConfig config) {
        this.credential = credential;
        this.template = template;
        this.config = config;
        this.retry = configureRetry(config);
    }

    void postRequest(final String url) {
        final HttpEntity<?> request = new HttpEntity<Object>(buildHeaders());

        possiblyRetry(new ToCall<Void>() {
            @Override
            public Void run() {
                template.postForEntity(url, request, String.class);
                return null;
            }
        });
    }

    void postRequest(final String url, final Object object) {
        final HttpEntity<?> request = new HttpEntity<>(object, buildHeaders());

        possiblyRetry(new ToCall<Void>() {
            @Override
            public Void run() {
                template.postForEntity(url, request, Void.class);
                return null;
            }
        });
    }

    <T> T postRequest(final String url, final Class<T> objectClass, final Object object) {
        final HttpEntity<?> request = new HttpEntity<>(object, buildHeaders());

        return possiblyRetry(new ToCall<T>() {
            @Override
            public T run() {
                return template.postForObject(url, request, objectClass);
            }
        });
    }

    <T> T postRequest(final String url, final ParameterizedTypeReference<T> type, final Object object) {
        final HttpEntity<?> request = new HttpEntity<>(object, buildHeaders());

        return possiblyRetry(new ToCall<T>() {
            @Override
            public T run() {
                final ResponseEntity<T> response = template.exchange(url, HttpMethod.POST, request, type);
                if (response == null) {
                    return null;
                } else {
                    return response.getBody();
                }
            }
        });
    }

    void putRequest(final String url, final Object object) {
        final HttpEntity<?> request = new HttpEntity<>(object, buildHeaders());

        possiblyRetry(new ToCall<Void>() {
            @Override
            public Void run() {
                template.put(url, request);
                return null;
            }
        });
    }

    <T> T putRequest(final String url, final Object object, final Class<T> objectClass) {
        final HttpEntity<?> request = new HttpEntity<>(object, buildHeaders());

        return possiblyRetry(new ToCall<T>() {
            @Override
            public T run() {
                final ResponseEntity<T> response = template.exchange(url, HttpMethod.PUT, request, objectClass);
                if (response == null) {
                    return null;
                } else {
                    return response.getBody();
                }
            }
        });
    }

    <T> T getRequest(final String url, final Class<T> objectClass) {
        final HttpEntity<?> request = new HttpEntity<Object>(buildHeaders());

        return possiblyRetry(new ToCall<T>() {
            @Override
            public T run() {
                final ResponseEntity<T> response = template.exchange(url, HttpMethod.GET, request, objectClass);
                if (response == null) {
                    return null;
                } else {
                    return response.getBody();
                }
            }
        });
    }

    <T> T getRequest(final String url, final ParameterizedTypeReference<T> type) {
        final HttpEntity<?> request = new HttpEntity<Object>(buildHeaders());

        return possiblyRetry(new ToCall<T>() {
            @Override
            public T run() {
                final ResponseEntity<T> response = template.exchange(url, HttpMethod.GET, request, type);
                if (response == null) {
                    return null;
                } else {
                    return response.getBody();
                }
            }
        });
    }

    void deleteRequest(final String url) {
        final HttpEntity<?> request = new HttpEntity<Object>(buildHeaders());

        possiblyRetry(new ToCall<Void>() {
            @Override
            public Void run() {
                template.exchange(url, HttpMethod.DELETE, request, String.class);
                return null;
            }
        });
    }

    HttpHeaders buildHeaders() {
        // header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", credential.getAutorizationToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    MnuboSDKConfig getConfig() {
        return config;
    }

    UriComponentsBuilder getIngestionBaseUri() {
        return UriComponentsBuilder.newInstance().host(getConfig().getHostName())
                .port(getConfig().getPlatformPort()).scheme(getConfig().getHttpProtocol())
                .path(getConfig().getHttpBasePath());
    }

    UriComponentsBuilder getRestitutionBaseUri() {
        return UriComponentsBuilder.newInstance().host(getConfig().getHostName())
                .port(getConfig().getRestitutionPort()).scheme(getConfig().getHttpProtocol())
                .path(getConfig().getHttpBasePath());
    }

    UriComponentsBuilder getModelBaseUri() {
        return UriComponentsBuilder.newInstance()
                .host(getConfig().getHostName())
                .port(443)
                .scheme("https")
                .path(getConfig().getHttpBasePath())
                .path("/model");
    }

    private <T> T possiblyRetry(final ToCall<T> toCall) {
        if (this.retry != null) {
            return retry.execute(new RetryCallback<T, RestClientException>() {
                @Override
                public T doWithRetry(RetryContext context) throws RestClientException {
                    return toCall.run();
                }
            });
        } else {
            return toCall.run();
        }
    }

    private RetryTemplate configureRetry(final MnuboSDKConfig config) {
        final ExponentialBackoffConfig exponentialBackoffConfig = config.getExponentialBackoffConfig();

        if (exponentialBackoffConfig == null) {
            return null;
        } else {
            final RetryListener listener = new RetryListenerSupport() {
                @Override
                public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                    exponentialBackoffConfig.getOnRetry().onRetry(context.getRetryCount());
                    super.onError(context, callback, throwable);
                }
            };

            final RetryTemplate template = new RetryTemplate();

            final ExponentialRandomBackOffPolicy policy = new ExponentialRandomBackOffPolicy();
            policy.setInitialInterval(exponentialBackoffConfig.getInitialDelay());
            policy.setMaxInterval(60000L);
            policy.setMultiplier(exponentialBackoffConfig.getMultiplier());

            final RetryPolicy retryPolicy = new SimpleRetryPolicy(
                    exponentialBackoffConfig.getNumberOfAttempts(),
                    Collections.<Class<? extends Throwable>, Boolean> singletonMap(Exception.class, true)) {
                @Override
                public boolean canRetry(RetryContext context) {
                    Throwable t = context.getLastThrowable();
                    if (t == null && context.getRetryCount() <= 0) {
                        return true;
                    } else if (t instanceof HttpServerErrorException) {
                        HttpServerErrorException serverError = (HttpServerErrorException) t;
                        return (serverError.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE &&
                                context.getRetryCount() < this.getMaxAttempts());
                    } else {
                        return false;
                    }
                }
            };


            template.setBackOffPolicy(policy);
            template.setRetryPolicy(retryPolicy);
            template.setListeners(new RetryListener[]{listener});
            template.setThrowLastExceptionOnExhausted(true);

            return template;
        }

    }

    private interface ToCall<T> {
        T run();
    }

}
