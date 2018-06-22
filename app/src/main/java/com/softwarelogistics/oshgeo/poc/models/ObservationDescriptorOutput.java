package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ObservationDescriptorOutput {
    public String Name;

    public ObservationDescriptorOutput()
    {
        Fields = new ArrayList<>();
    }

    public List<ObservationDescriptorDataField> Fields;

    public static ObservationDescriptorOutput create(Node node) {
        ObservationDescriptorOutput output = new ObservationDescriptorOutput();
        output.Name = NodeUtils.getAttrValue(node.getAttributes(), "name");

        Node dataRecord = NodeUtils.findNode(node.getChildNodes(),"DataRecord");
        if(dataRecord != null){
            List<Node> fields = NodeUtils.getMatchingChildren(dataRecord.getChildNodes(), "field");
            for (Node fieldNode : fields) {
                Node timeNode = NodeUtils.findNode(fieldNode.getChildNodes(), "Time");
                if (timeNode != null) {
                    output.Fields.add(ObservationDescriptorDataField.create(timeNode, ObservationDescriptorDataField.FieldTypes.Time));
                }

                Node quantityNode = NodeUtils.findNode(fieldNode.getChildNodes(), "Quantity");
                if (quantityNode != null) {
                    output.Fields.add(ObservationDescriptorDataField.create(quantityNode, ObservationDescriptorDataField.FieldTypes.Quantity));
                }

                Node vectorNode = NodeUtils.findNode(fieldNode.getChildNodes(), "Vector");
                if (vectorNode != null) {
                    output.Fields.add(ObservationDescriptorDataField.create(vectorNode, ObservationDescriptorDataField.FieldTypes.Vector));
                }
            }
        }

        return output;
    }
}
