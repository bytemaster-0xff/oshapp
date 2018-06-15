package com.softwarelogistics.oshgeo.poc.models;

import org.w3c.dom.Node;

public class ObservationType {
    public String Name;

    public static ObservationType create(Node node){
        ObservationType type = new ObservationType();
        type.Name = node.getTextContent();
        return type;
    }
}
