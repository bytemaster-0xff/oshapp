package com.softwarelogistics.oshgeo.poc.utils;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public class GeoUtils {

    public static double distance(LatLng origin, LatLng dest) {
        Location start = new Location(LocationManager.GPS_PROVIDER);
        start.setLatitude(origin.latitude);
        start.setLongitude(origin.longitude);
        Location end = new Location(LocationManager.GPS_PROVIDER);
        end.setLatitude(dest.latitude);
        end.setLongitude(dest.longitude);
        return start.distanceTo(end);
    }

    public static double bearing(LatLng origin, LatLng dest) {
        Location start = new Location(LocationManager.GPS_PROVIDER);
        start.setLatitude(origin.latitude);
        start.setLongitude(origin.longitude);
        Location end = new Location(LocationManager.GPS_PROVIDER);
        end.setLatitude(dest.latitude);
        end.setLongitude(dest.longitude);

        return start.bearingTo(end);
    }
}
