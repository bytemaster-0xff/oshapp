package com.softwarelogistics.oshgeo.poc.services;


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
    private String readStringFromURL(String requestURL) throws IOException
    {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public ObservationDescriptor loadObservationDescriptor(String uri){
        HttpURLConnection urlConnection = null;

        try {
            String json = readStringFromURL(uri);
            System.out.print(json);
            JSONTokener tokener = new JSONTokener(json);

            JSONObject obj = new JSONObject(tokener);
            return ObservationDescriptor.create(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    public Capabilities loadOSHData(String uri) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(uri);
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

