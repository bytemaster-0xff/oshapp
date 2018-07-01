package com.softwarelogistics.oshgeo.poc.models;

import com.google.android.gms.maps.model.LatLng;

public class MapFeature {
    public MapFeature()
    {

    }

    public long Id;
    public String Name;
    public String Description;
    public LatLng Location;

    //This value isn't stored, but we may need it to find the feature later.
    public String TableName;
}
