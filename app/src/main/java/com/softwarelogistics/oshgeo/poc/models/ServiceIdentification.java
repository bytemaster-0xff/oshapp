package com.softwarelogistics.oshgeo.poc.models;

import com.slsys.sensorobservationserverdemo.utils.NodeUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ServiceIdentification {
    public ServiceIdentification()
    {
        Profiles = new ArrayList<>();
    }

    public String Title;
    public String Abstract;
    public String ServiceType;
    public String ServiceTypeVersion;
    public List<String> Profiles;
    public String Fees;
    public String AccessConstraints;

    public static ServiceIdentification create(Node node)
    {
        ServiceIdentification serviceId = new ServiceIdentification();

        serviceId.Title = NodeUtils.getNodeText(node.getChildNodes(), "Title");
        serviceId.Abstract = NodeUtils.getNodeText(node.getChildNodes(), "Abstract");
        serviceId.ServiceType = NodeUtils.getNodeText(node.getChildNodes(), "ServiceType");
        serviceId.ServiceTypeVersion = NodeUtils.getNodeText(node.getChildNodes(), "ServiceTypeVersion");
        serviceId.Fees = NodeUtils.getNodeText(node.getChildNodes(), "Fees");
        serviceId.AccessConstraints = NodeUtils.getNodeText(node.getChildNodes(), "AccessConstraints");

        List<Node> profileNodes = NodeUtils.getMatchingChildren(node.getChildNodes(),"Profile");
        for(Node profileNode : profileNodes)
        {
            serviceId.Profiles.add(profileNode.getTextContent());
        }


        return serviceId;
    }
}
