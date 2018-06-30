package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.FeatureRelatedSensorAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.RemoveRelatedSensorHandler;
import com.softwarelogistics.oshgeo.poc.models.FeatureRelatedSensor;
import com.softwarelogistics.oshgeo.poc.models.MapFeature;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

public class FeatureActivity extends AppCompatActivity implements RemoveRelatedSensorHandler {

    final static int SELECTLOCATION_REQUESTION_ID = 202;
    final static int ADD_RELATEDFATURE_REQUEST_ID = 203;
    public final static String FEATURE_TABLE_NAME = "FEATURE_TABLE_NAME";
    public final static String FEATURE_ID = "FEATURE_ID";

    private String mFeatureTableName;
    private String mGeoPackgeName;
    private long mMapFeatureId;

    private MapFeature mMapFeature;

    EditText mFeatureName;
    EditText mFeatureDescription;
    Button mSaveButton;
    Button mCancelButton;
    Button mSetLocationButton;
    LatLng mFeatureLocation;
    TextView mLocation;

    Button mAddRelatedFeatures;


    FeatureRelatedSensorAdapter mRelatedSensorsAdapter;
    ListView mRelatedfeaturesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);

        mGeoPackgeName = this.getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mFeatureTableName = this.getIntent().getStringExtra(FeatureActivity.FEATURE_TABLE_NAME);

        mRelatedfeaturesList = findViewById(R.id.feature_related_sensors);

        mLocation = findViewById(R.id.feature_location);
        mFeatureName = findViewById(R.id.feature_name);
        mFeatureDescription = findViewById(R.id.feature_description);
        mCancelButton = findViewById(R.id.feature_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeatureActivity.this.finish();
            }
        });

        mAddRelatedFeatures = findViewById(R.id.feature_add_related_table);
        mAddRelatedFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickLocationIntent = new Intent(FeatureActivity.this, SensorPickerActivity.class);
                pickLocationIntent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackgeName);
                startActivityForResult(pickLocationIntent, ADD_RELATEDFATURE_REQUEST_ID );
            }
        });

        mSaveButton = findViewById(R.id.feature_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFeatureLocation == null){
                    Toast.makeText(FeatureActivity.this, "Please set location of feature.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(mFeatureName.getText().length() == 0){
                    Toast.makeText(FeatureActivity.this, "Please provide a name for the feature.", Toast.LENGTH_LONG).show();
                    return;
                }

                mMapFeature.Location = mFeatureLocation;
                mMapFeature.Name = mFeatureName.getText().toString();
                mMapFeature.Description = mFeatureDescription.getText().toString();

                GeoDataContext ctx = new GeoDataContext(FeatureActivity.this);
                OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackgeName);
                hubsContext.saveFeature(mFeatureTableName, mMapFeature);

                FeatureActivity.this.finish();
            }
        });

        mSetLocationButton = findViewById(R.id.feature_set_location);
        mSetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent pickLocationIntent = new Intent(FeatureActivity.this, LocationPickerActivity.class);
            if(mFeatureLocation != null){
                pickLocationIntent.putExtra(LocationPickerActivity.EXTRA_LOCATION_SET_LATITUDE, mFeatureLocation.latitude);
                pickLocationIntent.putExtra(LocationPickerActivity.EXTRA_LOCATION_SET_LONGITUDE, mFeatureLocation.longitude);
            }

            startActivityForResult(pickLocationIntent, SELECTLOCATION_REQUESTION_ID );
            }
        });

        if(this.getIntent().hasExtra(FeatureActivity.FEATURE_ID)){
            mMapFeatureId = this.getIntent().getLongExtra(FeatureActivity.FEATURE_ID,0);
            getMapFeature(mMapFeatureId);
            populateRelatedSensors();
        }
        else {
            mMapFeature = new MapFeature();
            mLocation.setText("not set");
        }
    }

    private void populateRelatedSensors(){
        GeoDataContext ctx = new GeoDataContext(FeatureActivity.this);
        OSHDataContext oshCtx = ctx.getOSHDataContext(mGeoPackgeName);
        List<FeatureRelatedSensor> relatedSensors = oshCtx.getRelatedSensorsForFeature(mFeatureTableName, mMapFeatureId);

        mRelatedSensorsAdapter = new FeatureRelatedSensorAdapter(this,
                R.layout.list_row_feature_related_item, relatedSensors, this) ;
        mRelatedfeaturesList.setAdapter(mRelatedSensorsAdapter);
    }

    private void getMapFeature(long featureId){
        GeoDataContext ctx = new GeoDataContext(FeatureActivity.this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackgeName);
        mMapFeature = hubsContext.getMapFeature(mFeatureTableName, featureId);

        mFeatureLocation = mMapFeature.Location;
        mFeatureName.setText(mMapFeature.Name);
        mFeatureDescription.setText(mMapFeature.Description);
        mLocation.setText(String.format("%.6f x %.6f", mMapFeature.Location.latitude, mMapFeature.Location.longitude));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECTLOCATION_REQUESTION_ID  &&
                resultCode == LocationPickerActivity.EXTRA_SET_LOCATION) {
            double lat = data.getDoubleExtra(LocationPickerActivity.EXTRA_LOCATION_SET_LATITUDE, 0);
            double lng = data.getDoubleExtra(LocationPickerActivity.EXTRA_LOCATION_SET_LONGITUDE, 0);
            mFeatureLocation = new LatLng(lat, lng);
            mLocation.setText(String.format("%.6f x %.6f", mFeatureLocation.latitude, mFeatureLocation.longitude));
        }
        else if(requestCode == ADD_RELATEDFATURE_REQUEST_ID &&
                resultCode == SensorPickerActivity.SENSOR_PICKED_ID){

            GeoDataContext ctx = new GeoDataContext(FeatureActivity.this);
            OSHDataContext oshCtx = ctx.getOSHDataContext(mGeoPackgeName);
            long sensorId = data.getLongExtra(SensorPickerActivity.EXTRA_SENSOR_ID, 0);
            String sensorName = data.getStringExtra(SensorPickerActivity.EXTRA_SENSOR_NAME);
            oshCtx.relateFeatureToSensor(mFeatureTableName, mMapFeatureId, sensorId, sensorName, "");
            populateRelatedSensors();
        }
    }

    @Override
    public void onRemoveRelatedSensor(FeatureRelatedSensor relatedSensor) {

    }
}
