package com.mnubo.java.sdk.client.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnubo.java.sdk.client.Consumer;
import com.mnubo.java.sdk.client.LocalRestServer;
import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import com.mnubo.java.sdk.client.models.Owner;
import com.mnubo.java.sdk.client.models.result.Result;
import com.mnubo.java.sdk.client.models.result.Result.ResultStates;
import com.mnubo.java.sdk.client.spi.OwnersSDK;
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

public class OwnersSDKServicesTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final ObjectMapper jsonParser = new ObjectMapper();
    private static final LocalRestServer server = new LocalRestServer(new Consumer<LocalRestServer.LocalRestContext>() {
        @Override
        public void accept(LocalRestServer.LocalRestContext ctx) {
            Restlet createOrUpdateOwnerRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    if (request.getMethod().equals(Method.POST)) {
                        val input = jsonParser.readTree(request.getEntityAsText());
                        if (input.isObject() && input.get("username").isTextual()) {
                            response.setEntity(String.format("{\"username\":\"%s\"}", input.get("username").asText()), MediaType.APPLICATION_JSON);
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

                            final String username = item.get("username").asText();
                            resVal
                                    .append("{\"id\":\"")
                                    .append(username)
                                    .append("\",\"result\":\"")
                                    .append(!username.equals("user2") ? "success": "error")
                                    .append("\"")
                                    .append(",\"message\":\"")
                                    .append(username.equals("user2") ? "Invalid attribute X for the Owner": "")
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

            Restlet ownerClaimObjectRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    if (request.getMethod().equals(Method.POST)) {
                        response.setEntity(null, MediaType.TEXT_PLAIN);
                    }
                    else {
                        response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
                    }
                }
            };

            Restlet ownerUnclaimObjectRoute = new Restlet(ctx.restletContext) {
                @Override
                @SneakyThrows
                public void handle(org.restlet.Request request, Response response) {
                    if (request.getMethod().equals(Method.POST)) {
                        response.setEntity(null, MediaType.TEXT_PLAIN);
                    }
                    else {
                        response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
                    }
                }
            };

            Restlet deleteOrUpdateOwnerRoute = new Restlet(ctx.restletContext) {
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

            Restlet ownerExistsRoute = new Restlet(ctx.restletContext) {
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
                                    .append(item.asText().equals("username1") ? "true" : "false")
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

            ctx.router.attach(ctx.baseUrl + "/api/v3/owners", createOrUpdateOwnerRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/owners/exists", ownerExistsRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/owners/username1/objects/deviceId1/claim", ownerClaimObjectRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/owners/username1/objects/deviceId1/unclaim", ownerUnclaimObjectRoute);
            ctx.router.attach(ctx.baseUrl + "/api/v3/owners/username1", deleteOrUpdateOwnerRoute);
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
    private static final OwnersSDK ownerClient = new OwnersSDKServices(sdkService);

    @AfterClass
    public static void classTearDown() throws Exception {
        server.close();
    }

    @Test
    public void createOwnerThenOk() {
        Owner owner = Owner.builder().withUsername("test").build();

        ownerClient.create(owner);
    }

    @Test
    public void createOwnerNullThenFail() {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Owner body cannot be null.");
        ownerClient.create(null);
    }

    @Test
    public void createOwnerUsernameNullThenFail() {

        Owner owner = Owner.builder().withUsername(null).build();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.create(owner);
    }

    @Test
    public void createOwnerUsernameEmptyThenFail() {

        Owner owner = Owner.builder().withUsername("").build();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.create(owner);
    }

    @Test
    public void ownerClaimingObjectUsernameNullThenFail() {

        String username = null;
        String deviceId = "deviceId";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.claim(username, deviceId);
    }

    @Test
    public void ownerClaimingObjectUsernameEmptyThenFail() {

        String username = "";
        String deviceId = "deviceId";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.claim(username, deviceId);
    }

    @Test
    public void ownerClaimingObjectDeviceNullThenFail() {

        String username = "username";
        String deviceId = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("deviceId cannot be blank.");
        ownerClient.claim(username, deviceId);
    }

    @Test
    public void ownerClaimingObjectDeviceEmptyThenFail() {

        String username = "username";
        String deviceId = "";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("deviceId cannot be blank.");
        ownerClient.claim(username, deviceId);
    }

    @Test
    public void ownerClaimingObjectThenOk() {
        ownerClient.claim("username1", "deviceId1");
    }

    @Test
    public void ownerUnClaimingObjectThenOk() {
        ownerClient.unclaim("username1", "deviceId1");
    }

    @Test
    public void ownerUnclaimingObjectUsernameNullThenFail() {

        String username = null;
        String deviceId = "deviceId";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.unclaim(username, deviceId);
    }

    @Test
    public void ownerUnclaimingObjectUsernameEmptyThenFail() {

        String username = "";
        String deviceId = "deviceId";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.unclaim(username, deviceId);
    }

    @Test
    public void ownerUnclaimingObjectDeviceNullThenFail() {

        String username = "username";
        String deviceId = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("deviceId cannot be blank.");
        ownerClient.unclaim(username, deviceId);
    }

    @Test
    public void ownerUnclaimingObjectDeviceEmptyThenFail() {

        String username = "username";
        String deviceId = "";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("deviceId cannot be blank.");
        ownerClient.unclaim(username, deviceId);
    }

    @Test
    public void deleteOwnerThenOk() {
        ownerClient.delete("username1");
    }

    @Test
    public void deleteOwnerUsernameNullThenFail() {
        String username = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.delete(username);
    }

    @Test
    public void deleteOwnerUsernameEmptyThenFail() {

        String username = "";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.delete(username);
    }

    @Test
    public void updateOwnerThenOk() {
        ownerClient.update(Owner.builder().build(), "username1");
    }

    @Test
    public void updateOwnerUsernameNullThenFail() {

        String username = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.update(Owner.builder().withUsername(null).build(), username);
    }

    @Test
    public void updateOwnerUsernameEmptyThenFail() {

        String username = "";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");
        ownerClient.update(Owner.builder().withUsername("").build(), username);
    }

    @Test
    public void updateOwnerOwnerNullThenFail() {

        String username = "username";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Owner body cannot be null.");
        ownerClient.update(null, username);
    }

    @Test
    public void createUpdateThenOk() {
        List<Owner> owners = new ArrayList<>();
        owners.add(Owner.builder().withUsername("user1").build());
        owners.add(Owner.builder().withUsername("user2").build());
        owners.add(Owner.builder().withUsername("user3").build());

        List<Result> results = ownerClient.createUpdate(owners);

        validateResults(results);
    }

    @Test
    public void createUpdateWithVarargThenOk() {
        Owner owner = Owner.builder().withUsername("user1").build();

        List<Result> results = ownerClient.createUpdate(owner);

        validateOneResult(results);
    }

    @Test
    public void ownerExistsThenOk() {
        assertThat(ownerClient.ownerExists("username1"), is(equalTo(true)));
        assertThat(ownerClient.ownerExists("username2"), is(equalTo(false)));
    }

    @Test
    public void existObjectDeviceIdNullThenFail() {
        String usernames = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("username cannot be blank.");

        assertThat(ownerClient.ownerExists(usernames), is(equalTo(false)));
    }

    @Test
    public void existOwnersThenOk() {
        List<String> usernames = Arrays.asList(
                "username1",
                "username2"
        );

        Map<String, Boolean> expected = new LinkedHashMap<String, Boolean>(){{
            put("username1", true);
            put("username2", false);
        }};

        assertThat(ownerClient.ownersExist(usernames), is(equalTo(expected)));
    }

    @Test
    public void existObjectsDeviceIdNullThenFail() {
        List<String> usernames = null;

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("List of the usernames cannot be null.");
        ownerClient.ownersExist(usernames);
    }

    private void validateResults(List<Result> results) {
        assertThat(results.size(), equalTo(3));

        assertThat(results.get(0).getId(), equalTo("user1"));
        assertThat(results.get(1).getId(), equalTo("user2"));
        assertThat(results.get(2).getId(), equalTo("user3"));

        assertThat(results.get(0).getResult(), equalTo(ResultStates.success));
        assertThat(results.get(1).getResult(), equalTo(ResultStates.error));
        assertThat(results.get(2).getResult(), equalTo(ResultStates.success));

        assertThat(results.get(0).getMessage(), equalTo(""));
        assertThat(results.get(1).getMessage(), equalTo("Invalid attribute X for the Owner"));
        assertThat(results.get(2).getMessage(), equalTo(""));
    }

    private void validateOneResult(List<Result> results) {
        assertThat(results.size(), equalTo(1));

        assertThat(results.get(0).getId(), equalTo("user1"));
        assertThat(results.get(0).getResult(), equalTo(ResultStates.success));
        assertThat(results.get(0).getMessage(), equalTo(""));
    }
}
