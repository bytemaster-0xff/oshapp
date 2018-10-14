package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

public class ObservableProperty {
    public String Name;
    public String Id;

    public static ObservableProperty create(Node propertyNode, double sosVersion) {
        ObservableProperty property = new ObservableProperty();
        if(sosVersion == 2.0) {
            property.Name = propertyNode.getTextContent();
        }
        else {
            property.Name = NodeUtils.getAttrValue(propertyNode.getAttributes(), "xlink:href");
        }

        if(property.Name != null){
            String[] parts = property.Name.split("/");
            if(parts.length > 0){
                property.Id = parts[parts.length - 1];
            }
        }

        return property;

    }
}
