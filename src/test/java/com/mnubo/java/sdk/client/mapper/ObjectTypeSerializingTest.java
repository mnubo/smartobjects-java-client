package com.mnubo.java.sdk.client.mapper;

import com.mnubo.java.sdk.client.models.datamodel.ObjectType;
import org.junit.Test;

import java.util.Collections;

import static com.mnubo.java.sdk.client.mapper.ObjectMapperConfig.genericObjectMapper;
import static org.junit.Assert.*;

public class ObjectTypeSerializingTest {
    @Test
    public void testSerDeser() throws Exception {
        ObjectType ot = new ObjectType("key", "desc", Collections.singleton("oneKey"));
        ObjectType emptyOt = new ObjectType("key", "desc", Collections.<String>emptySet());

        assertEquals(
            ot,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(ot), ObjectType.class)
        );

        assertEquals(
            emptyOt,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(emptyOt), ObjectType.class)
        );
    }
}