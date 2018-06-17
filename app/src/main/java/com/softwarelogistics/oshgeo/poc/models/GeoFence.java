package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

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
}
