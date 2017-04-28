package com.mnubo.java.sdk.client.mapper;

import com.mnubo.java.sdk.client.models.ClaimOrUnclaim;
import org.junit.Test;

import java.util.Collections;

import static com.mnubo.java.sdk.client.mapper.ObjectMapperConfig.genericObjectMapper;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClaimOrUnclaimSerializeTest extends AbstractSerializerTest  {

    @Test
    public void testSerialize() throws Exception {
        ClaimOrUnclaim claim = new ClaimOrUnclaim("username", "deviceId");

        String claimResult = genericObjectMapper.writeValueAsString(claim);
        assertThat(claimResult, equalTo("{\"username\":\"username\",\"x_device_id\":\"deviceId\"}"));

        ClaimOrUnclaim claimWithAttribute = new ClaimOrUnclaim(
                "username", "deviceId",
                Collections.<String, Object>singletonMap("x_timestamp", "value")
        );
        String claimWithAttributeResult = genericObjectMapper.writeValueAsString(claimWithAttribute);
        assertThat(claimWithAttributeResult, equalTo("{\"username\":\"username\",\"x_device_id\":\"deviceId\",\"x_timestamp\":\"value\"}"));
    }
}
