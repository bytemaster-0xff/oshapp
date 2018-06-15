package com.softwarelogistics.oshgeo.poc.models;

import com.slsys.sensorobservationserverdemo.utils.NodeUtils;

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
    public String Procedure;
    public String Identifier;
    public GeoFence GeoGence;

    public List<ObservableProperty> Properties;
    public List<ObservationType> ObservationTypes;

    public static Offering create(Node node)
    {
        Offering offering = new Offering();

        offering.Description = NodeUtils.getNodeText(node.getChildNodes(), "description");
        offering.Identifier = NodeUtils.getNodeText(node.getChildNodes(), "identifier");
        offering.Name = NodeUtils.getNodeText(node.getChildNodes(), "name");
        offering.Procedure = NodeUtils.getNodeText(node.getChildNodes(),"procedure");
        List<Node> properties = NodeUtils.getMatchingChildren(node.getChildNodes(), "observableProperty");
        for(Node propertyNode : properties)
        {
            offering.Properties.add(ObservableProperty.create(propertyNode));
        }

        List<Node> observationTypes = NodeUtils.getMatchingChildren(node.getChildNodes(), "observationType");
        for(Node observationType : observationTypes)
        {
            offering.ObservationTypes.add(ObservationType.create(observationType));
        }

        Node phenomenonNode = NodeUtils.findNode(node.getChildNodes(), "phenomenonTime");
        Node timePeriodNode = NodeUtils.findNode(phenomenonNode.getChildNodes(), "TimePeriod");

        Node beginPosition = NodeUtils.findNode(timePeriodNode.getChildNodes(), "beginPosition");
        Node endPosition = NodeUtils.findNode(timePeriodNode.getChildNodes(), "endPosition");

        offering.StartTime = beginPosition.getTextContent();
        offering.EndTime = endPosition.getTextContent();

        if(offering.StartTime.length() == 0)
        {
            offering.StartTime = NodeUtils.getAttrValue(beginPosition.getAttributes(), "indeterminatePosition");
        }

        if(offering.EndTime.length() == 0)
        {
            offering.EndTime = NodeUtils.getAttrValue(endPosition.getAttributes(), "indeterminatePosition");
        }

        Node area = NodeUtils.findNode(node.getChildNodes(), "observedArea");
        if(area != null)
        {
            Node envelope = NodeUtils.findNode(area.getChildNodes(), "Envelope");
            if(envelope != null) {
                offering.GeoGence = GeoFence.create(envelope);
            }
        }


        return offering;
    }

    public String StartTime;
    public String EndTime;
}
