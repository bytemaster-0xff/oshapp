package com.softwarelogistics.oshgeo.poc.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservableProperty;
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

import mil.nga.geopackage.BoundingBox;

public class GetSOSSensorValuesTask extends AsyncTask<SensorHubUpdateRequest, String, List<SensorValue>> {

    public GetSensorValuesResponseHandler responseHandler = null;

    public ProgressHandler progressHandler = null;

    @Override
    protected List<SensorValue> doInBackground(SensorHubUpdateRequest... args) {
        List<SensorValue> values = new ArrayList<>();

        //TODO: We likely should just pull this from the GeoPackage since everything is there, this ensures we don't miss anything though.
        publishProgress("Downloading Sensor Data");

        OpenSensorHub hub = args[0].Hub;
        OSHDataContext ctx = args[0].DataContext;
        BoundingBox bb = ctx.getBoundingBox();

        SosClient client = new SosClient(bb, hub.SecureConnection,hub.URI, hub.Path, hub.Port);
        Capabilities capabilities = client.loadOSHData();
        if(capabilities != null) {
            for(Offering offering : capabilities.Offerings){
                publishProgress("Offering: " + offering.Name);

                ObservationDescriptor descriptor = client.loadObservationDescriptor(capabilities.SOSVersion, offering.Procedure);
                capabilities.Descriptors.add(descriptor);

                Sensor sensor = ctx.findSensor(hub.Id, descriptor.Id);
                List<SensorValue> sensorValues = new ArrayList<>();

                if(capabilities.SOSVersion == 2.0) {
                    for (ObservationDescriptorOutput output : descriptor.Outputs) {
                        for (ObservationDescriptorDataField field : output.Fields) {
                            publishProgress(String.format("Sensor Value: %s - %s", offering.Name, field.Label));
                            if (field.FieldType != ObservationDescriptorDataField.FieldTypes.Time) {
                                SensorValue value = client.getSensorValueV2(descriptor, capabilities.SOSVersion, offering, field);
                                if (value != null) {
                                    value.SensorId = sensor.Id;
                                    value.HubId = hub.Id;
                                    values.add(value);
                                    sensorValues.add(value);
                                }
                            }
                        }
                    }

                    for (ObservationDescriptorOutput output : descriptor.DataStreams) {
                        for (ObservationDescriptorDataField field : output.Fields) {
                            publishProgress(String.format("Sensor Value: %s - %s", offering.Name, field.Label));
                            if (field.FieldType != ObservationDescriptorDataField.FieldTypes.Time) {
                                SensorValue value = client.getSensorValueV2(descriptor, capabilities.SOSVersion, offering, field);
                                if (value != null) {
                                    value.SensorId = sensor.Id;
                                    value.HubId = hub.Id;
                                    values.add(value);
                                    sensorValues.add(value);
                                }
                            }
                        }
                    }
                }
                else if(capabilities.SOSVersion == 1.0) {
                    for(ObservableProperty property : offering.Properties) {
                        publishProgress(String.format("Sensor Value %s %s", offering.Name, property.Name));

                        List<SensorValue> observations = client.getSensorValueV1(descriptor, capabilities.SOSVersion, offering, property.Id);
                        values.addAll(observations);
                        sensorValues.addAll(observations);
                    }
                }

                ctx.addReadings(hub, sensor, sensorValues);
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
