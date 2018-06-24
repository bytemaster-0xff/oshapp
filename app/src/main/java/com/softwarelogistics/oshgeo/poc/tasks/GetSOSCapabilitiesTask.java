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

public class GetSOSCapabilitiesTask extends AsyncTask<OpenSensorHub, Void, Capabilities> {
    public GetSOSCapabilitiesResponseHandler responseHandler = null;

    @Override
    protected Capabilities doInBackground(OpenSensorHub... hub) {
        SosClient client = new SosClient(hub[0].SecureConnection,hub[0].URI,hub[0].Port);
        Capabilities capabilities = client.loadOSHData();
        if(capabilities != null) {
            for(Offering offering : capabilities.Offerings){
                Log.d("log.osh", String.format("%s - %s", offering.Name, offering.Procedure));
                ObservationDescriptor descriptor = client.loadObservationDescriptor(offering.Procedure);
                capabilities.Descriptors.add(descriptor);

                for(ObservationDescriptorOutput output : descriptor.Outputs) {
                    for(ObservationDescriptorDataField field : output.Fields) {
                        if(field.FieldType != ObservationDescriptorDataField.FieldTypes.Time) {
                            client.getSensorValue(descriptor, offering.Identifier, field.Definition);
                        }
                    }
                }

                for(ObservationDescriptorOutput output : descriptor.DataStreams) {
                    for(ObservationDescriptorDataField field : output.Fields) {
                        if(field.FieldType != ObservationDescriptorDataField.FieldTypes.Time) {
                            client.getSensorValue(descriptor, offering.Identifier, field.Definition);
                        }
                    }
                }
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
