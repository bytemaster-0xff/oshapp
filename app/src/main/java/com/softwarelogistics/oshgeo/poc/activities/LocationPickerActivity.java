package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softwarelogistics.oshgeo.poc.R;

public class LocationPickerActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    Button mSetButton;
    Button mCancelButton;
    TextView mSelectedPointLabel;
    SupportMapFragment mMapFragment;
    GoogleMap mMap;
    LatLng mSelectedLocation;

    Marker mCurrentMapMarker;
    FusedLocationProviderClient mLocationClient;

    public final static int EXTRA_SET_LOCATION = 501;
    public final static String EXTRA_LOCATION_SET_LATITUDE = "EXTRA_LOCATION_SET_LATITUDE";
    public final static String EXTRA_LOCATION_SET_LONGITUDE = "EXTRA_LOCATION_SET_LONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_picker_map);

        mMapFragment.getMapAsync(this);

        mSelectedPointLabel = findViewById(R.id.location_picker_selected_location);
        mCancelButton = findViewById(R.id.location_picker_button_cancel_location);
        mCancelButton.setOnClickListener(this.onCancelClickListener);

        mSetButton = findViewById(R.id.location_picker_button_set_location);
        mSetButton.setEnabled(false);
        mSetButton.setOnClickListener(this.onSetLocationListener);

        mSelectedPointLabel.setText("Please select a point");

        if(getIntent().hasExtra(EXTRA_LOCATION_SET_LONGITUDE) &&
                getIntent().hasExtra(EXTRA_LOCATION_SET_LATITUDE)){
            mSelectedLocation = new LatLng(getIntent().getDoubleExtra(EXTRA_LOCATION_SET_LATITUDE, 0),
                    getIntent().getDoubleExtra(EXTRA_LOCATION_SET_LONGITUDE,0));
            mSelectedPointLabel.setText(String.format("%.6f x %.6f", mSelectedLocation.latitude, mSelectedLocation.longitude));
        }

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    View.OnClickListener onSetLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent data = new Intent();
            data.putExtra(EXTRA_LOCATION_SET_LATITUDE, mSelectedLocation.latitude);
            data.putExtra(EXTRA_LOCATION_SET_LONGITUDE, mSelectedLocation.longitude);
            setResult(EXTRA_SET_LOCATION, data);
            finish();
        }
    };

    View.OnClickListener onCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LocationPickerActivity.this.finish();
        }
    };

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Sorry, you do not appear to have location permissions enabled.  Please restart the app and allow access to location.", Toast.LENGTH_LONG).show();
            return;
        }

        mMap.setMyLocationEnabled(true);

        if(mSelectedLocation != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLocation, 13));
            mCurrentMapMarker = mMap.addMarker(new MarkerOptions().position(mSelectedLocation));
        }
        else {
            mLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }
            });
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mSelectedLocation = latLng;

                mSetButton.setEnabled(true);

                mSelectedPointLabel.setText(String.format("%.6f x %.6f", latLng.latitude, latLng.longitude));

                if(mCurrentMapMarker == null) {
                    mCurrentMapMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                }
                else {
                    mCurrentMapMarker.setPosition(latLng);
                }
            }
        });
    }
}
