package com.mnubo.java.sdk.client.mapper;

import com.mnubo.java.sdk.client.models.datamodel.EventType;
import org.junit.Test;

import java.util.Collections;

import static com.mnubo.java.sdk.client.mapper.ObjectMapperConfig.genericObjectMapper;
import static org.junit.Assert.assertEquals;

public class EventTypeSerializingTest {

    @Test
    public void testSerDeser() throws Exception {
        EventType et = new EventType("key", "desc", "scheduled", Collections.singleton("oneKey"));
        EventType emptyEt = new EventType("key", "desc", "unscheduled", Collections.<String>emptySet());

        assertEquals(
            et,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(et), EventType.class)
        );

        assertEquals(
            emptyEt,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(emptyEt), EventType.class)
        );
    }
}