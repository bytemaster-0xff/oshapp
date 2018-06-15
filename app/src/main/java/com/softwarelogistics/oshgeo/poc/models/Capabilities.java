package com.softwarelogistics.oshgeo.poc.models;

import com.slsys.sensorobservationserverdemo.utils.NodeUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class Capabilities {

    public Capabilities()
    {
        Offerings = new ArrayList<>();
        Operations = new ArrayList<>();
    }

    public static Capabilities create(Node node){
        Capabilities capabilities = new Capabilities();
        capabilities.ServiceId = ServiceIdentification.create(NodeUtils.findNode(node.getChildNodes(),"ServiceIdentification"));
        capabilities.Provider = ServiceProvider.create(NodeUtils.findNode(node.getChildNodes(),"ServiceProvider"));

        Node metaDataNode = NodeUtils.findNode(node.getChildNodes(), "OperationsMetadata");
        List<Node> operationNodes = NodeUtils.getMatchingChildren(metaDataNode.getChildNodes(), "Operation");
        for(Node operationNode : operationNodes)
        {
            capabilities.Operations.add(Operation.create(operationNode));
        }

        Node contentsNode = NodeUtils.findNode(node.getChildNodes(),"contents");
        //No, this isn't a type-o - see XML Schema for /contents/Contents
        Node ContentsNode = NodeUtils.findNode(contentsNode.getChildNodes(),"Contents");

        List<Node> offeringNodes = NodeUtils.getMatchingChildren(ContentsNode.getChildNodes(),"offering");
        for(Node offeringNode : offeringNodes)
        {
            Node observationOffering = NodeUtils.findNode(offeringNode.getChildNodes(),"ObservationOffering");
            capabilities.Offerings.add(Offering.create(observationOffering));
        }

        return capabilities;
    }

    public ServiceIdentification ServiceId;
    public ServiceProvider Provider;
    public List<Operation> Operations;
    public List<Offering> Offerings;
}
