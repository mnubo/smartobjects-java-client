package com.mnubo.java.sdk.client.services;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnubo.java.sdk.client.Consumer;
import com.mnubo.java.sdk.client.LocalRestServer;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
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

import com.mnubo.java.sdk.client.models.SmartObject;
import com.mnubo.java.sdk.client.models.result.Result;
import com.mnubo.java.sdk.client.models.result.Result.ResultStates;
import com.mnubo.java.sdk.client.spi.ObjectsSDK;
import org.springframework.web.client.RestTemplate;

public class ObjectsSDKServicesTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final ObjectMapper jsonParser = new ObjectMapper();
    private static final LocalRestServer server = new LocalRestServer(new Consumer<LocalRestServer.LocalRestContext>() {
        @Override
        public void accept(LocalRestServer.LocalRestContext ctx) {
            Restlet createOrUpdateObjectRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    if (request.getMethod().equals(Method.POST)) {
                        val input = jsonParser.readTree(request.getEntityAsText());
                        if (input.isObject() && input.get("x_device_id").isTextual()) {
                            response.setEntity(String.format("{\"x_device_id\":\"%s\"}", input.get("x_device_id").asText()), MediaType.APPLICATION_JSON);
                        } else {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                            response.setEntity("Oops", MediaType.TEXT_PLAIN);
                        }
                    }
                    else if (request.getMethod().equals(Method.PUT)) {
                        val input = jsonParser.readTree(request.getEntityAsText());

                        boolean first = true;
                        val resVal = new StringBuilder("[");
                        for(JsonNode item: input) {
                            if (!first) {
                                resVal.append(",");
                            }

                            final String deviceId = item.get("x_device_id").asText();
                            resVal
                                    .append("{\"id\":\"")
                                    .append(deviceId)
                                    .append("\",\"result\":\"")
                                    .append(!deviceId.equals("deviceId2") ? "success": "error")
                                    .append("\"")
                                    .append(",\"message\":\"")
                                    .append(deviceId.equals("deviceId2") ? "Invalid attribute X for the Object": "")
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

            Restlet deleteOrUpdateObjectRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    if (request.getMethod().equals(Method.DELETE) || request.getMethod().equals(Method.PUT)) {
                        response.setEntity("Ok", MediaType.TEXT_PLAIN);
                    }
                    else {
                        response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
                    }
                }
            };

            Restlet objectExistsRoute = new Restlet(ctx.restletContext) {
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
                                    .append(item.asText().equals("deviceId1") ? "true" : "false")
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

            ctx.router.attach(ctx.baseUrl + "/api/v3/objects", createOrUpdateObjectRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/objects/exists", objectExistsRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/objects/deviceId1", deleteOrUpdateObjectRoute);
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
    private static final CredentialHandler credentials = new CredentialHandler(config, restTemplate);
    private static final SDKService sdkService = new SDKService(restTemplate, credentials, config);
    private static final ObjectsSDK objectsClient = new ObjectsSDKServices(sdkService);

    @AfterClass
    public static void classTearDown() throws Exception {
        server.close();
    }

    @Test
    public void createObjectThenOk() {

        SmartObject object = SmartObject.builder().withObjectType("type").withDeviceId("device").build();

        objectsClient.create(object);
    }

    @Test
    public void createObjectNullThenFail() {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Object body cannot be null.");
        objectsClient.create(null);
    }

    @Test
    public void createObjectObjectTypeNullThenFail() {

        SmartObject object = SmartObject.builder().withObjectType(null).withDeviceId("deviceId").build();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_object_type cannot be blank.");
        objectsClient.create(object);
    }

    @Test
    public void createObjectObjectTypeEmptyThenFail() {

        SmartObject object = SmartObject.builder().withObjectType("").withDeviceId("deviceId").build();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_object_type cannot be blank.");
        objectsClient.create(object);
    }

    @Test
    public void createObjectDeviceIdNullThenFail() {

        SmartObject object = SmartObject.builder().withObjectType("type").withDeviceId(null).build();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_device_id cannot be blank.");
        objectsClient.create(object);
    }

    @Test
    public void createObjectDeviceIdEmptyThenFail() {

        SmartObject object = SmartObject.builder().withObjectType("type").withDeviceId("").build();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_device_id cannot be blank.");
        objectsClient.create(object);
    }

    @Test
    public void deleteObjectThenOk() {
        objectsClient.delete("deviceId1");
    }

    @Test
    public void deleteObjectDeviceIdNullThenFail() {

        String deviceId = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_device_id cannot be blank.");
        objectsClient.delete(deviceId);
    }

    @Test
    public void deleteObjectDeviceIdEmptyThenFail() {

        String deviceId = "";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_device_id cannot be blank.");
        objectsClient.delete(deviceId);
    }

    @Test
    public void updateObjectThenOk() {
        objectsClient.update(SmartObject.builder().build(), "deviceId1");
    }

    @Test
    public void updateObjectDeviceIdNullThenFail() {

        String deviceId = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_device_id cannot be blank.");
        objectsClient.update(SmartObject.builder().withDeviceId(null).build(), deviceId);
    }

    @Test
    public void updateObjectDeviceIdEmptyThenFail() {

        String deviceId = "";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_device_id cannot be blank.");
        objectsClient.update(SmartObject.builder().withDeviceId("").build(), deviceId);
    }

    @Test
    public void updateObjectOwnerNullThenFail() {

        String deviceId = "deviceId";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Object body cannot be null.");
        objectsClient.update(null, deviceId);
    }

    @Test
    public void createUpdateThenOk() {

        List<SmartObject> objects = new ArrayList<>();
        objects.add(SmartObject.builder().withObjectType("type").withDeviceId("deviceId1").build());
        objects.add(SmartObject.builder().withObjectType("type").withDeviceId("deviceId2").build());
        objects.add(SmartObject.builder().withObjectType("type").withDeviceId("deviceId3").build());

        List<Result> results = objectsClient.createUpdate(objects);

        validateResult(results);
    }

    @Test
    public void createUpdateWithVarargThenOk() {
        SmartObject smartObject = SmartObject.builder().withObjectType("type").withDeviceId("deviceId1").build();

        List<Result> results = objectsClient.createUpdate(smartObject);

        validateOneResult(results);
    }

    @Test
    public void existObjectThenOk() {
        assertThat(objectsClient.objectExists("deviceId1"), is(equalTo(true)));
    }

    @Test
    public void existObjectDeviceIdNullThenFail() {
        String deviceId = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("x_device_id cannot be blank.");
        objectsClient.objectExists(deviceId);
    }

    @Test
    public void existObjectsThenOk() {
        List<String> deviceIds = Arrays.asList("deviceId1", "deviceId2");

        Map<String, Boolean> expected = new LinkedHashMap<String, Boolean>(){{
            put("deviceId1", true);
            put("deviceId2", false);
        }};

        assertThat(objectsClient.objectsExist(deviceIds), is(equalTo(expected)));
    }

    @Test
    public void existObjectsDeviceIdNullThenFail() {
        List<String> deviceId = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("List of the deviceIds cannot be null.");
        objectsClient.objectsExist(deviceId);
    }

    private void validateResult(List<Result> results) {
        assertThat(results.size(), equalTo(3));

        assertThat(results.get(0).getId(), equalTo("deviceId1"));
        assertThat(results.get(1).getId(), equalTo("deviceId2"));
        assertThat(results.get(2).getId(), equalTo("deviceId3"));

        assertThat(results.get(0).getResult(), equalTo(ResultStates.success));
        assertThat(results.get(1).getResult(), equalTo(ResultStates.error));
        assertThat(results.get(2).getResult(), equalTo(ResultStates.success));

        assertThat(results.get(0).getMessage(), equalTo(""));
        assertThat(results.get(1).getMessage(), equalTo("Invalid attribute X for the Object"));
        assertThat(results.get(2).getMessage(), equalTo(""));
    }

    private void validateOneResult(List<Result> results) {
        assertThat(results.size(), equalTo(1));

        assertThat(results.get(0).getId(), equalTo("deviceId1"));
        assertThat(results.get(0).getResult(), equalTo(ResultStates.success));
        assertThat(results.get(0).getMessage(), equalTo(""));
    }
}
