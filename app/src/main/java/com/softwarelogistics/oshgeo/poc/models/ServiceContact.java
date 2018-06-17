package com.softwarelogistics.oshgeo.poc.models;


import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

public class ServiceContact {
    public String IndividualName;
    public String PositionName;
    public ContactInfo Contact;

    public static ServiceContact create(Node node){
        ServiceContact contact = new ServiceContact();
        contact.IndividualName = NodeUtils.getNodeText(node.getChildNodes(), "IndividualName");
        contact.PositionName = NodeUtils.getNodeText(node.getChildNodes(), "PositionName");
        contact.Contact = ContactInfo.create(NodeUtils.findNode(node.getChildNodes(), "ContactInfo"));
        return contact;
    }
}
