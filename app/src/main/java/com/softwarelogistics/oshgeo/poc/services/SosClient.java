package com.softwarelogistics.oshgeo.poc.services;


import android.util.Log;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SosClient  {
    private boolean mHttps;
    private String mUri;
    private long mPort;

    public SosClient(boolean https, String uri, long port){
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
        InputStream xmlInputStream = null;

        try {
            URL url = new URL(offeringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            xmlInputStream = urlConnection.getInputStream();

            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);
            Node node = doc.getFirstChild();
            ObservationDescriptor descriptor = ObservationDescriptor.create(node);
            Log.d("log.osh", "Got descriptor - ");
            return descriptor;
        } catch (Exception e) {
            Log.d("log.osh", "Exception loading descriptor " + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if(xmlInputStream != null){
                try {
                    xmlInputStream.close();
                }
                catch (Exception ex) {

                }
            }
        }

        return null;
    }

    public Capabilities loadOSHData() {
        String capabilitiesUrl = String.format("%s://%s:%d/sensorhub/sos?service=SOS&version=2.0&request=GetCapabilities", mHttps ? "https" : "http", mUri, mPort);

        HttpURLConnection urlConnection = null;
        InputStream xmlInputStream = null;
        try {
            Log.d("log.osh","Calling capabilities");
            URL url = new URL(capabilitiesUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            xmlInputStream = urlConnection.getInputStream();
            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);
            Node node = doc.getFirstChild();
            Capabilities capabilities = Capabilities.create(node);
            xmlInputStream.close();

            return capabilities;

        } catch (Exception e) {
            Log.d("log.osh", "Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if(xmlInputStream != null){
                try {
                    xmlInputStream.close();
                }
                catch (Exception ex) {

                }
            }
        }

        return null;
    }

    public List<SensorValue> getSensorValue(ObservationDescriptor descriptors, String offeringId, String property){

        //String offeringId = "urn:osh:esp8266:dht:attic-sos";
        //String property = "http://sensorml.com/ont/swe/property/AirTemperature";

        String valueUri = String.format("%s://%s:%d/sensorhub/sos?service=SOS&version=2.0&request=GetResult&offering=%s&observedProperty=%s&temporalFilter=phenomenonTime,now", mHttps ? "https" : "http", mUri, mPort, offeringId,property);

        Log.d("log.osh","Asking for value");
        Log.d("log.osh",valueUri);
        Log.d("log.osh","-------------------------------------------");
        HttpURLConnection urlConnection = null;
        InputStream rawValueStream = null;
        try {
            String valueInput = readStringFromURL(valueUri);
            Log.d("log.osh", valueInput);
        }
        catch(Exception e) {

        }

        return null;
    }
}

