package com.mnubo.java.sdk.client.mapper;

import com.mnubo.java.sdk.client.models.datamodel.ObjectAttribute;
import org.junit.Test;

import java.util.Collections;

import static com.mnubo.java.sdk.client.mapper.ObjectMapperConfig.genericObjectMapper;
import static org.junit.Assert.assertEquals;

public class ObjectAttributeSerializingTest {
    @Test
    public void testSerDeser() throws Exception {
        ObjectAttribute owner = new ObjectAttribute("key", "display", "desc", "TEXT", "list", Collections.singleton("oneKey"));
        ObjectAttribute emptyOwner = new ObjectAttribute("key", "display", "desc", "TEXT", "list", Collections.<String>emptySet());

        assertEquals(
            owner,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(owner), ObjectAttribute.class)
        );
        assertEquals(
            emptyOwner,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(emptyOwner), ObjectAttribute.class)
        );
    }
}