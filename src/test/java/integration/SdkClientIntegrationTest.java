package integration;

import com.mnubo.java.sdk.client.models.ClaimOrUnclaim;
import com.mnubo.java.sdk.client.models.Event;
import com.mnubo.java.sdk.client.models.Owner;
import com.mnubo.java.sdk.client.models.SmartObject;
import com.mnubo.java.sdk.client.models.datamodel.Model;
import com.mnubo.java.sdk.client.models.result.Result;
import com.mnubo.java.sdk.client.services.MnuboSDKFactory;
import com.mnubo.java.sdk.client.spi.MnuboSDKClient;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class SdkClientIntegrationTest {
    private final static Log log = LogFactory.getLog(SdkClientIntegrationTest.class);

    private final String OWNER_TEXT_ATTRIBUTE = "owner_text_attribute";
    private final String OBJECT_TEXT_ATTRIBUTE = "object_text_attribute";
    private final String TS_TEXT_ATTRIBUTE = "ts_text_attribute";
    private final String OBJECT_TYPE = "object_type1";
    private final String EVENT_TYPE = "event_type1";


    private static String CONSUMER_KEY;
    private static String CONSUMER_SECRET;
    private static final String HOSTNAME = "rest.sandbox.mnubo.com";
    private static MnuboSDKClient CLIENT;


    static {
        try (InputStream input = openResource("credentials.properties")) {
            final Properties props = new Properties();
            props.load(input);

            CONSUMER_KEY = props.getProperty("consumer.key");
            CONSUMER_SECRET = props.getProperty("consumer.secret");
            CLIENT = MnuboSDKFactory.getClient(HOSTNAME, CONSUMER_KEY, CONSUMER_SECRET);
        } catch (IOException e) {
            System.out.println("TEST INIT FAILED");
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        if (CONSUMER_KEY == null || CONSUMER_SECRET == null || CLIENT == null) {
            log.error("Test initialization failed.");
            throw new IllegalStateException("Test initialization failed.");
        }
    }

    @Test
    public void owner() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String username = "username-" + uuid;
        final String usernameToDelete = "usernameToDelete-" + uuid;
        final String value = "value-" + uuid;

        assertThat(CLIENT.getOwnerClient().ownerExists(username), equalTo(false));

        final Owner validOwner =
                Owner.builder()
                        .withUsername(username)
                        .withPassword("rpasswod-" + uuid)
                        .withAddedAttribute(OWNER_TEXT_ATTRIBUTE, value)
                        .build();

        final Owner ownerToDelete =
                Owner.builder()
                        .withUsername(usernameToDelete)
                        .withPassword("password-" + uuid)
                        .build();

        CLIENT.getOwnerClient().create(ownerToDelete);
        CLIENT.getOwnerClient().create(validOwner);

        final Owner invalidOwner =
                Owner.builder()
                        .withUsername("username-" + UUID.randomUUID())
                        .withPassword("password-" + UUID.randomUUID())
                        .withAddedAttribute("unknown", "value")
                        .build();
        try {
            CLIENT.getOwnerClient().create(invalidOwner);
            fail("should fail because the payload contain an unknown owner attribute");
        } catch (HttpStatusCodeException ex) {
            //expected
        }

        final String newValue = "newValue";
        final Owner updatedOwner =
                Owner.builder()
                        .withUsername(username)
                        .withAddedAttribute(OWNER_TEXT_ATTRIBUTE, newValue)
                        .build();

        CLIENT.getOwnerClient().update(updatedOwner, username);

        assertThat(CLIENT.getOwnerClient().ownerExists(username), equalTo(true));

        CLIENT.getOwnerClient().delete(ownerToDelete.getUsername());
    }

    @Test
    public void objects() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String deviceId = "deviceId-" + uuid;
        final String deviceIdToDelete = "deviceIdToDelete-" + uuid;
        final String value = "value-" + uuid;

        assertThat(CLIENT.getObjectClient().objectExists(deviceId), equalTo(false));
        assertThat(
                CLIENT.getObjectClient().objectsExist(Collections.singletonList(deviceId)),
                equalTo(Collections.singletonMap(deviceId, false))
        );

        final SmartObject validObject =
                SmartObject.builder()
                        .withDeviceId(deviceId)
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute(OBJECT_TEXT_ATTRIBUTE, value)
                        .build();

        final SmartObject objectToDelete =
                SmartObject.builder()
                        .withDeviceId(deviceIdToDelete)
                        .withObjectType(OBJECT_TYPE)
                        .build();

        CLIENT.getObjectClient().create(objectToDelete);
        CLIENT.getObjectClient().create(validObject);

        final SmartObject invalidOwner =
                SmartObject.builder()
                        .withDeviceId("deviceId-" + UUID.randomUUID())
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute("unknown", "value")
                        .build();
        try {
            CLIENT.getObjectClient().create(invalidOwner);
        } catch (HttpStatusCodeException ex) {
            //expected
        }

        final String newValue = "newValue";
        final SmartObject updatedObject =
                SmartObject.builder()
                        .withDeviceId(deviceId)
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute(OBJECT_TEXT_ATTRIBUTE, newValue)
                        .build();

        CLIENT.getObjectClient().update(updatedObject, deviceId);

        assertThat(CLIENT.getObjectClient().objectExists(deviceId), equalTo(true));
        assertThat(
                CLIENT.getObjectClient().objectsExist(Collections.singletonList(deviceId)),
                equalTo(Collections.singletonMap(deviceId, true))
        );

        CLIENT.getObjectClient().delete(objectToDelete.getDeviceId());

    }

    @Test
    public void events() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String deviceId = "deviceId-" + uuid;
        final SmartObject validObject =
                SmartObject.builder()
                        .withDeviceId(deviceId)
                        .withObjectType(OBJECT_TYPE)
                        .build();
        CLIENT.getObjectClient().create(validObject);


        final UUID id1 = UUID.randomUUID();
        final String value1 = "value-" + id1;
        final Event event1 =
                Event.builder()
                        .withEventID(id1)
                        .withSmartObject(validObject.getDeviceId())
                        .withEventType(EVENT_TYPE)
                        .withAddedTimeseries(TS_TEXT_ATTRIBUTE, value1)
                        .build();

        final UUID id2 = UUID.randomUUID();
        final String value2 = "value-" + id2;
        final Event event2 =
                Event.builder()
                        .withEventID(id2)
                        .withSmartObject(validObject.getDeviceId())
                        .withEventType(EVENT_TYPE)
                        .withAddedTimeseries(TS_TEXT_ATTRIBUTE, value2)
                        .build();

        assertThat(CLIENT.getEventClient().eventExists(event1.getEventId()), equalTo(false));
        assertThat(
                CLIENT.getEventClient().eventsExist(Collections.singletonList(event1.getEventId())),
                equalTo(Collections.singletonMap(event1.getEventId(), false))
        );

        val eventResult = CLIENT.getEventClient().send(event1, event2);
        assertThat(eventResult.get(0).getResult(), equalTo(Result.ResultStates.success));
        assertThat(eventResult.get(1).getResult(), equalTo(Result.ResultStates.success));

        assertThat(CLIENT.getEventClient().eventExists(event1.getEventId()), equalTo(true));
        assertThat(
                CLIENT.getEventClient().eventsExist(Collections.singletonList(event1.getEventId())),
                equalTo(Collections.singletonMap(event1.getEventId(), true))
        );

    }

    @Test
    public void batching() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String value1 = "value1";
        final String value2 = "value2";

        final String username1 = "username-batch1-" + uuid;
        final String username2 = "username-batch2-" + uuid;
        final List<Owner> owners = Arrays.asList(
                Owner.builder()
                        .withUsername(username1)
                        .withPassword("password")
                        .withAddedAttribute(OWNER_TEXT_ATTRIBUTE, value1)
                        .build(),
                Owner.builder()
                        .withUsername(username2)
                        .withPassword("password")
                        .withAddedAttribute(OWNER_TEXT_ATTRIBUTE, value2)
                        .build()
        );
        final List<Result> createdOwners = CLIENT.getOwnerClient().createUpdate(owners);
        assertThat(createdOwners.size(), equalTo(2));

        final String deviceId1 = "deviceId-batch1-" + uuid;
        final String deviceId2 = "deviceId-batch2-" + uuid;
        final List<SmartObject> objects = Arrays.asList(
                SmartObject.builder()
                        .withDeviceId(deviceId1)
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute(OBJECT_TEXT_ATTRIBUTE, value1)
                        .build(),
                SmartObject.builder()
                        .withDeviceId(deviceId2)
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute(OBJECT_TEXT_ATTRIBUTE, value2)
                        .build()
        );
        final List<Result> createdObjects = CLIENT.getObjectClient().createUpdate(objects);
        assertThat(createdObjects.size(), equalTo(2));
    }

    @Test
    public void claimAndUnclaim() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String username = "username-" + uuid;
        final String otherUsername = "otherUsername-" + uuid;
        final String deviceId = "deviceId-" + uuid;

        final Owner validOwner =
                Owner.builder()
                        .withUsername(username)
                        .withPassword("password-" + uuid)
                        .build();
        final Owner validOtherOwner =
                Owner.builder()
                        .withUsername(otherUsername)
                        .withPassword("password-" + uuid)
                        .build();

        final SmartObject validObject =
                SmartObject.builder()
                        .withDeviceId(deviceId)
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute(OBJECT_TEXT_ATTRIBUTE, username)
                        .build();

        CLIENT.getObjectClient().create(validObject);
        CLIENT.getOwnerClient().create(validOwner);
        CLIENT.getOwnerClient().create(validOtherOwner);

        final ClaimOrUnclaim unknownUser= new ClaimOrUnclaim("unknownuser-" + uuid, deviceId, null);
        final ClaimOrUnclaim unknownDevice= new ClaimOrUnclaim(username, "unknownDevice-" + uuid, null);
        final ClaimOrUnclaim bothUnknown= new ClaimOrUnclaim("unknownuser-" + uuid, "unknownDevice-" + uuid, null);

        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(unknownUser)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(unknownDevice)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(bothUnknown)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );

        final ClaimOrUnclaim validClaim = new ClaimOrUnclaim(username, deviceId, null);
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(validClaim)).get(0).getResult(),
                equalTo(Result.ResultStates.success)
        );
        final ClaimOrUnclaim validClaimWithBody = new ClaimOrUnclaim(username, deviceId, Collections.<String, Object>singletonMap("x_timestamp", "2015-01-22T00:01:25-02:00"));
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(validClaimWithBody)).get(0).getResult(),
                equalTo(Result.ResultStates.success)
        );
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.<ClaimOrUnclaim>emptyList()).size(),
                equalTo(0)
        );

        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(unknownUser)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(unknownDevice)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(bothUnknown)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );

        final ClaimOrUnclaim validUnclaim = new ClaimOrUnclaim(username, deviceId, null);
        CLIENT.getOwnerClient().batchUnclaim(Collections.<ClaimOrUnclaim>emptyList());
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.<ClaimOrUnclaim>emptyList()).size(),
                equalTo(0)
        );
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(validUnclaim)).get(0).getResult(),
                equalTo(Result.ResultStates.success)
        );

        //should fail because the object is already unclaimed
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(validUnclaim)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );


    }

    @Test
    public void batchclaimAndUnclaim() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String username = "username-" + uuid;
        final String otherUsername = "otherUsername-" + uuid;
        final String deviceId = "deviceId-" + uuid;

        final Owner validOwner =
                Owner.builder()
                        .withUsername(username)
                        .withPassword("password-" + uuid)
                        .build();
        final Owner validOtherOwner =
                Owner.builder()
                        .withUsername(otherUsername)
                        .withPassword("password-" + uuid)
                        .build();

        final SmartObject validObject =
                SmartObject.builder()
                        .withDeviceId(deviceId)
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute(OBJECT_TEXT_ATTRIBUTE, username)
                        .build();

        CLIENT.getObjectClient().create(validObject);
        CLIENT.getOwnerClient().create(validOwner);
        CLIENT.getOwnerClient().create(validOtherOwner);

        final ClaimOrUnclaim unknownUser= new ClaimOrUnclaim("unknownuser-" + uuid, deviceId, null);
        final ClaimOrUnclaim unknownDevice= new ClaimOrUnclaim(username, "unknownDevice-" + uuid, null);
        final ClaimOrUnclaim bothUnknown= new ClaimOrUnclaim("unknownuser-" + uuid, "unknownDevice-" + uuid, null);

        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(unknownUser)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(unknownDevice)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(bothUnknown)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );

        final ClaimOrUnclaim validClaim = new ClaimOrUnclaim(username, deviceId, null);
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(validClaim)).get(0).getResult(),
                equalTo(Result.ResultStates.success)
        );
        final ClaimOrUnclaim validClaimWithBody = new ClaimOrUnclaim(username, deviceId, Collections.<String, Object>singletonMap("x_timestamp", "2015-01-22T00:01:25-02:00"));
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.singletonList(validClaimWithBody)).get(0).getResult(),
                equalTo(Result.ResultStates.success)
        );
        assertThat(
                CLIENT.getOwnerClient().batchClaim(Collections.<ClaimOrUnclaim>emptyList()).size(),
                equalTo(0)
        );


        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(unknownUser)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(unknownDevice)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(bothUnknown)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );

        final ClaimOrUnclaim validUnclaim = new ClaimOrUnclaim(username, deviceId, null);
        CLIENT.getOwnerClient().batchUnclaim(Collections.<ClaimOrUnclaim>emptyList());
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.<ClaimOrUnclaim>emptyList()).size(),
                equalTo(0)
        );
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(validUnclaim)).get(0).getResult(),
                equalTo(Result.ResultStates.success)
        );

        //should fail because the object is already unclaimed
        assertThat(
                CLIENT.getOwnerClient().batchUnclaim(Collections.singletonList(validUnclaim)).get(0).getResult(),
                equalTo(Result.ResultStates.error)
        );


    }

    @Test
    public void exportModel() throws Exception {
        final Model model = CLIENT.getModelClient().export();

        assertThat(model, is(not(nullValue())));

        assertThat(model.getEventTypes().size(), equalTo(2));

        assertThat(model.getObjectTypes().size(), equalTo(1));

        assertThat(model.getTimeseries().size(), equalTo(2));

        assertThat(model.getObjectAttributes().size(), equalTo(1));

        assertThat(model.getOwnerAttributes().size(), equalTo(1));

        assertThat(model.getSessionizers().size(), equalTo(1));
    }

    private static InputStream openResource(String filename) {
        return SdkClientIntegrationTest.class.getClassLoader().getResourceAsStream(filename);
    }
}
