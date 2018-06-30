package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.SensorsAdapter;
import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.ObservationDescriptor;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.Sensor;
import com.softwarelogistics.oshgeo.poc.models.SensorReading;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSCapabilitiesResponseHandler;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSCapabilitiesTask;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSSensorValuesTask;
import com.softwarelogistics.oshgeo.poc.tasks.GetSensorValuesResponseHandler;
import com.softwarelogistics.oshgeo.poc.tasks.ProgressHandler;
import com.softwarelogistics.oshgeo.poc.tasks.SensorHubUpdateRequest;

import java.util.Date;
import java.util.List;

public class SensorsActivity extends AppCompatActivity {

    public static final String EXTRA_HUB_ID = "HUB_ID";

    private SensorsAdapter mSensorAdapter;
    private Button mRefreshButton;

    private OpenSensorHub mHub;
    private String mDatabaseName;

    private List<Sensor> mSensors;
    private ListView mSensorsListView;

    private RelativeLayout mSensorBusyMask;
    private TextView mProgressMessage;

    // https://www.androidcode.ninja/android-compass-code-example/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        mSensorsListView = findViewById(R.id.list_sensors);
        mSensorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sensor sensor = mSensors.get(i);
                showSensor(sensor.Id);
            }
        });

        mRefreshButton = findViewById(R.id.sensors_refresh_button);
        mSensorBusyMask = findViewById(R.id.sensor_progress_mask);
        mProgressMessage = findViewById(R.id.sensors_progress_status);

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshFromServer();
            }
        });

        mDatabaseName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        long hubId = getIntent().getLongExtra(SensorsActivity.EXTRA_HUB_ID, 0);

        GeoDataContext ctx = new GeoDataContext(SensorsActivity.this);
        final OSHDataContext oshHubCtx = ctx.getOSHDataContext(mDatabaseName);
        mHub = oshHubCtx .getHub(hubId);
        populateSensors();
    }

    private void populateSensors() {
        GeoDataContext ctx = new GeoDataContext(SensorsActivity.this);
        final OSHDataContext oshHubCtx = ctx.getOSHDataContext(mDatabaseName);
        mSensors = oshHubCtx.getSensors(mHub.Id);
        mSensorAdapter = new SensorsAdapter(this, mSensors, R.layout.list_row_sensor);
        mSensorsListView.setAdapter(mSensorAdapter);
        mSensorsListView.invalidate();
    }

    private void getSensorValues() {
        GetSOSSensorValuesTask task = new GetSOSSensorValuesTask();
        task.responseHandler = new GetSensorValuesResponseHandler() {
            @Override
            public void gotSensorValues(List<SensorValue> sensorValueList) {
                SensorReading reading = new SensorReading();
                reading.HubId = mHub.Id;
                reading.Timestamp = new Date();
                GeoDataContext ctx = new GeoDataContext(SensorsActivity.this);
                OSHDataContext oshCtx = ctx.getOSHDataContext(mDatabaseName);
                mHub.LastContact = new Date();
                oshCtx.updateHub(mHub);

                for(SensorValue value: sensorValueList){
                    Log.d("log.osh", value.Name + " " + value.SensorId + " " + value.Units + " " + value.StrValue);
                }

                mSensorBusyMask.setVisibility(View.GONE);
            }
        };

        task.progressHandler = new ProgressHandler() {
            @Override
            public void progressUpdated(String message) {
                mProgressMessage.setText(message);
            }
        };

        mSensorBusyMask.setVisibility(View.VISIBLE);
        SensorHubUpdateRequest request = new SensorHubUpdateRequest();
        GeoDataContext ctx = new GeoDataContext(SensorsActivity.this);

        request.Hub = mHub;
        request.DataContext = ctx.getOSHDataContext(mDatabaseName);
        task.execute(request);
    }

    private void showSensor(long sensorId){
        Intent intent = new Intent(this, SensorObservationActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mDatabaseName);
        intent.putExtra(SensorObservationActivity.HUB_ID, mHub.Id);
        intent.putExtra(SensorObservationActivity.SENSOR_ID, sensorId);
        startActivity(intent);
    }

    private void refreshFromServer() {
        GetSOSCapabilitiesTask task = new GetSOSCapabilitiesTask();
        task.responseHandler = new GetSOSCapabilitiesResponseHandler() {
            @Override
            public void gotCapabilities(Capabilities capabilities) {
                for(ObservationDescriptor descriptor : capabilities.Descriptors){
                    Sensor sensor = new Sensor();
                    sensor.HubId = mHub.Id;
                    sensor.Name = descriptor.Name;
                    sensor.SensorType = "unknown";
                    sensor.Description = descriptor.Description;
                    sensor.SensorUniqueId = descriptor.Id;
                    GeoDataContext ctx = new GeoDataContext(SensorsActivity.this);
                    final OSHDataContext oshHubCtx = ctx.getOSHDataContext(mDatabaseName);

                    oshHubCtx.refreshSensor(mHub, sensor);
                }

                mSensorBusyMask.setVisibility(View.GONE);

                populateSensors();
                getSensorValues();
            }
        };

        task.progressHandler = new ProgressHandler() {
            @Override
            public void progressUpdated(String message) {
                mProgressMessage.setText(message);
            }
        };

        mSensorBusyMask.setVisibility(View.VISIBLE);
        task.execute(mHub);
    }
}
