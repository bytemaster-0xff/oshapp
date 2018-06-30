package com.softwarelogistics.oshgeo.poc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.SensorValuesAdapter;
import com.softwarelogistics.oshgeo.poc.models.MapFeature;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

public class FeatureAttributesActivity extends AppCompatActivity {

    public final static String FEATURE_ID = "FEATURE_ID";
    public final static String FEATURE_TABLE_NAME = "FEATURE_TABLE_NAME";

    private String mGeoPackageName;
    private long mFeatureId;
    private String mFeatureTableName;
    private MapFeature mFeature;
    private List<SensorValue> mCurrentValues;
    private ListView mSensorValuesList;
    private SensorValuesAdapter mSensorValuesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feature_attributes);

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mFeatureId = getIntent().getLongExtra(FEATURE_ID, 0);
        mFeatureTableName = getIntent().getStringExtra(FEATURE_TABLE_NAME);

        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext oshCtx = ctx.getOSHDataContext(mGeoPackageName);

        mFeature = oshCtx.getMapFeature(mFeatureTableName, mFeatureId);
        mCurrentValues = oshCtx.getRelatedSensorValuesForFeature(mFeatureTableName, mFeatureId);

        mSensorValuesList = findViewById(R.id.feature_attribute_sensor_values);
        mSensorValuesAdapter = new SensorValuesAdapter(this, R.layout.list_row_sensor_value, mCurrentValues);
        mSensorValuesList.setAdapter(mSensorValuesAdapter);

    }
}
