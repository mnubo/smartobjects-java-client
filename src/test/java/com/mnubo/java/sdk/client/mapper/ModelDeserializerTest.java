package com.mnubo.java.sdk.client.mapper;

import com.google.common.collect.Sets;
import com.mnubo.java.sdk.client.models.datamodel.*;
import lombok.val;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelDeserializerTest extends AbstractSerializerTest {

    @Test
    public void testDeserialize() throws Exception {
        Model model = ObjectMapperConfig.genericObjectMapper.readValue(ModelDeserializerTest.class.getClassLoader().getResourceAsStream("model.json"), Model.class);

        assertThat(model, is(not(nullValue())));

        assertThat(model.getEventTypes().size(), equalTo(2));
        val tsKeys = Sets.newHashSet("ts_number_attribute", "ts_text_attribute");
        val tsKeys2 = Sets.newHashSet("ts_text_attribute");
        assertThat(model.getEventTypes(), hasItems(
                new EventType("event_type1", "desc", "scheduled", tsKeys),
                new EventType("event_type2", "desc", "rule", tsKeys2)
        ));

        val otKeys = Sets.newHashSet("object_text_attribute", "object_int_attribute");
        assertThat(model.getObjectTypes().size(), equalTo(1));
        assertThat(model.getObjectTypes(), hasItems(
                new ObjectType("object_type1", "desc", otKeys)
        ));

        assertThat(model.getTimeseries().size(), equalTo(2));
        val etKeys = Sets.newHashSet("event_type2", "event_type1");
        System.out.println(model.getTimeseries());
        assertThat(model.getTimeseries(), hasItems(
                new Timeseries("ts_text_attribute", "dp ts_text_attribute", "desc ts_text_attribute", "TEXT", etKeys),
                new Timeseries("ts_number_attribute", "dp ts_number_attribute", "desc ts_number_attribute", "DOUBLE", Collections.singleton("event_type1"))
        ));

        assertThat(model.getObjectAttributes().size(), equalTo(2));
        assertThat(model.getObjectAttributes(), hasItems(
                new ObjectAttribute("object_text_attribute", "dp object_text_attribute", "desc object_text_attribute", "TEXT", "none", Collections.singleton("object_type1")),
                new ObjectAttribute("object_int_attribute", "dp object_int_attribute", "desc object_int_attribute", "INT", "list", Collections.singleton("object_type1"))
        ));

        assertThat(model.getOwnerAttributes().size(), equalTo(1));
        assertThat(model.getOwnerAttributes(), hasItems(
                new OwnerAttribute("owner_text_attribute", "dp owner_text_attribute", "desc owner_text_attribute", "TEXT", "none")
        ));

        assertThat(model.getSessionizers().size(), equalTo(1));
        assertThat(model.getSessionizers(), hasItems(
                new Sessionizer("sessionizer", "dp sessionizer", "desc sessionizer", "event_type1", "event_type2")
        ));


        assertThat(model.getOrphans().getTimeseries(), hasItems(
                new Timeseries("orphan_ts", "dp orphan_ts", "desc orphan_ts", "ACCELERATION", Collections.<String>emptySet())
        ));
        assertThat(model.getOrphans().getObjectAttributes(), hasItems(
                new ObjectAttribute("orphan_object", "dp orphan_object", "desc orphan_object", "EMAIL", "none", Collections.<String>emptySet())
        ));
    }


    @Test
    public void testDeserializeEmptyModel() throws Exception {
        Model model = ObjectMapperConfig.genericObjectMapper.readValue(ModelDeserializerTest.class.getClassLoader().getResourceAsStream("empty_model.json"), Model.class);

        assertThat(model, is(not(nullValue())));

        assertThat(model.getEventTypes().size(), equalTo(0));

        assertThat(model.getObjectTypes().size(), equalTo(0));

        assertThat(model.getTimeseries().size(), equalTo(0));

        assertThat(model.getObjectAttributes().size(), equalTo(0));

        assertThat(model.getOwnerAttributes().size(), equalTo(0));

        assertThat(model.getSessionizers().size(), equalTo(0));
    }
}
