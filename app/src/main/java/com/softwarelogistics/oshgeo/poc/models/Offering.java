package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class Offering {

    public Offering()
    {
        Properties = new ArrayList<>();
        ObservationTypes = new ArrayList<>();
    }

    public String Description;
    public String Name;
    public String StartTime;
    public String EndTime;
    public String Procedure;
    public String Identifier;
    public GeoFence GeoFence;

    public List<ObservableProperty> Properties;
    public List<ObservationType> ObservationTypes;

    public static Offering create(Node node, double sosVersion) {
        Offering offering = new Offering();

        offering.Description = NodeUtils.getNodeText(node.getChildNodes(), "description");
        if(sosVersion == 2.0) {
            offering.Identifier = NodeUtils.getNodeText(node.getChildNodes(), "identifier");
        }
        else {
            offering.Identifier = NodeUtils.getAttrValue(node.getAttributes(), "gml:id");
        }

        offering.Name = NodeUtils.getNodeText(node.getChildNodes(), "name");

        if(sosVersion == 2.0) {
            offering.Procedure = NodeUtils.getNodeText(node.getChildNodes(), "procedure");
        }
        else {
            Node procedureNode = NodeUtils.findNode(node.getChildNodes(), "procedure");
            offering.Procedure = NodeUtils.getAttrValue(procedureNode.getAttributes(),"xlink:href");
        }

        List<Node> properties;
        if(sosVersion == 2.0) {
            properties = NodeUtils.getMatchingChildren(node.getChildNodes(), "observableProperty");
        }
        else {
            properties = NodeUtils.getMatchingChildren(node.getChildNodes(), "observedProperty");
        }

        for(Node propertyNode : properties)
        {
            offering.Properties.add(ObservableProperty.create(propertyNode, sosVersion));
        }

        List<Node> observationTypes = NodeUtils.getMatchingChildren(node.getChildNodes(), "observationType");
        for(Node observationType : observationTypes)
        {
            offering.ObservationTypes.add(ObservationType.create(observationType));
        }

        offering.StartTime = "????";
        offering.EndTime = "????";

        Node timeNode = NodeUtils.findNode(node.getChildNodes(), sosVersion == 2.0 ? "phenomenonTime" : "time");

        if(timeNode != null) {
            Node timePeriod = NodeUtils.findNode(timeNode.getChildNodes(), "TimePeriod");
            if(timePeriod != null) {
                Node beginPosition = NodeUtils.findNode(timePeriod.getChildNodes(), "beginPosition");
                Node endPosition = NodeUtils.findNode(timePeriod.getChildNodes(), "endPosition");
                if(beginPosition != null) {
                    offering.StartTime = beginPosition.getTextContent();
                    // If we don't have this set as a text field within the node, check the attribute [indeterminatePosition] this will likely be "now"
                    if (offering.StartTime.length() == 0) {
                        offering.StartTime = NodeUtils.getAttrValue(beginPosition.getAttributes(), "indeterminatePosition");
                    }
                }

                if(endPosition != null) {
                    offering.EndTime = endPosition.getTextContent();
                    if (offering.EndTime.length() == 0) {
                        // If we don't have this set as a text field within the node, check the attribute [indeterminatePosition] this will likely be "now"
                        offering.EndTime = NodeUtils.getAttrValue(endPosition.getAttributes(), "indeterminatePosition");
                    }
                }
            }
        }

        Node area = NodeUtils.findNode(node.getChildNodes(), sosVersion == 1.0 ? "boundedBy" : "observedArea");
        if (area != null) {
            Node envelope = NodeUtils.findNode(area.getChildNodes(), "Envelope");
            if (envelope != null) {
                offering.GeoFence = com.softwarelogistics.oshgeo.poc.models.GeoFence.create(envelope);
            }
        }

        return offering;
    }
}
