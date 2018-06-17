package com.softwarelogistics.oshgeo.poc.tasks;

import android.os.AsyncTask;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.services.SosClient;

public class GetSOSCapabilitiesTask extends AsyncTask<Void, Void, Capabilities> {
    public GetSOSCapabilitiesResponseHandler responseHandler = null;


    @Override
    protected Capabilities doInBackground(Void... voids) {
        SosClient client = new SosClient();
        return client.loadOSHData("http://10.1.1.244:8181/sensorhub/sos?service=SOS&version=2.0&request=GetCapabilities");
    }

    @Override
    protected void onPostExecute(Capabilities capabilities) {
        if(responseHandler != null) {
            responseHandler.gotCapabilities(capabilities);
        }
    }
}
