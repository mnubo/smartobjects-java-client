package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.Consumer;
import com.mnubo.java.sdk.client.LocalRestServer;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import com.mnubo.java.sdk.client.models.Event;

import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Encoding;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.engine.application.EncodeRepresentation;
import org.restlet.representation.Representation;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class SDKClientGzipCompressTest {
    private static final UUID eventId1 = UUID.randomUUID();
    private static final UUID eventId2 = UUID.randomUUID();
    private static final UUID eventId3 = UUID.randomUUID();
    private static final UUID eventId4 = UUID.randomUUID();
    private static final String ResponseString = "{\"ok\":\"true\"}";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final LocalRestServer server = new LocalRestServer(new Consumer<LocalRestServer.LocalRestContext>() {
        @Override
        public void accept(LocalRestServer.LocalRestContext ctx) {
            Restlet oauthRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    response.setEntity("{\"access_token\":\"thetoken\",\"token_type\":\"client\",\"expires_in\":1000000,\"scope\":\"ALL\"}", MediaType.APPLICATION_JSON);
                }
            };

            Restlet compressed = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    String encoding = ((HttpRequest) request).getHeaders().getFirstValue("content-encoding", true);
                    if(encoding == null || !encoding.equals("gzip")){
                        response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        return;
                    }

                    String accept = ((HttpRequest) request).getHeaders().getFirstValue("accept-encoding", true);
                    if(accept == null || !accept.contains("gzip")){
                        response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        return;
                    }

                    response.setEntity(ResponseString, MediaType.APPLICATION_JSON);
                    Representation repr = new EncodeRepresentation(Encoding.GZIP, response.getEntity());
                    response.setEntity(repr);
                }
            };

            Restlet uncompressed = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    String encoding = ((HttpRequest) request).getHeaders().getFirstValue("content-encoding", true);
                    if(encoding != null && encoding.equals("gzip")){
                        response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        return;
                    }

                    response.setEntity(ResponseString, MediaType.APPLICATION_JSON);
                }
            };

            ctx.router.attach(ctx.baseUrl + "/oauth/token", oauthRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/test_only/compressed", compressed);
            ctx.router.attach(ctx.baseUrl + "/api/v3/test_only/uncompressed", uncompressed);
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
    private static final ClientSecretCredentialHandler credentials = new ClientSecretCredentialHandler(config, restTemplate);
    private static final SDKService sdkService = new SDKService(restTemplate, credentials, config);

    @AfterClass
    public static void classTearDown() throws Exception {
        server.close();
    }

    @Test
    public void testCompressionDefault() {
        List<Event> events = new ArrayList<Event>() {{
            add(Event.builder().withEventID(eventId1).withEventType("type").withSmartObject("deviceId1").build());
            add(Event.builder().withEventID(eventId2).withEventType("type").withSmartObject("deviceId2").build());
            add(Event.builder().withEventID(eventId3).withEventType("type").withSmartObject("deviceId3").build());
            add(Event.builder().withEventID(eventId4).withEventType("type").withSmartObject("deviceId4").build());
        }};

        String url = sdkService.getIngestionBaseUri()
                .path("/test_only/compressed")
                .build().toString();
        String text = sdkService.postRequest(url, String.class, events);
        assertThat(text, equalTo(ResponseString));
    }

    @Test
    public void testDisableCompressionForceFalse() {
        MnuboSDKConfig compressedConfig = MnuboSDKConfig
                .builder()
                .withHostName(server.host)
                .withIngestionPort(Integer.toString(server.port))
                .withAuthenticationPort(Integer.toString(server.port))
                .withSecurityConsumerKey("ABC")
                .withSecurityConsumerSecret("ABC")
                .withHttpProtocol("http")
                .withHttpDisableContentCompression("false")
                .build();
        RestTemplate restTemplate = new HttpRestTemplate(compressedConfig).getRestTemplate();
        ClientSecretCredentialHandler credentials = new ClientSecretCredentialHandler(compressedConfig, restTemplate);
        SDKService sdkService = new SDKService(restTemplate, credentials, compressedConfig);

        List<Event> events = new ArrayList<Event>() {{
            add(Event.builder().withEventID(eventId1).withEventType("type").withSmartObject("deviceId1").build());
            add(Event.builder().withEventID(eventId2).withEventType("type").withSmartObject("deviceId2").build());
            add(Event.builder().withEventID(eventId3).withEventType("type").withSmartObject("deviceId3").build());
            add(Event.builder().withEventID(eventId4).withEventType("type").withSmartObject("deviceId4").build());
        }};


        String url = sdkService.getIngestionBaseUri()
                .path("/test_only/compressed")
                .build().toString();
        String text = sdkService.postRequest(url, String.class, events);
        assertThat(text, equalTo(ResponseString));
    }

    @Test
    public void testDisableCompressionForceTrue() {
        MnuboSDKConfig uncompressedConfig = MnuboSDKConfig
                .builder()
                .withHostName(server.host)
                .withIngestionPort(Integer.toString(server.port))
                .withAuthenticationPort(Integer.toString(server.port))
                .withSecurityConsumerKey("ABC")
                .withSecurityConsumerSecret("ABC")
                .withHttpProtocol("http")
                .withHttpDisableContentCompression("true")
                .build();
        RestTemplate restTemplate = new HttpRestTemplate(uncompressedConfig).getRestTemplate();
        ClientSecretCredentialHandler credentials = new ClientSecretCredentialHandler(uncompressedConfig, restTemplate);
        SDKService sdkService = new SDKService(restTemplate, credentials, uncompressedConfig);

        List<Event> events = new ArrayList<Event>() {{
            add(Event.builder().withEventID(eventId1).withEventType("type").withSmartObject("deviceId1").build());
            add(Event.builder().withEventID(eventId2).withEventType("type").withSmartObject("deviceId2").build());
            add(Event.builder().withEventID(eventId3).withEventType("type").withSmartObject("deviceId3").build());
            add(Event.builder().withEventID(eventId4).withEventType("type").withSmartObject("deviceId4").build());
        }};


        String url = sdkService.getIngestionBaseUri()
                .path("/test_only/uncompressed")
                .build().toString();
        String text = sdkService.postRequest(url, String.class, events);
        assertThat(text, equalTo(ResponseString));
    }
}
