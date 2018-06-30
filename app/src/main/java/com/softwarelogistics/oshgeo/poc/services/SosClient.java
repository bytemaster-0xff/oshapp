package com.softwarelogistics.oshgeo.poc.services;


import android.icu.util.Output;
import android.util.Log;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptorDataField;
import com.softwarelogistics.oshgeo.poc.models.Offering;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;
import com.softwarelogistics.oshgeo.poc.utils.DateParser;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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

    public SensorValue getSensorValue(ObservationDescriptor descriptors, Offering offering, ObservationDescriptorDataField field){

        //String offeringId = "urn:osh:esp8266:dht:attic-sos";
        //String property = "http://sensorml.com/ont/swe/property/AirTemperature";

        String formatString = "%s://%s:%d/sensorhub/sos?service=SOS&version=2.0&request=GetResult&offering=%s&observedProperty=%s&temporalFilter=phenomenonTime,now";

        String valueUri = String.format(formatString, mHttps ? "https" : "http", mUri, mPort, offering.Identifier, field.Definition);

        Log.d("log.osh","");
        Log.d("log.osh","-------------------------------------------");
        Log.d("log.osh",offering.Name);
        Log.d("log.osh",valueUri);

        try {
            String rawValue = readStringFromURL(valueUri);
            Log.d("log.osh",  rawValue);

            SensorValue value = new SensorValue();
            value.Name = field.Label;
            value.Label = field.Label;
            value.StrValue = rawValue;
            value.Units = field.UnitOfMeasure;
            value.DataType = "string";
            String[] parts = rawValue.split(",");
            if(parts.length == 2){
                //TODO:  Should probably do somthing to figure out actual datatypes, for now everything is a string.
                value.DataType = "string";
                value.StrValue = parts[1];
                value.Timestamp = DateParser.parse(parts[0]);
            }
            else if(parts.length == 4){
                //TODO: Hack, if we have three values assume it's time stamp, lat and lon
                value.DataType = "latlng";
                value.StrValue = String.format("%s,%s", parts[1], parts[2]);
                value.Timestamp = DateParser.parse(parts[0]);
            }
            else {
                value.Timestamp = new Date();
            }

            Log.d("log.osh","-------------------------------------------");

            return value;

        }
        catch(Exception e) {
            Log.d("log.osh",e.getLocalizedMessage());
            Log.d("log.osh","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        return null;
    }
}

