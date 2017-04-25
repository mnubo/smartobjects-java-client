package com.mnubo.java.sdk.client.mapper;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.mnubo.java.sdk.client.models.datamodel.Model;
import org.joda.time.Interval;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.deser.IntervalDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.IntervalSerializer;
import com.mnubo.java.sdk.client.models.Event;
import com.mnubo.java.sdk.client.models.Owner;
import com.mnubo.java.sdk.client.models.SmartObject;

public abstract class ObjectMapperConfig {
    public static final ObjectMapper genericObjectMapper = buildObjectMapper(buildMnuboModule());
    public static final ObjectMapper uuidExistsObjectMapper = buildObjectMapper(buildUUIDExistsResultModule());
    public static final ObjectMapper stringExistsObjectMapper = buildObjectMapper(buildStringExistsResultModule());

    private static ObjectMapper buildObjectMapper(SimpleModule module) {
        Jackson2ObjectMapperFactoryBean factory = new Jackson2ObjectMapperFactoryBean();
        factory.setFeaturesToDisable(WRITE_DATES_AS_TIMESTAMPS);
        factory.setFeaturesToEnable(DEFAULT_VIEW_INCLUSION);
        factory.setFindModulesViaServiceLoader(false);
        factory.setSerializationInclusion(NON_NULL);
        factory.setIndentOutput(false);
        factory.afterPropertiesSet();
        ObjectMapper resVal = factory.getObject();
        resVal.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        resVal.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        resVal.registerModule(module);
        return resVal;
    }

    private static SimpleModule buildMnuboModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new IntervalSerializer());
        module.addDeserializer(Interval.class, new IntervalDeserializer());
        module.addSerializer(new SmartObjectSerializer());
        module.addDeserializer(SmartObject.class, new SmartObjectDeserializer());
        module.addSerializer(new OwnerSerializer());
        module.addDeserializer(Owner.class, new OwnerDeserializer());
        module.addSerializer(new EventSerializer());
        module.addDeserializer(Event.class, new EventDeserializer());
        module.addSerializer(new ClaimOrUnclaimSerializer());
        module.addDeserializer(Model.class, new ModelDeserializer());
        return module;
    }

    private static SimpleModule buildUUIDExistsResultModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UUIDExistsResultDeserializer.targetClass, new UUIDExistsResultDeserializer());
        return module;
    }

    private static SimpleModule buildStringExistsResultModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(StringExistsResultDeserializer.targetClass, new StringExistsResultDeserializer());
        return module;
    }
}
