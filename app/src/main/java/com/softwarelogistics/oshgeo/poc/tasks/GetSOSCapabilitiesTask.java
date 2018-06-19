package com.softwarelogistics.oshgeo.poc.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;
import com.softwarelogistics.oshgeo.poc.models.Offering;
import com.softwarelogistics.oshgeo.poc.services.SosClient;

public class GetSOSCapabilitiesTask extends AsyncTask<Object, Void, Capabilities> {
    public GetSOSCapabilitiesResponseHandler responseHandler = null;


    @Override
    protected Capabilities doInBackground(Object... args) {
        boolean https = (boolean)args[0];
        String uri = (String)args[1];
        int port = (int)args[2];

        SosClient client = new SosClient(https, uri, port);
        Capabilities capabilities = client.loadOSHData();
        if(capabilities != null) {

        for(Offering offering : capabilities.Offerings){
            Log.d("log.osh", String.format("%s - %s", offering.Name, offering.Procedure));

            ObservationDescriptor descriptor = client.loadObservationDescriptor(offering.Procedure);

        }
        }

        return capabilities;
    }

    @Override
    protected void onPostExecute(Capabilities capabilities) {
        if(responseHandler != null) {
            responseHandler.gotCapabilities(capabilities);
        }
    }
}
