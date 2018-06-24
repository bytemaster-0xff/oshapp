package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ObservationDescriptorDataField {
    public ObservationDescriptorDataField()
    {
        Fields = new ArrayList<>();
    }


    public enum FieldTypes {
        Time,
        Quantity,
        Vector
    }

    public FieldTypes FieldType;

    public String Description;
    public String Label;
    public String UnitOfMeasure;
    public String AxisId;
    public String Name;
    public String Definition;

    public List<ObservationDescriptorDataField> Fields;

    public static ObservationDescriptorDataField create(Node node, FieldTypes fieldType) {
        ObservationDescriptorDataField field = new ObservationDescriptorDataField();
        field.Label = NodeUtils.getNodeText(node.getChildNodes(),"label");
        field.UnitOfMeasure = NodeUtils.getNodeText(node.getChildNodes(),"uom");
        field.Description = NodeUtils.getNodeText(node.getChildNodes(),"description");
        field.Definition = NodeUtils.getAttrValue(node.getAttributes(), "definition");
        field.FieldType = fieldType;

        List<Node> coodNodes = NodeUtils.getMatchingChildren(node.getChildNodes(), "coordinate");
        for(Node coodNode : coodNodes){
            Node quantityNode = NodeUtils.findNode(coodNode.getChildNodes(),"Quantity");
            if(quantityNode != null) {
                ObservationDescriptorDataField childField = ObservationDescriptorDataField.create(quantityNode, FieldTypes.Quantity);
                childField.AxisId = NodeUtils.getAttrValue(quantityNode.getAttributes(), "axisID");
                childField.Name = NodeUtils.getAttrValue(coodNode.getAttributes(), "name");
                field.Fields.add(childField);
            }
        }

        return field;
    }
}
