package com.wodriver;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by user on 2016-12-08.
 */
@DynamoDBTable(tableName = "health_Info")
public class HealthInfo {
    double hr;
    String time;
    double x;
    double y;
    double z;

    @DynamoDBHashKey(attributeName = "hr")
    public double getHr() {
        return hr;
    }

    public void setHr(double hr) {
        this.hr = hr;
    }

    @DynamoDBRangeKey(attributeName = "time")
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @DynamoDBAttribute(attributeName = "x")
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @DynamoDBAttribute(attributeName = "y")
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @DynamoDBAttribute(attributeName = "z")
    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
