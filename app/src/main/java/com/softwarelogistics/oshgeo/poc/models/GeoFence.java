package com.softwarelogistics.oshgeo.poc.models;

import android.util.Log;

import com.softwarelogistics.oshgeo.poc.activities.MainActivity;
import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

import mil.nga.geopackage.BoundingBox;

//TODO: This may contain other geometries
public class GeoFence {
    public GeoLocation LowerCorner;
    public GeoLocation UpperCorner;

    public  static GeoFence create(Node node){
        GeoFence fence = new GeoFence();
        fence.UpperCorner = GeoLocation.create(NodeUtils.findNode(node.getChildNodes(), "upperCorner"));
        fence.LowerCorner = GeoLocation.create(NodeUtils.findNode(node.getChildNodes(), "lowerCorner"));
        return fence;
    }

    public BoundingBox toBoundingBox() {
        BoundingBox box = new BoundingBox();
        box.setMinLatitude(LowerCorner.Latitude < UpperCorner.Latitude ? LowerCorner.Latitude : UpperCorner.Latitude);
        box.setMaxLatitude(LowerCorner.Latitude > UpperCorner.Latitude ? LowerCorner.Latitude : UpperCorner.Latitude);

        box.setMinLongitude(LowerCorner.Longitude < UpperCorner.Longitude ? LowerCorner.Longitude : UpperCorner.Longitude);
        box.setMaxLongitude(LowerCorner.Longitude > UpperCorner.Longitude ? LowerCorner.Longitude : UpperCorner.Longitude);
        return box;
    }

    public boolean intersects(BoundingBox otherBoundingBox) {
        Log.d(MainActivity.TAG, "-------------------");
        BoundingBox thisBoundingBox = toBoundingBox();
        boolean intersects = thisBoundingBox.intersects(otherBoundingBox);
        if(intersects) {
            Log.d(MainActivity.TAG, "YES");
        }
        else {
            Log.d(MainActivity.TAG, "NO");
        }

        Log.d(MainActivity.TAG, String.format("%f %f %f %f", thisBoundingBox.getMinLatitude(), thisBoundingBox.getMaxLatitude(), thisBoundingBox.getMinLongitude(), thisBoundingBox.getMaxLongitude() ));
//        Log.d(MainActivity.TAG, String.format("%f %f %f %f", otherBoundingBox.getMinLatitude(), otherBoundingBox.getMaxLatitude(), otherBoundingBox.getMinLongitude(), otherBoundingBox.getMaxLongitude() ));
        Log.d(MainActivity.TAG, "-------------------");

        return intersects;
    }
}
