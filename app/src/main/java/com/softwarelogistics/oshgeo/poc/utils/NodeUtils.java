package com.softwarelogistics.oshgeo.poc.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NodeUtils {

    public static Node findNode(NodeList nodes, String name) {
        for(int idx = 0; idx < nodes.getLength(); ++idx){
            Node node = nodes.item(idx);
            //TODO: Should really add name space support
            String parts[] = node.getNodeName().split(":");
            if(parts.length == 2){
                if(parts[1].contentEquals(name)){
                    return node;
                }
            }
            else if(parts.length == 1){
                if(parts[0].contentEquals(name)) {
                    return node;
                }
            }
        }

        return null;
    }

    public static String getAttrValue(NamedNodeMap map, String name)
    {
        Node node = map.getNamedItem(name);
        if(node == null){
            return "";
        }
        else {
            return node.getTextContent();
        }
    }


    public static String getAttrValue(NamedNodeMap map, String uri, String name)
    {
        Node node = map.getNamedItemNS(uri,name);
        if(node == null){
            return "";
        }
        else {
            return node.getTextContent();
        }
    }

    public static List<Node> getMatchingChildren(NodeList nodes, String name) {
        List<Node> children = new ArrayList<Node>();

        for(int idx = 0; idx < nodes.getLength(); ++idx){
            Node node = nodes.item(idx);
            //TODO: Should really add name space support
            String parts[] = node.getNodeName().split(":");
            if(parts.length == 2){
                if(parts[1].contentEquals(name)){
                    children.add(node);
                }
            }
            else if(parts.length == 1){
                if(parts[0].contentEquals(name)) {
                    children.add(node);
                }
            }
        }

        return children;
    }

    public static String getNodeText(NodeList nodes, String name) {
        Node node = findNode(nodes, name);

        if(node == null){
            return null;
        }

        return node.getTextContent();
    }
}
