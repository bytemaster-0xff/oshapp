package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservableProperty;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptorOutput;
import com.softwarelogistics.oshgeo.poc.models.ObservationType;
import com.softwarelogistics.oshgeo.poc.models.Offering;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.Sensor;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSCapabilitiesResponseHandler;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSCapabilitiesTask;

import java.util.List;

public class SensorsActivity extends AppCompatActivity {

    public static final String EXTRA_HUB_ID = "HUB_ID";

    OpenSensorHub mHub;
    String mDatabaseName;

    List<Sensor> mSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        mDatabaseName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        long hubId = getIntent().getLongExtra(SensorsActivity.EXTRA_HUB_ID, 0);

        GeoDataContext ctx = new GeoDataContext(this);
        final OSHDataContext oshHubCtx = ctx.getOSHDataContext(mDatabaseName);

        mHub = oshHubCtx .getHub(hubId);

        GetSOSCapabilitiesTask task = new GetSOSCapabilitiesTask();
        task.responseHandler = new GetSOSCapabilitiesResponseHandler() {
            @Override
            public void gotCapabilities(Capabilities capabilities) {
                for(Offering offering: capabilities.Offerings){
                    Log.d("log.osh", offering.Name);
                    for(ObservationType type : offering.ObservationTypes) {
                        Log.d("log.osh", type.Name);
                    }

                    for(ObservableProperty prop : offering.Properties) {
                        Log.d("log.osh", prop.Name);
                    }

                    Log.d("log.osh","=======================");
                }

                for(ObservationDescriptor descriptor : capabilities.Descriptors){
                    Sensor sensor = new Sensor();
                    sensor.HubId = mHub.Id;
                    sensor.Name = descriptor.Name;
                    sensor.SensorType = "unknown";
                    sensor.Description = descriptor.Description;
                    sensor.SensorUniqueId = descriptor.Id;
                    oshHubCtx.addSensor(mHub, sensor);

                    Log.d("log.osh", descriptor.Id);
                    Log.d("log.osh", descriptor.Name);
                    Log.d("log.osh", descriptor.Description);
                }
                Log.d("log.osh","=======================");
            }
        };

        task.execute(mHub);
    }
}
