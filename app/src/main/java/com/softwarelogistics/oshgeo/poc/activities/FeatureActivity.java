package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.MapFeature;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

public class FeatureActivity extends AppCompatActivity {

    final static int SELECTLOCATION_REQUESTION_ID = 202;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);

        mGeoPackgeName = this.getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mFeatureTableName = this.getIntent().getStringExtra(FeatureActivity.FEATURE_TABLE_NAME);

        mFeatureName = findViewById(R.id.feature_name);
        mFeatureDescription = findViewById(R.id.feature_description);
        mCancelButton = findViewById(R.id.feature_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeatureActivity.this.finish();
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
                hubsContext.addFeature(mFeatureTableName, mMapFeature);

                FeatureActivity.this.finish();
            }
        });

        mSetLocationButton = findViewById(R.id.feature_set_location);
        mSetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickLocationIntent = new Intent(FeatureActivity.this, LocationPicker.class);
                startActivityForResult(pickLocationIntent, SELECTLOCATION_REQUESTION_ID );

            }
        });

        if(this.getIntent().hasExtra(FeatureActivity.FEATURE_ID)){
            mMapFeatureId = this.getIntent().getLongExtra(FeatureActivity.FEATURE_ID,0);
            getMapFeature(mMapFeatureId);
        }
        else {
            mMapFeatureId = 0;
        }
    }

    private void getMapFeature(long featureId){
        GeoDataContext ctx = new GeoDataContext(FeatureActivity.this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackgeName);
        mMapFeature = hubsContext.getMapFeature(mFeatureTableName, featureId);

        mFeatureLocation = mMapFeature.Location;
        mFeatureName.setText(mMapFeature.Name);
        mFeatureDescription.setText(mMapFeature.Description);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECTLOCATION_REQUESTION_ID  &&
                resultCode == LocationPicker.EXTRA_SET_LOCATION) {
            double lat = data.getDoubleExtra(LocationPicker.EXTRA_LOCATION_SET_LATITUDE, 0);
            double lng = data.getDoubleExtra(LocationPicker.EXTRA_LOCATION_SET_LONGITUDE, 0);
            mFeatureLocation = new LatLng(lat, lng);
        }
    }

}
