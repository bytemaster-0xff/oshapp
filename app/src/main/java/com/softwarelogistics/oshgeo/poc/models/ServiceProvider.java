package com.softwarelogistics.oshgeo.poc.models;

import android.app.Service;

import com.slsys.sensorobservationserverdemo.utils.NodeUtils;

import org.w3c.dom.Node;

public class ServiceProvider {
    public String ProviderName;

    public ServiceContact Contact;

    public static ServiceProvider create(Node node)
    {
        ServiceProvider provider = new ServiceProvider();

        provider.ProviderName = NodeUtils.getNodeText(node.getChildNodes(), "ProviderName");
        provider.Contact = ServiceContact.create(NodeUtils.findNode(node.getChildNodes(), "ServiceContact"));

        return provider;
    }
}
