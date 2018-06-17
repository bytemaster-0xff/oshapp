package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ObservationDescriptor {
    public ObservationDescriptor() {
        Outputs = new ArrayList<>();
    }

    public String Name;
    public String Identifier;
    public List<ObservationDescriptorOutput> Outputs;
    public String Type;
    public String Definition;
    public String Label;
    public String AxisID;
    public String ReferenceFrame;

    public UnitOfMeasureDescriptor UnitOfMeasure;

    public static ObservationDescriptor create(Node node)  {
        ObservationDescriptor descriptor = new ObservationDescriptor();

        Node descriptionNode = NodeUtils.findNode(node.getChildNodes(),"description");
        Node sensorDescription = NodeUtils.findNode(descriptionNode.getChildNodes(),"SensorDescription");
        Node data = NodeUtils.findNode(sensorDescription.getChildNodes(),"data");
        Node physicalComponent = NodeUtils.findNode(data.getChildNodes(),"PhysicalComponent ");
        Node outputs = NodeUtils.findNode(physicalComponent.getChildNodes(),"PhysicalComponent ");
        List<Node> outputNodes = NodeUtils.getMatchingChildren(outputs.getChildNodes(), "output");

        for(Node outputNode : outputNodes) {

        }


        /*descriptor.Name = obj.getString("name");
        descriptor.Type = obj.getString("type");
        descriptor.Definition = obj.getString("definition");
        descriptor.Label = obj.getString("label");
        descriptor.AxisID = obj.getString("axisID");*/
        return descriptor;
    }
}
