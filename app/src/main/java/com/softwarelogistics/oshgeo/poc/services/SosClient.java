package com.softwarelogistics.oshgeo.poc.services;


import android.icu.util.Output;
import android.util.Log;

import com.softwarelogistics.oshgeo.poc.activities.MainActivity;
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

import mil.nga.geopackage.BoundingBox;

public class SosClient  {
    private boolean mHttps;
    private String mUri;
    private String mPath;
    private long mPort;
    private BoundingBox mBoundingBox;

    public SosClient(BoundingBox boundingBox, boolean https, String uri, String path, long port){
        mHttps = https;
        mPath = path;
        mUri = uri;
        mPort = port;
        mBoundingBox = boundingBox;
    }

    private String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public ObservationDescriptor loadObservationDescriptor(double hubVersion, String procedure){
        String offeringUrl = String.format("%s://%s:%d%s?service=SOS&version=%.1f.0&request=DescribeSensor&procedure=%s", mHttps ? "https" : "http", mUri, mPort, mPath, hubVersion, procedure);

        if(hubVersion == 1.0) {
            offeringUrl += "&outputFormat=text%2Fxml%3Bsubtype%3D%22sensorML%2F1.0.1%22\n";
        }

        Log.d(MainActivity.TAG, offeringUrl);

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

            ObservationDescriptor descriptor = null;

            if(hubVersion == 2.0) {
                descriptor = ObservationDescriptor.createV2(node);
            }
            else if(hubVersion == 1.0) {
                descriptor = ObservationDescriptor.createV1(node);
            }

            if(descriptor != null) {
                Log.d(MainActivity.TAG, "Got descriptor - " + descriptor.Name);
            }
            else {
                Log.d(MainActivity.TAG, "Could not load descriptor for procedure: " + procedure);
            }

            return descriptor;

        } catch (Exception e) {
            Log.d(MainActivity.TAG, "Exception loading descriptor " + e.getLocalizedMessage());
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
        String capabilitiesUrl = String.format("%s://%s:%d%s?service=SOS&version=2.0&request=GetCapabilities", mHttps ? "https" : "http", mUri, mPort, mPath);

        HttpURLConnection urlConnection = null;
        InputStream xmlInputStream = null;
        try {
            Log.d(MainActivity.TAG,"Calling capabilities");
            Log.d(MainActivity.TAG,capabilitiesUrl);

            URL url = new URL(capabilitiesUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            xmlInputStream = urlConnection.getInputStream();
            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);
            Node node = doc.getFirstChild();
            Capabilities capabilities = Capabilities.create(node, mBoundingBox);
            xmlInputStream.close();
            return capabilities;

        } catch (Exception e) {
            Log.d(MainActivity.TAG, "Exception: " + e.getLocalizedMessage());
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

    public SensorValue getSensorValue(ObservationDescriptor descriptors, double hubVersion, Offering offering, ObservationDescriptorDataField field){
        String formatString = "%s://%s:%d%s?service=SOS&version=2.0&request=GetResult&offering=%s&observedProperty=%s&temporalFilter=phenomenonTime,now";
        String valueUri = String.format(formatString, mHttps ? "https" : "http", mUri, mPort, mPath, offering.Identifier, field.Definition);

        Log.d(MainActivity.TAG,"");
        Log.d(MainActivity.TAG,"-------------------------------------------");
        Log.d(MainActivity.TAG,offering.Name);
        Log.d(MainActivity.TAG,valueUri);

        try {
            String rawValue = readStringFromURL(valueUri);
            if(rawValue != null) {
                rawValue = rawValue.trim();
                Log.d(MainActivity.TAG, rawValue);

                SensorValue value = new SensorValue();
                value.Name = field.Label;
                value.Label = field.Label;
                value.StrValue = rawValue;
                value.Units = field.UnitOfMeasure;
                value.DataType = "string";
                String[] parts = rawValue.split(",");
                if (parts.length == 2) {
                    //TODO:  Should probably do somthing to figure out actual datatypes, for now everything is a string.
                    value.DataType = "string";
                    value.StrValue = parts[1];
                    value.Timestamp = DateParser.parse(parts[0]);
                } else if (parts.length == 4) {
                    //TODO: Hack, if we have three values assume it's time stamp, lat and lon
                    value.DataType = "latlng";
                    value.StrValue = String.format("%s,%s", parts[1], parts[2]);
                    value.Timestamp = DateParser.parse(parts[0]);
                } else {
                    value.Timestamp = new Date();
                }

                Log.d(MainActivity.TAG, "-------------------------------------------");

                return value;
            }
        }
        catch(Exception e) {
            Log.d(MainActivity.TAG,e.getLocalizedMessage());
            Log.d(MainActivity.TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        return null;
    }
}

