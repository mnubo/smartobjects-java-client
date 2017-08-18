package com.mnubo.java.sdk.client.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnubo.java.sdk.client.Consumer;
import com.mnubo.java.sdk.client.LocalRestServer;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import com.mnubo.java.sdk.client.models.Event;
import com.mnubo.java.sdk.client.models.result.Result;
import com.mnubo.java.sdk.client.models.result.Result.ResultStates;
import com.mnubo.java.sdk.client.spi.EventsSDK;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class EventsSDKServicesTest {
    private static final UUID eventId1 = UUID.randomUUID();
    private static final UUID eventId2 = UUID.randomUUID();
    private static final UUID eventId3 = UUID.randomUUID();
    private static final UUID eventId4 = UUID.randomUUID();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final ObjectMapper jsonParser = new ObjectMapper();
    private static final LocalRestServer server = new LocalRestServer(new Consumer<LocalRestServer.LocalRestContext>() {
        @Override
        public void accept(LocalRestServer.LocalRestContext ctx) {
            Restlet postEventsRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    if (request.getMethod().equals(Method.POST)) {
                        val input = jsonParser.readTree(request.getEntityAsText());
                        val resVal = new StringBuilder("[");
                        boolean first = true;
                        for(JsonNode item: input) {
                            if (!first) {
                                resVal.append(",");
                            }

                            final String deviceId = item.get("x_object").get("x_device_id").asText();
                            resVal
                                    .append("{\"id\":\"")
                                    .append(item.get("event_id").asText())
                                    .append("\",\"result\":\"")
                                    .append(deviceId.equals("deviceId1") ? "success": (deviceId.equals("deviceId3") ? "conflict": (deviceId.equals("deviceId2") ? "notfound": "error")))
                                    .append("\"")
                                    .append(",\"message\":\"")
                                    .append(deviceId.equals("deviceId2") ? "Object for the Event doesn't exist": (deviceId.equals("deviceId1") ? "": "Simple error"))
                                    .append("\"")
                                    .append("}");

                            first = false;
                        }
                        resVal.append("]");
                        response.setEntity(resVal.toString(), MediaType.APPLICATION_JSON);

                    }
                    else {
                        response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
                    }
                }
            };

            Restlet eventsExistsRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    if (request.getMethod().equals(Method.POST)) {
                        val input = jsonParser.readTree(request.getEntityAsText());
                        val resVal = new StringBuilder("[");
                        boolean first = true;
                        for(JsonNode item: input) {
                            if (!first) {
                                resVal.append(",");
                            }

                            resVal
                                    .append("{\"")
                                    .append(item.asText())
                                    .append("\":")
                                    .append(item.asText().equals(eventId1.toString()) ? "true" : "false")
                                    .append("}");

                            first = false;
                        }
                        resVal.append("]");
                        response.setEntity(resVal.toString(), MediaType.APPLICATION_JSON);
                    }
                    else {
                        response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
                    }

                }
            };

            Restlet oauthRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    response.setEntity("{\"access_token\":\"thetoken\",\"token_type\":\"client\",\"expires_in\":1000000,\"scope\":\"ALL\"}", MediaType.APPLICATION_JSON);
                }
            };

            ctx.router.attach(ctx.baseUrl + "/api/v3/events", postEventsRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/objects/deviceId1/events", postEventsRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/events/exists", eventsExistsRoute);
            ctx.router.attach(ctx.baseUrl + "/oauth/token", oauthRoute);
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
    private static final EventsSDK eventsClient = new EventsSDKServices(sdkService);

    @AfterClass
    public static void classTearDown() throws Exception {
        server.close();
    }
    @Test
    public void postEventListThenOk() {
        List<Event> events = new ArrayList<Event>() {{
            add(Event.builder().withEventID(eventId1).withEventType("type").withSmartObject("deviceId1").build());
            add(Event.builder().withEventID(eventId2).withEventType("type").withSmartObject("deviceId2").build());
            add(Event.builder().withEventID(eventId3).withEventType("type").withSmartObject("deviceId3").build());
            add(Event.builder().withEventID(eventId4).withEventType("type").withSmartObject("deviceId4").build());
        }};

        List<Result> results = eventsClient.send(events);

        validateResult(results);
    }

    @Test
    public void postVarargEventThenOk() {
        Event event = Event.builder().withEventID(eventId1).withEventType("type").withSmartObject("deviceId1").build();

        List<Result> results = eventsClient.send(event);

        validateOneResult(results);
    }

    @Test
    public void postListEventListEmptyThenFail() {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Event list cannot be null or empty.");
        eventsClient.send(new ArrayList<Event>());
    }

    @Test
    public void postListEventWithDeviceIdThenOk() {
        List<Event> events = new ArrayList<Event>() {{
            add(Event.builder().withEventID(eventId1).withEventType("type").withSmartObject("deviceId1").build());
        }};

        List<Result> results = eventsClient.send("deviceId1", events);

        validateOneResult(results);
    }

    @Test
    public void postListEventNullThenFail() {

        String deviceId = "deviceId";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Event list cannot be null or empty.");
        eventsClient.send(deviceId, null);
    }

    @Test
    public void postListEventEmptyThenFail() {

        String deviceId = "deviceId";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Event list cannot be null or empty.");
        eventsClient.send(deviceId, new ArrayList<Event>());
    }

    @Test
    public void postListEventWithDeviceIdNullThenFail() {

        List<Event> events = new ArrayList<>();

        events.add(Event.builder().withEventType("type").build());

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("device_Id cannot be blank.");
        eventsClient.send(null, events);
    }

    @Test
    public void postListEventWithDeviceIdEmptyThenFail() {

        List<Event> events = new ArrayList<>();

        events.add(Event.builder().withEventType("type").build());
        String deviceId = "";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("device_Id cannot be blank.");
        eventsClient.send(deviceId, events);
    }

    private void validateResult(List<Result> results) {
        assertThat(results.size(), equalTo(4));

        assertThat(results.get(0).getId(), equalTo(eventId1.toString()));
        assertThat(results.get(1).getId(), equalTo(eventId2.toString()));
        assertThat(results.get(2).getId(), equalTo(eventId3.toString()));
        assertThat(results.get(3).getId(), equalTo(eventId4.toString()));

        assertThat(results.get(0).getResult(), equalTo(ResultStates.success));
        assertThat(results.get(1).getResult(), equalTo(ResultStates.notfound));
        assertThat(results.get(2).getResult(), equalTo(ResultStates.conflict));
        assertThat(results.get(3).getResult(), equalTo(ResultStates.error));

        assertThat(results.get(0).getMessage(), equalTo(""));
        assertThat(results.get(1).getMessage(), equalTo("Object for the Event doesn't exist"));
        assertThat(results.get(2).getMessage(), equalTo("Simple error"));
        assertThat(results.get(3).getMessage(), equalTo("Simple error"));
    }

    private void validateOneResult(List<Result> results) {
        assertThat(results.size(), equalTo(1));

        assertThat(results.get(0).getId(), equalTo(eventId1.toString()));
        assertThat(results.get(0).getResult(), equalTo(ResultStates.success));
        assertThat(results.get(0).getMessage(), equalTo(""));
    }

    @Test
    public void eventExistsThenOk() {
        assertThat(eventsClient.eventExists(UUID.randomUUID()), is(equalTo(false)));
        assertThat(eventsClient.eventExists(eventId1), is(equalTo(true)));
    }

    @Test
    public void eventExistsEventIdNullThenFail() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("eventId cannot be blank.");
        eventsClient.eventExists(null);
    }

    @Test
    public void eventsExistThenOk() {
        final UUID unknown = UUID.randomUUID();
        final List<UUID> eventId = Arrays.asList(unknown, eventId1);

        Map<UUID, Boolean> expected = new LinkedHashMap<UUID, Boolean>(){{
            put(unknown, false);
            put(eventId1, true);
        }};

        assertThat(eventsClient.eventsExist(eventId), is(equalTo(expected)));
    }

    @Test
    public void eventsExistEventIdNullThenFail() {
        List<UUID> eventId = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("List of the eventIds cannot be null.");
        eventsClient.eventsExist(eventId);
    }
}
