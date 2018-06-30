package com.softwarelogistics.oshgeo.poc.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class SensorValue {
    public long Id;
    public long HubId;
    public long SensorId;
    public long ReadingId;

    public String Label;
    public String DataType;
    public String Name;
    public String StrValue;
    public java.sql.Date DateValue;
    public Double NumbValue;
    public byte[] MediaValue;
    public String Units;
    public Date Timestamp;
    public LatLng Location;
}
