package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Operation {
    public String Name;

    public String Get;
    public String Post;
    public String Put;
    public String Delete;

    public static Operation create(Node node) {
        Operation operation = new Operation();

        final String HREF_URI = "http://www.w3.org/1999/xlink";

        operation.Name = NodeUtils.getAttrValue(node.getAttributes(), "name");

        Node dcpNode = NodeUtils.findNode(node.getChildNodes(), "DCP");
        if(dcpNode != null)
        {
            Node httpNode = NodeUtils.findNode(dcpNode.getChildNodes(), "HTTP");
            if(httpNode != null)
            {
                Node getNode = NodeUtils.findNode(httpNode.getChildNodes(), "Get");
                if(getNode != null) {
                    operation.Get = NodeUtils.getAttrValue(getNode.getAttributes(),"xlink:href");
                }
                Node postNode = NodeUtils.findNode(httpNode.getChildNodes(), "Post");
                if(postNode != null) {
                    operation.Post = NodeUtils.getAttrValue(postNode.getAttributes(), "xlink:href");
                }
                Node putNode = NodeUtils.findNode(httpNode.getChildNodes(), "Put");
                if(putNode != null) {
                    operation.Put = NodeUtils.getAttrValue(putNode.getAttributes(),"xlink:href");
                }
                Node deleteNode = NodeUtils.findNode(httpNode.getChildNodes(), "Delete");
                if(deleteNode != null) {
                    operation.Delete = NodeUtils.getAttrValue(deleteNode.getAttributes(),"xlink:href");
                }
            }
        }

        return operation;
    }
}
