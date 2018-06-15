package com.softwarelogistics.oshgeo.poc.models;


import com.slsys.sensorobservationserverdemo.utils.NodeUtils;

import org.w3c.dom.Node;

public class ContactInfo {
    public PhoneInfo Phone;
    public AddressInfo Address;

    public static ContactInfo create(Node node) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.Phone = PhoneInfo.create(NodeUtils.findNode(node.getChildNodes(), "Phone"));
        contactInfo.Address = AddressInfo.create(NodeUtils.findNode(node.getChildNodes(), "Address"));
        return contactInfo;
    }
}
