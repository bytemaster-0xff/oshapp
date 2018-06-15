package com.softwarelogistics.oshgeo.poc.models;

import org.w3c.dom.Node;

public class ObservableProperty {
    public String Name;

    public static ObservableProperty create(Node propertyNode) {
        ObservableProperty property = new ObservableProperty();
        property.Name = propertyNode.getTextContent();
        return property;

    }
}
