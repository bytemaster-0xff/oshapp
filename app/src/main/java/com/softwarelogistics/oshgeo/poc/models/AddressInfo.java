package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

public class AddressInfo {
    public String DeliveryPoint1;
    public String City;
    public String AdministrativeArea;
    public String PostalCode;
    public String Country;
    public String ElectronicMailAddress;


    public static AddressInfo create(Node node) {
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.AdministrativeArea = NodeUtils.getNodeText(node.getChildNodes(), "AdministrativeArea");
        addressInfo.City = NodeUtils.getNodeText(node.getChildNodes(), "City");
        addressInfo.DeliveryPoint1 = NodeUtils.getNodeText(node.getChildNodes(), "DeliveryPoint");
        addressInfo.PostalCode = NodeUtils.getNodeText(node.getChildNodes(), "PostalCode");
        addressInfo.Country = NodeUtils.getNodeText(node.getChildNodes(), "Country");
        addressInfo.ElectronicMailAddress = NodeUtils.getNodeText(node.getChildNodes(), "ElectronicMailAddress");
        return addressInfo;
    }
}
