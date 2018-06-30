package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.SensorsAdapter;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.Sensor;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

public class SensorPickerActivity extends AppCompatActivity {
    public final static int SENSOR_PICKED_ID= 202;
    public final static String EXTRA_SENSOR_ID = "SENSOR_ID";
    public final static String EXTRA_SENSOR_NAME = "SENSOR_NAME";

    private String mDatabaseName;
    private List<Sensor> mSensors;
    private SensorsAdapter mSensorAdapter;
    private ListView mSensorsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_picker);

        mDatabaseName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);

        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mDatabaseName);

        mSensors = hubsContext.getAllSensors();
        mSensorsList = findViewById(R.id.sensor_picker_list);
        mSensorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sensor sensor = mSensors.get(i);
                Intent data = new Intent();
                data.putExtra(EXTRA_SENSOR_ID, sensor.Id);
                data.putExtra(EXTRA_SENSOR_NAME, sensor.Name);
                setResult(SENSOR_PICKED_ID, data);
                finish();
            }
        });

        mSensorAdapter = new SensorsAdapter(this, mSensors, R.layout.list_row_sensor);
        mSensorsList.setAdapter(mSensorAdapter);
    }
}
