package com.mnubo.java.sdk.client.mapper;

import com.mnubo.java.sdk.client.models.datamodel.Timeseries;
import org.junit.Test;

import java.util.Collections;

import static com.mnubo.java.sdk.client.mapper.ObjectMapperConfig.genericObjectMapper;
import static org.junit.Assert.assertEquals;

public class TimeseriesSerializingTest {
    @Test
    public void testSerDeser() throws Exception {
        Timeseries ts = new Timeseries("key", "display", "desc", "TEXT", Collections.singleton("oneKey"));
        Timeseries emptyTs = new Timeseries("key", "display", "desc", "TEXT", Collections.<String>emptySet());

        assertEquals(
            ts,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(ts), Timeseries.class)
        );

        assertEquals(
            emptyTs,
            genericObjectMapper.readValue(genericObjectMapper.writeValueAsString(emptyTs), Timeseries.class)
        );
    }
}