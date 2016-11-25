package com.mnubo.java.sdk.client.models.result;

import java.util.UUID;

import org.joda.time.DateTime;

public class BooleanValue implements ResultValue  {
    private final boolean value;

    public BooleanValue(boolean value){
        this.value = value;
    }

    @Override
    public boolean booleanValue() {
        return value;
    }

    @Override
    public int intValue() { return (value) ? 1 : 0; }

    @Override
    public long longValue() {
        return (value) ? 1 : 0;
    }

    @Override
    public float floatValue() {
        return (value) ? 1.0f : 0.0f;
    }

    @Override
    public double doubleValue() {
        return (value) ? 1.0 : 0.0;
    }

    @Override
    public String strValue() { return String.valueOf(value); }

    @Override
    public DateTime datetimeValue() {
        throw new NumberFormatException("Cannot convert a boolean to DateTime");
    }

    @Override
    public UUID uuidValue() {
        throw new NumberFormatException("Cannot convert a boolean to uuid");
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof BooleanValue){
            return value == ((BooleanValue)obj).booleanValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Boolean.valueOf(value).hashCode();
    }
}
