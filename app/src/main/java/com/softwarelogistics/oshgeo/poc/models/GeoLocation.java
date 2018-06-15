package com.softwarelogistics.oshgeo.poc.models;

import com.slsys.sensorobservationserverdemo.utils.NodeUtils;

import org.w3c.dom.Node;

public class GeoLocation {
    public double Latitude;
    public double Longitude;

    public static GeoLocation create(Node node) {
        GeoLocation geo = new GeoLocation();

        String locationText = node.getTextContent();
        if(locationText.length() != 0)
        {
            String[] parts = locationText.split(" ");
            if(parts.length == 2) {
                geo.Latitude = Double.parseDouble(parts[0]);
                geo.Longitude = Double.parseDouble(parts[1]);
            }
        }

        return geo;
    }
}
