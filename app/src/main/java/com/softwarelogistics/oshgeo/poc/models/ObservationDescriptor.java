package com.softwarelogistics.oshgeo.poc.models;

import com.google.android.gms.maps.model.LatLng;
import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ObservationDescriptor {
    public ObservationDescriptor() {
        Outputs = new ArrayList<>();
        DataStreams = new ArrayList<>();
        Components = new ArrayList<>();
    }

    public String Name;
    public String Id;
    public List<String> Components;
    public List<ObservationDescriptorOutput> Outputs;
    public List<ObservationDescriptorOutput> DataStreams;

    public Double SOSVersion;

    public String ValidTimeStart;
    public String ValidTimeEnd;

    public String Description;

    public LatLng Position;

    public static ObservationDescriptor createV2(Node node)  {
        ObservationDescriptor descriptor = new ObservationDescriptor();
        descriptor.SOSVersion = 2.0;

        Node descriptionNode = NodeUtils.findNode(node.getChildNodes(),"description");
        Node sensorDescription = NodeUtils.findNode(descriptionNode.getChildNodes(),"SensorDescription");
        Node data = NodeUtils.findNode(sensorDescription.getChildNodes(),"data");
        Node physical = NodeUtils.findNode(data.getChildNodes(),"PhysicalSystem");
        if(physical == null) {
            physical = NodeUtils.findNode(data.getChildNodes(), "PhysicalComponent");
        }

        descriptor.Name = NodeUtils.getNodeText(physical.getChildNodes(), "name");
        descriptor.Id = NodeUtils.getNodeText(physical.getChildNodes(), "identifier");
        descriptor.Description = NodeUtils.getNodeText(physical.getChildNodes(), "description");

        Node validTimeNode = NodeUtils.findNode(physical.getChildNodes(), "validTime");
        if(validTimeNode != null) {
            Node timePeriod = NodeUtils.findNode(validTimeNode.getChildNodes(), "TimePeriod");

            if (timePeriod != null) {
                descriptor.ValidTimeStart = NodeUtils.getNodeText(timePeriod.getChildNodes(), "beginPosition");
                descriptor.ValidTimeEnd = NodeUtils.getNodeText(timePeriod.getChildNodes(), "endPosition");
            }
        }

        Node outputsNode = NodeUtils.findNode(physical.getChildNodes(), "outputs");
        Node outputListNodes = NodeUtils.findNode(outputsNode.getChildNodes(), "OutputList");
        List<Node> outputNodes = NodeUtils.getMatchingChildren(outputListNodes.getChildNodes(), "output");
        for(Node outputNode : outputNodes){
            ObservationDescriptorOutput output = ObservationDescriptorOutput.create(outputNode);
            if(output != null) {
                descriptor.Outputs.add(output);
            }

            Node dataStream = NodeUtils.findNode(outputNode.getChildNodes(), "DataStream");
            if(dataStream != null){
                Node elementType = NodeUtils.findNode(dataStream.getChildNodes(),"elementType");
                if(elementType != null) {
                    ObservationDescriptorOutput dataStreamOutput = ObservationDescriptorOutput.create(elementType);
                    if (dataStreamOutput != null) {
                        descriptor.DataStreams.add(dataStreamOutput);
                    }
                }
            }
        }

        Node positionNode = NodeUtils.findNode(physical.getChildNodes(),"position");
        if(positionNode !=  null) {
            Node pointNode = NodeUtils.findNode(positionNode.getChildNodes(), "Point");
            if(pointNode != null){
                String posText = NodeUtils.getNodeText(pointNode.getChildNodes(),"pos");
                if(posText !=  null) {
                    String[] parts = posText.split(" ");
                    if (parts.length == 3) {
                        try {
                            double lat = Double.parseDouble(parts[0]);
                            double lng = Double.parseDouble(parts[1]);
                            descriptor.Position = new LatLng(lat, lng);
                        } catch (Exception ex) {
                            /* nop */
                        }
                    }
                }
            }
        }

        return descriptor;
    }

    public static ObservationDescriptor createV1(Node node) {
        ObservationDescriptor descriptor = new ObservationDescriptor();
        descriptor.SOSVersion = 1.0;
        Node memberNode = NodeUtils.findNode(node.getChildNodes(), "member");
        Node systemNode = NodeUtils.findNode(memberNode.getChildNodes(), "System");
        descriptor.Name = NodeUtils.getNodeText(systemNode.getChildNodes(), "name");
        descriptor.Description = NodeUtils.getNodeText(systemNode.getChildNodes(), "description");
        Node componentsNode = NodeUtils.findNode(systemNode.getChildNodes(), "components");
        if(componentsNode != null) {
            Node componentListNode = NodeUtils.findNode(componentsNode.getChildNodes(), "ComponentList");
            if(componentListNode != null) {
                List<Node> componentsList = NodeUtils.getMatchingChildren(componentListNode.getChildNodes(), "component");
                for (Node component : componentsList) {
                    String href = NodeUtils.getAttrValue(component.getAttributes(), "xlink:href");
                    if (href != null && href.length() > 0) {
                        descriptor.Components.add(href);
                    }
                }
            }
        }

        Node positionNode = NodeUtils.findNode(systemNode.getChildNodes(),"location");
        if(positionNode !=  null) {
            Node pointNode = NodeUtils.findNode(positionNode.getChildNodes(), "Point");
            if(pointNode != null){
                String posText = NodeUtils.getNodeText(pointNode.getChildNodes(),"coordinates");
                if(posText !=  null) {
                    String[] parts = posText.split(" ");
                    if (parts.length == 2) {
                        try {
                            double lat = Double.parseDouble(parts[0]);
                            double lng = Double.parseDouble(parts[1]);
                            descriptor.Position = new LatLng(lat, lng);
                        } catch (Exception ex) {
                            /* nop */
                        }
                    }
                }
            }
        }

        return descriptor;
    }
}
