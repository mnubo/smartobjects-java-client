package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.Consumer;
import com.mnubo.java.sdk.client.LocalRestServer;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ErrorHandlingTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final LocalRestServer server = new LocalRestServer(new Consumer<LocalRestServer.LocalRestContext>() {
        @Override
        public void accept(LocalRestServer.LocalRestContext ctx) {
            Restlet errorRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    response.setEntity("Oops an error occurred", MediaType.TEXT_PLAIN);
                    response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                }
            };

            ctx.router.attach(ctx.baseUrl + "/api/v3/events", errorRoute);
        }
    });

    private static final MnuboSDKConfig config = MnuboSDKConfig
            .builder()
            .withHostName(server.host)
            .withIngestionPort(Integer.toString(server.port))
            .withAuthenticationPort(Integer.toString(server.port))
            .withSecurityConsumerKey("ABC")
            .withSecurityConsumerSecret("ABC")
            .withHttpProtocol("http")
            .build();
    private static final RestTemplate restTemplate = new HttpRestTemplate(config).getRestTemplate();

    @AfterClass
    public static void classTearDown() throws Exception {
        server.close();
    }

    @Test
    public void rewriteErrorMessageWithBody() {
        expectedException.expect(RestClientException.class);
        expectedException.expectMessage("400 Bad Request with body: Oops an error occurred");
        restTemplate.postForLocation(server.baseUrl + "/api/v3/events", "payload");
    }
}
