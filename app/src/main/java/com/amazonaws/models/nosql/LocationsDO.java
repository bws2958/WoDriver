package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "wodriver-mobilehub-1034201032-Locations")

public class LocationsDO {
    private String _userId;
    private Double _category;
    private String _itemId;
    private Double _latitude;
    private Double _longitude;
    private String _name;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "category")
    @DynamoDBAttribute(attributeName = "category")
    public Double getCategory() {
        return _category;
    }

    public void setCategory(final Double _category) {
        this._category = _category;
    }
    @DynamoDBAttribute(attributeName = "itemId")
    public String getItemId() {
        return _itemId;
    }

    public void setItemId(final String _itemId) {
        this._itemId = _itemId;
    }
    @DynamoDBAttribute(attributeName = "latitude")
    public Double getLatitude() {
        return _latitude;
    }

    public void setLatitude(final Double _latitude) {
        this._latitude = _latitude;
    }
    @DynamoDBAttribute(attributeName = "longitude")
    public Double getLongitude() {
        return _longitude;
    }

    public void setLongitude(final Double _longitude) {
        this._longitude = _longitude;
    }
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }

}
