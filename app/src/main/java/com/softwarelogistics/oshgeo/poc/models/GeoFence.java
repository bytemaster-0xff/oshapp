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
        BoundingBox thisBoundingBox = toBoundingBox();
        if(thisBoundingBox.getMinLongitude() > otherBoundingBox.getMinLongitude() &&
                thisBoundingBox.getMinLongitude() < otherBoundingBox.getMaxLongitude() &&
                thisBoundingBox.getMinLatitude() > otherBoundingBox.getMinLatitude() &&
                thisBoundingBox.getMinLatitude() < otherBoundingBox.getMaxLatitude()) {
            return true;
        }
        else {
            return false;
        }
    }
}
