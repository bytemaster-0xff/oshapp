package com.softwarelogistics.oshgeo.poc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.Sensor;
import com.softwarelogistics.oshgeo.poc.models.SensorReading;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

public class SensorObservationActivity extends AppCompatActivity {

    public final static String HUB_ID = "HUBID";
    public final static String SENSOR_ID = "SENSORID";

    private long mHubId;
    private long mSensorId;
    private String mDatabaseName;

    private OpenSensorHub mHub;
    private Sensor mSensor;

    private List<SensorValue> mSensorValues;
    private List<SensorReading> mSensorReadings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_observation);

        mDatabaseName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mHubId = getIntent().getLongExtra(HUB_ID, 0);
        mSensorId = getIntent().getLongExtra(SENSOR_ID,0);

        GeoDataContext ctx = new GeoDataContext(this);
        final OSHDataContext oshHubCtx = ctx.getOSHDataContext(mDatabaseName);
        mHub = oshHubCtx.getHub(mHubId);
        mSensor = oshHubCtx.findSensor(mSensorId);

        mSensorValues = oshHubCtx.getSensorCurrentValues(mSensorId);
        mSensorReadings = oshHubCtx.getReadings(mSensorId);
    }
}
