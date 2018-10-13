package com.softwarelogistics.oshgeo.poc.models;

import android.util.Log;

import com.softwarelogistics.oshgeo.poc.activities.MainActivity;
import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.BoundingBox;

public class Capabilities {

    public Capabilities()
    {
        Offerings = new ArrayList<>();
        Operations = new ArrayList<>();
        Descriptors = new ArrayList<>();
    }

    public static Capabilities create(Node node, BoundingBox boundingBox){
        Capabilities capabilities = new Capabilities();

        String sosVersion =  NodeUtils.getAttrValue(node.getAttributes(), "version");
        Log.d(MainActivity.TAG, "SOS Version: " + sosVersion);

        capabilities.ServiceId = ServiceIdentification.create(NodeUtils.findNode(node.getChildNodes(),"ServiceIdentification"));
        capabilities.Provider = ServiceProvider.create(NodeUtils.findNode(node.getChildNodes(),"ServiceProvider"));

        Node metaDataNode = NodeUtils.findNode(node.getChildNodes(), "OperationsMetadata");
        List<Node> operationNodes = NodeUtils.getMatchingChildren(metaDataNode.getChildNodes(), "Operation");
        for(Node operationNode : operationNodes)
        {
            capabilities.Operations.add(Operation.create(operationNode));
        }

        List<Node> offeringNodes = null;

        if(sosVersion.contentEquals("1.0.0")){
            capabilities.SOSVersion = 1.0;
            //V1.0 has the contents structure: /Contents/ObservationOfferingList/ObservationOffering[],
            Node contentsNode = NodeUtils.findNode(node.getChildNodes(),"Contents");
            Node offeringList = NodeUtils.findNode(contentsNode.getChildNodes(),"ObservationOfferingList");
            offeringNodes = NodeUtils.getMatchingChildren(offeringList.getChildNodes(),"ObservationOffering");
        }
        else if(sosVersion.contentEquals("2.0.0")) {
            capabilities.SOSVersion = 2.0;
            //V2.0 has the contents structure: /contents/Contents/offering[],
            Node contentsNode = NodeUtils.findNode(node.getChildNodes(), "contents");
            //No, this isn't a type-o - see XML Schema for /contents/Contents
            Node ContentsNode = NodeUtils.findNode(contentsNode.getChildNodes(),"Contents");
            offeringNodes = NodeUtils.getMatchingChildren(ContentsNode.getChildNodes(),"offering");
        }

        if(offeringNodes != null) {
            for (Node offeringNode : offeringNodes) {
                Offering offering = Offering.create(offeringNode, capabilities.SOSVersion);
                if (offering.GeoFence != null && offering.GeoFence.intersects(boundingBox)) {
                    capabilities.Offerings.add(offering);
                }
            }
        }


        return capabilities;
    }

    public double SOSVersion;
    public ServiceIdentification ServiceId;
    public ServiceProvider Provider;
    public List<Operation> Operations;
    public List<Offering> Offerings;
    public List<ObservationDescriptor> Descriptors;
}
