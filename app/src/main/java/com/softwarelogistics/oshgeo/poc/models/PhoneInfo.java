package com.softwarelogistics.oshgeo.poc.models;

import com.slsys.sensorobservationserverdemo.utils.NodeUtils;

import org.w3c.dom.Node;

public class PhoneInfo {
    public String Voice;
    public String Facsimile;

    public static PhoneInfo create(Node node) {
        PhoneInfo phoneInfo = new PhoneInfo();
        phoneInfo.Voice = NodeUtils.getNodeText(node.getChildNodes(), "Voice");
        phoneInfo.Facsimile = NodeUtils.getNodeText(node.getChildNodes(), "Facsimile");

        return phoneInfo;

    }
}
