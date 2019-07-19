package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.Consumer;
import com.mnubo.java.sdk.client.LocalRestServer;
import com.mnubo.java.sdk.client.config.ExponentialBackoffConfig;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import com.mnubo.java.sdk.client.config.OnRetryCallback;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Router;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.fail;

public class ExponentialBackoffRetriesTest {

    private static final Map<String, Integer> counts = new HashMap<>();
    private static final Map<String, Integer> limits = new HashMap<>();

    private static final LocalRestServer server = new LocalRestServer(new Consumer<LocalRestServer.LocalRestContext>() {
        @Override
        public void accept(LocalRestServer.LocalRestContext ctx) {
            Restlet auth = new Restlet(ctx.restletContext) {
                private final String token = "{\"access_token\":\"eyJhbGciOiJSUzI1NiJ9\",\"token_type\":\"Bearer\",\"expires_in\":3887999,\"scope\":\"ALL\",\"jti\":\"974c90f0-d8b7-4753-bce0-91a8b64c97fe\"}";

                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    response.setStatus(Status.SUCCESS_OK);
                    response.setEntity(token, MediaType.APPLICATION_JSON);
                }
            };
            Restlet internalError = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    response.setStatus(Status.SERVER_ERROR_INTERNAL);
                    response.setEntity("Oops", MediaType.TEXT_PLAIN);
                }
            };
            Restlet unpredictedError = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    //unknown status that triggers a different RestClientException
                    response.setStatus(Status.valueOf(495));
                    response.setEntity("Oops", MediaType.TEXT_PLAIN);
                }
            };
            Restlet success = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    bumpCount("success");
                    response.setStatus(Status.SUCCESS_OK);
                    response.setEntity("OK", MediaType.TEXT_PLAIN);
                }
            };
            Restlet route = new Restlet(ctx.restletContext) {
                final String content = "{\"data\":\"hehe\"}";

                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    String path = request.getAttributes().get("path").toString();
                    bumpCount(path);
                    Integer currentCount = counts.get(path) != null ? counts.get(path) : 0;
                    Integer currentLimit = limits.get(path) != null ? limits.get(path) : 0;

                    if (currentCount > currentLimit) {
                        System.out.println("OK");
                        response.setStatus(Status.SUCCESS_OK);
                        response.setEntity(content, MediaType.APPLICATION_JSON);

                    } else {
                        System.out.println("Service Unavailable");
                        response.setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
                        response.setEntity("Service Unavailable", MediaType.TEXT_PLAIN);
                    }
                }
            };
            ctx.router.setDefaultMatchingMode(Router.MODE_FIRST_MATCH);

            ctx.router.attach(ctx.baseUrl + "/oauth/token", auth);
            ctx.router.attach(ctx.baseUrl + "/unpredictedError", unpredictedError);
            ctx.router.attach(ctx.baseUrl + "/internalError", internalError);
            ctx.router.attach(ctx.baseUrl + "/success", success);
            ctx.router.attach(ctx.baseUrl + "/unavailable/{path}", route);
        }
    });



    @AfterClass
    public static void classTearDown() throws Exception {
        server.close();
    }

    @Test
    public void shouldRetry() {
        final AtomicInteger counter = new AtomicInteger(0);
        final String path = "shouldRetry";
        setLimit(path, 3);
        final ExponentialBackoffConfig cfg = new ExponentialBackoffConfig(5, 500, new OnRetryCallback() {
            @Override
            public void onRetry(int attempt) {
                System.out.println("Attempt #" + attempt);
                counter.incrementAndGet();
            }
        });
        client(cfg).postRequest(server.baseUrl + "/unavailable/" + path);
        Assert.assertThat(counter.get(), CoreMatchers.equalTo(3));
        Assert.assertThat(limits.get(path), CoreMatchers.equalTo(3));
    }

    @Test(expected = HttpServerErrorException.class)
    public void shouldNotRetryOnInternalServerError() {
        client(ExponentialBackoffConfig.DEFAULT).postRequest(server.baseUrl + "/internalError");
    }

    @Test(expected = UnknownHttpStatusCodeException.class)
    public void shouldNotRetryOnRandomError() {
        client(ExponentialBackoffConfig.DEFAULT).postRequest(server.baseUrl + "/unpredictedError");
    }

    @Test
    public void shouldNotRetryWhenNotConfigured() {
        setLimit("success", -1);
        client(null).postRequest(server.baseUrl + "/success");
    }

    @Test
    public void shouldNotRetryMoreThanMaxAttempts() {
        final AtomicInteger counter = new AtomicInteger(0);
        final String path = "shouldNotRetryMoreThanMaxAttempts";
        setLimit(path, 3);
        final ExponentialBackoffConfig cfg = new ExponentialBackoffConfig(5, 500, new OnRetryCallback() {
            @Override
            public void onRetry(int attempt) {
                System.out.println("Attempt #" + attempt);
                counter.incrementAndGet();
            }
        });

        try {
            client(cfg).postRequest(server.baseUrl + "/unavailable/" + path);
        } catch (HttpServerErrorException x) {
            Assert.assertThat(counter.get(), CoreMatchers.equalTo(6));
            Assert.assertThat(limits.get(path), CoreMatchers.equalTo(6));
        } catch (Exception ex) {
            fail("Unexpected exception");
        }
    }

    private static void bumpCount(String path) {
        Integer currentCount = counts.get(path) != null ? counts.get(path) : 0;
        counts.put(path, currentCount + 1);
    }

    private static void setLimit(String path, Integer limit) {
        limits.put(path, limit);
    }

    private SDKService client(ExponentialBackoffConfig cfg) {
        final MnuboSDKConfig config = MnuboSDKConfig
                .builder()
                .withHostName(server.host)
                .withIngestionPort(Integer.toString(server.port))
                .withAuthenticationPort(Integer.toString(server.port))
                .withSecurityConsumerKey("ABC")
                .withSecurityConsumerSecret("ABC")
                .withHttpProtocol("http")
                .withExponentionalBackoffConfig(cfg)
                .build();


        final RestTemplate restTemplate = new HttpRestTemplate(config).getRestTemplate();
        final ClientSecretCredentialHandler credentials = new ClientSecretCredentialHandler(config, restTemplate);
        final SDKService sdkService = new SDKService(restTemplate, credentials, config);
        return sdkService;
    }

}
