package integration;

import com.mnubo.java.sdk.client.models.Event;
import com.mnubo.java.sdk.client.models.Owner;
import com.mnubo.java.sdk.client.models.SmartObject;
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

import static org.hamcrest.CoreMatchers.equalTo;
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


    private static String SEARCH_OWNER_WITH_PLACEHOLDER;
    private static String SEARCH_OBJECT_WITH_PLACEHOLDER;
    private static String SEARCH_OBJECT_BY_OWNER_WITH_PLACEHOLDER;
    private static String SEARCH_EVENT_WITH_PLACEHOLDER;

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

        try (
                InputStream searchOwnerInput = openResource("search_owner.json");
                InputStream searchObjectInput = openResource("search_object.json");
                InputStream searchObjectByOwnerInput = openResource("search_object_by_owner.json");
                InputStream searchEventInput = openResource("search_event.json")
        ) {
            SEARCH_OWNER_WITH_PLACEHOLDER = IOUtils.toString(searchOwnerInput);
            SEARCH_OBJECT_WITH_PLACEHOLDER = IOUtils.toString(searchObjectInput);
            SEARCH_OBJECT_BY_OWNER_WITH_PLACEHOLDER = IOUtils.toString(searchObjectByOwnerInput);
            SEARCH_EVENT_WITH_PLACEHOLDER = IOUtils.toString(searchEventInput);
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
                        .withPassword("password-" + uuid)
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

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OWNER_WITH_PLACEHOLDER, username));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("username"), equalTo(username));
                assertThat(rows.get(0).getString(OWNER_TEXT_ATTRIBUTE), equalTo(value));
            }
        });

        final String newValue = "newValue";
        final Owner updatedOwner =
                Owner.builder()
                        .withUsername(username)
                        .withAddedAttribute(OWNER_TEXT_ATTRIBUTE, newValue)
                        .build();

        CLIENT.getOwnerClient().update(updatedOwner, username);

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OWNER_WITH_PLACEHOLDER, username));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("username"), equalTo(username));
                assertThat(rows.get(0).getString(OWNER_TEXT_ATTRIBUTE), equalTo(newValue));
            }
        });

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

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OBJECT_WITH_PLACEHOLDER, deviceId));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("x_device_id"), equalTo(deviceId));
                assertThat(rows.get(0).getString(OBJECT_TEXT_ATTRIBUTE), equalTo(value));
            }
        });

        final String newValue = "newValue";
        final SmartObject updatedObject =
                SmartObject.builder()
                        .withDeviceId(deviceId)
                        .withObjectType(OBJECT_TYPE)
                        .withAddedAttribute(OBJECT_TEXT_ATTRIBUTE, newValue)
                        .build();

        CLIENT.getObjectClient().update(updatedObject, deviceId);

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OBJECT_WITH_PLACEHOLDER, deviceId));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("x_device_id"), equalTo(deviceId));
                assertThat(rows.get(0).getString(OBJECT_TEXT_ATTRIBUTE), equalTo(newValue));
            }
        });

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

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_EVENT_WITH_PLACEHOLDER, event1.getEventId()));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("event_id"), equalTo(event1.getEventId().toString()));
                assertThat(rows.get(0).getString(TS_TEXT_ATTRIBUTE), equalTo(value1));

                val result2 = CLIENT.getSearchClient().search(String.format(SEARCH_EVENT_WITH_PLACEHOLDER, event2.getEventId()));
                val rows2 = result2.all();
                assertThat(rows2.size(), equalTo(1));
                assertThat(rows2.get(0).getString("event_id"), equalTo(event2.getEventId().toString()));
                assertThat(rows2.get(0).getString(TS_TEXT_ATTRIBUTE), equalTo(value2));
            }
        });

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

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OWNER_WITH_PLACEHOLDER, username1));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("username"), equalTo(username1));
                assertThat(rows.get(0).getString(OWNER_TEXT_ATTRIBUTE), equalTo(value1));
            }
        });
        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OWNER_WITH_PLACEHOLDER, username2));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("username"), equalTo(username2));
                assertThat(rows.get(0).getString(OWNER_TEXT_ATTRIBUTE), equalTo(value2));
            }
        });

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OBJECT_WITH_PLACEHOLDER, deviceId1));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("x_device_id"), equalTo(deviceId1));
                assertThat(rows.get(0).getString(OBJECT_TEXT_ATTRIBUTE), equalTo(value1));
            }
        });

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val result = CLIENT.getSearchClient().search(String.format(SEARCH_OBJECT_WITH_PLACEHOLDER, deviceId2));
                val rows = result.all();
                assertThat(rows.size(), equalTo(1));
                assertThat(rows.get(0).getString("x_device_id"), equalTo(deviceId2));
                assertThat(rows.get(0).getString(OBJECT_TEXT_ATTRIBUTE), equalTo(value2));
            }
        });
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

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val objResult = CLIENT.getSearchClient().search(String.format(SEARCH_OBJECT_WITH_PLACEHOLDER, deviceId));
                val objRows = objResult.all();
                assertThat(objRows.size(), equalTo(1));

                val ownResult = CLIENT.getSearchClient().search(String.format(SEARCH_OWNER_WITH_PLACEHOLDER, username));
                val ownRows = ownResult.all();
                assertThat(ownRows.size(), equalTo(1));
            }
        });

        try {
            CLIENT.getOwnerClient().claim("unknownuser-" + uuid, deviceId);
            fail("should fail because the username does not exist");
        } catch (HttpStatusCodeException ex) {
            //expected
        }
        try {
            CLIENT.getOwnerClient().claim(username, "unknownDevice-" + uuid);
            fail("should fail because the device id does not exist");
        } catch (HttpStatusCodeException ex) {
            //expected
        }
        try {
            CLIENT.getOwnerClient().claim("unknownuser-" + uuid, "unknownDevice-" + uuid);
            fail("should fail because the username and device id do not exist");
        } catch (HttpStatusCodeException ex) {
            //expected
        }

        CLIENT.getOwnerClient().claim(username, deviceId);

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val objResult = CLIENT.getSearchClient().search(String.format(SEARCH_OBJECT_BY_OWNER_WITH_PLACEHOLDER, username));
                val objRows = objResult.all();
                assertThat(objRows.size(), equalTo(1));
                assertThat(objRows.get(0).getString(OBJECT_TEXT_ATTRIBUTE), equalTo(username));
            }
        });

        try {
            CLIENT.getOwnerClient().unclaim("unknownuser-" + uuid, deviceId);
            fail("should fail because the username does not exist");
        } catch (HttpStatusCodeException ex) {
            //expected
        }
        try {
            CLIENT.getOwnerClient().unclaim(username, "unknownDevice-" + uuid);
            fail("should fail because the device id does not exist");
        } catch (HttpStatusCodeException ex) {
            //expected
        }
        try {
            CLIENT.getOwnerClient().unclaim("unknownuser-" + uuid, "unknownDevice-" + uuid);
            fail("should fail because the username and device id do not exist");
        } catch (HttpStatusCodeException ex) {
            //expected
        }

        CLIENT.getOwnerClient().unclaim(username, deviceId);

        AssertEventually.that(new Eventually() {
            @Override
            public void test() {
                val objResult = CLIENT.getSearchClient().search(String.format(SEARCH_OBJECT_BY_OWNER_WITH_PLACEHOLDER, username));
                val objRows = objResult.all();
                assertThat(objRows.size(), equalTo(0));
            }
        });

        try {
            CLIENT.getOwnerClient().unclaim(username, deviceId);
            fail("should fail because the object is already unclaimed");
        } catch (HttpStatusCodeException ex) {
            //expected
        }

    }

    private interface Eventually {
        void test();
    }

    private static final class AssertEventually {
        static long defaultTimeout = 1000 * 240;
        static long defaultDelay = 5000;
        public static void that(Eventually eventually) {
            that(eventually, defaultTimeout, defaultDelay);
        }

        @SneakyThrows
        public static void that(Eventually eventually, long timeout, long delay) {
            long end = System.currentTimeMillis() + timeout;
            Throwable lastException = null;
            while (System.currentTimeMillis() < end) {
                try {
                    eventually.test();
                    return; //completed
                } catch (AssertionError err) {
                    lastException = err;
                } catch (Exception ex) {
                    log.info("an error occurred: ", ex);
                    fail("eventual assertion threw: " + ex.getMessage());
                }
                Thread.sleep(delay);
            }
            fail("eventually timed out with this last assertion error: " + (lastException != null ? lastException.getMessage() : "none"));
        }
    }

    private static InputStream openResource(String filename) {
        return SdkClientIntegrationTest.class.getClassLoader().getResourceAsStream(filename);
    }
}
