package com.mnubo.java.sdk.client.utils;

import static java.lang.String.format;

import java.util.List;

import com.google.common.collect.Lists;
import com.mnubo.java.sdk.client.models.result.BooleanValue;
import com.mnubo.java.sdk.client.models.result.DoubleValue;
import com.mnubo.java.sdk.client.models.result.FloatValue;
import com.mnubo.java.sdk.client.models.result.IntegerValue;
import com.mnubo.java.sdk.client.models.result.LongValue;
import com.mnubo.java.sdk.client.models.result.ResultValue;
import com.mnubo.java.sdk.client.models.result.StringValue;

/**
 * Created by dmasse on 2016-04-13.
 */
public class Convert {
    public static  String highLevelToPrimitiveType(String highLevelType) {
        String primitiveType = "unknown";

        switch( highLevelType.toUpperCase() ){
            case "INT":
            case "TIME":
                primitiveType = "INT";
                break;
            case "COUNTRY_ISO":
            case "DATETIME":
            case "EMAIL":
            case "STATE":
            case "TEXT":
            case "TIME_ZONE":
                primitiveType = "STRING";
                break;
            case "LONG":
            case "DURATION":
                primitiveType = "LONG";
                break;
            case "ACCELERATION":
            case "AREA":
            case "DOUBLE":
            case "LENGTH":
            case "SPEED":
            case "TEMPERATURE":
            case "VOLUME":
                primitiveType = "DOUBLE";
                break;
            case "BOOLEAN":
                primitiveType = "BOOLEAN";
                break;
            case "MASS":
            case "FLOAT":
                primitiveType = "FLOAT";
                break;
            case "PREDICTIVE_MODEL":
                primitiveType = "BLOB";
                break;
        }
        return primitiveType;
    }

    public static ResultValue toResultValue(Object value){
        ResultValue resultValue;

        if( value instanceof String){
            resultValue = new StringValue((String)value);
        } else if( value instanceof Integer) {
            resultValue = new IntegerValue((Integer)value);
        } else if( value instanceof Long) {
            resultValue = new LongValue((Long)value);
        } else if( value instanceof Float) {
            resultValue = new FloatValue((Float)value);
        } else if( value instanceof Double) {
            resultValue = new DoubleValue((Double)value);
        } else if( value instanceof Boolean) {
            resultValue = new BooleanValue((Boolean) value);
        } else if (value == null){
            resultValue = null;
        } else {
            throw new IllegalArgumentException(format("Unsupported type for value '%s'", value));
        }
        return resultValue;
    }

    public static List<ResultValue> toResultValueList(List<Object> values){
        List<ResultValue> resultValues = Lists.newArrayList();

        for(Object value : values){
            resultValues.add(toResultValue(value));
        }

        return resultValues;
    }


}
