package com.softwarelogistics.oshgeo.poc.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Sensor {
    public long Id;
    public long HubId;
    public LatLng Location;
    public String Description;
    public String SensorUniqueId;
    public String SensorType;
    public String Name;
    public Date LastContact;
}
