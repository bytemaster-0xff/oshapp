package com.softwarelogistics.oshgeo.poc.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptorDataField;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptorOutput;
import com.softwarelogistics.oshgeo.poc.models.Offering;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.Sensor;
import com.softwarelogistics.oshgeo.poc.models.SensorReading;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.services.SosClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetSOSSensorValuesTask extends AsyncTask<SensorHubUpdateRequest, String, List<SensorValue>> {

    public GetSensorValuesResponseHandler responseHandler = null;

    public ProgressHandler progressHandler = null;

    @Override
    protected List<SensorValue> doInBackground(SensorHubUpdateRequest... args) {
        List<SensorValue> values = new ArrayList<>();

        //TODO: We likely should just pull this from the GeoPackage since everything is there, this ensures we don't miss anything though.
        publishProgress("Getting Manifest");

        OpenSensorHub hub = args[0].Hub;
        OSHDataContext ctx = args[0].DataContext;

        SosClient client = new SosClient(hub.SecureConnection,hub.URI,hub.Port);
        Capabilities capabilities = client.loadOSHData();
        if(capabilities != null) {
            for(Offering offering : capabilities.Offerings){
                ObservationDescriptor descriptor = client.loadObservationDescriptor(offering.Procedure);
                capabilities.Descriptors.add(descriptor);

                Sensor sensor = ctx.findSensor(hub.Id, descriptor.Id);


                for(ObservationDescriptorOutput output : descriptor.Outputs) {
                    for(ObservationDescriptorDataField field : output.Fields) {
                        publishProgress(String.format("Sensor Value: %s - %s", offering.Name, field.Label));
                        if(field.FieldType != ObservationDescriptorDataField.FieldTypes.Time) {
                            SensorValue value = client.getSensorValue(descriptor, offering, field);
                            if(value != null) {
                                value.SensorId = sensor.Id;
                                value.HubId = hub.Id;
                                values.add(value);
                            }
                        }
                    }
                }

                for(ObservationDescriptorOutput output : descriptor.DataStreams) {
                    for(ObservationDescriptorDataField field : output.Fields) {
                        publishProgress(String.format("Sensor Value: %s - %s", offering.Name, field.Label));
                        if(field.FieldType != ObservationDescriptorDataField.FieldTypes.Time) {
                            SensorValue value = client.getSensorValue(descriptor, offering, field);
                            if(value != null) {
                                value.SensorId = sensor.Id;
                                value.HubId = hub.Id;
                                values.add(value);
                            }
                        }
                    }
                }

                ctx.addReadings(hub, sensor, values);
            }
        }

        return values;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        progressHandler.progressUpdated(String.format(values[0]));
    }

    @Override
    protected void onPostExecute(List<SensorValue> values) {
        if(responseHandler != null) {
            responseHandler.gotSensorValues(values);
        }
    }
}
