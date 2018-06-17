package com.softwarelogistics.oshgeo.poc.services;


import android.util.Log;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SosClient  {
    private boolean mHttps;
    private String mUri;
    private int mPort;

    public SosClient(boolean https, String uri, int port){
        mHttps = https;
        mUri = uri;
        mPort = port;
    }

    private String readStringFromURL(String requestURL) throws IOException
    {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public ObservationDescriptor loadObservationDescriptor(String procedure){
        String offeringUrl = String.format("%s://%s:%d/sensorhub/sos?service=SOS&version=2.0&request=DescribeSensor&procedure=%s", mHttps ? "https" : "http", mUri, mPort, procedure);

        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(offeringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream xmlInputStream = urlConnection.getInputStream();

            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);
            Node node = doc.getFirstChild();
            ObservationDescriptor capabilities = ObservationDescriptor.create(node);
            xmlInputStream.close();

            return capabilities;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    public Capabilities loadOSHData() {
        String capabilitiesUrl = String.format("%s://%s:%d/sensorhub/sos?service=SOS&version=2.0&request=GetCapabilities", mHttps ? "https" : "http", mUri, mPort);

        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(capabilitiesUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream xmlInputStream = urlConnection.getInputStream();

            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);
            Node node = doc.getFirstChild();
            Capabilities capabilities = Capabilities.create(node);
            xmlInputStream.close();

            return capabilities;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }
}

