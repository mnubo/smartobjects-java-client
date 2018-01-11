package com.mnubo.java.sdk.client.mapper;

import com.mnubo.java.sdk.client.models.datamodel.OwnerAttribute;
import org.junit.Test;

import static com.mnubo.java.sdk.client.mapper.ObjectMapperConfig.genericObjectMapper;
import static org.junit.Assert.assertEquals;

public class OwnerAttributeSerializingTest {
    @Test
    public void testSerialize() throws Exception {
        OwnerAttribute owner = new OwnerAttribute("key", "display", "desc", "TEXT", "list");

        assertEquals(
            "Json serialization does not match",
            "{\"key\":\"key\",\"displayName\":\"display\",\"description\":\"desc\",\"type\":{\"highLevelType\":\"TEXT\",\"containerType\":\"list\"}}",
            genericObjectMapper.writeValueAsString(owner)
        );
    }
}