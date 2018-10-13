package com.softwarelogistics.oshgeo.poc.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptorDataField;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptorDataRecord;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptorOutput;
import com.softwarelogistics.oshgeo.poc.models.Offering;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.services.SosClient;

import mil.nga.geopackage.BoundingBox;

public class GetSOSCapabilitiesTask extends AsyncTask<GetCapabilitiesRequest, String, Capabilities> {
    public GetSOSCapabilitiesResponseHandler responseHandler = null;

    public ProgressHandler progressHandler = null;

    @Override
    protected Capabilities doInBackground(GetCapabilitiesRequest... request) {
        OpenSensorHub hub = request[0].Hub;
        BoundingBox boundingBox = request[0].BoundingBox;

        SosClient client = new SosClient(boundingBox, hub.SecureConnection, hub.URI, hub.Path, hub.Port);
        publishProgress("Downloading Capabilities");
        Capabilities capabilities = client.loadOSHData();
        if(capabilities != null) {
            for(Offering offering : capabilities.Offerings){
                publishProgress(String.format("Downloading %s", offering.Name));
                ObservationDescriptor descriptor = client.loadObservationDescriptor(capabilities.SOSVersion, offering.Procedure);
                if(descriptor != null) {
                    capabilities.Descriptors.add(descriptor);
                }
            }
        }

        return capabilities;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        progressHandler.progressUpdated(String.format(values[0]));
    }

    @Override
    protected void onPostExecute(Capabilities capabilities) {
        if(responseHandler != null) {
            responseHandler.gotCapabilities(capabilities);
        }
    }
}
