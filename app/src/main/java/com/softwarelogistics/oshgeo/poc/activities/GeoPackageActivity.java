package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.GeoLocation;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class GeoPackageActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private EditText mDatabaseName;
    private Button mSaveButton;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationClient;

    private Marker mStartLocation;
    private Marker mEndLocation;
    private PolygonOptions mPolygonOptions;
    private Polygon mGeoPackageRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_package);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_picker_map);

        mMapFragment.getMapAsync(this);


        mDatabaseName = findViewById(R.id.edit_package_name);
        mSaveButton = findViewById(R.id.button_save_package);
        mSaveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDatabase();
            }
        });

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void saveDatabase() {
        GeoDataContext ctx = new GeoDataContext(this);
        String dbName = mDatabaseName.getText().toString();
        findViewById(R.id.main_layout_geo_package).requestFocus();
        if(ValidationUtils.isValidDBName(dbName)) {
            ctx.createPackage(mDatabaseName.getText().toString());
            this.finish();
        }
        else {
            Toast.makeText(this, "Invalid Geo Package Name - Name can only contain lower case letters and numbers and must begin with a letter and be between 3 and 20 characters long.", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshGeoPackageRegion() {
        if(mGeoPackageRegion != null) {
            mGeoPackageRegion.remove();
            mGeoPackageRegion = null;
        }

        mPolygonOptions = new PolygonOptions();

        LatLng topLeft = new LatLng(Math.max(mStartLocation.getPosition().latitude, mEndLocation.getPosition().latitude),
                Math.min(mStartLocation.getPosition().longitude, mEndLocation.getPosition().longitude));

        LatLng topRight = new LatLng(Math.max(mStartLocation.getPosition().latitude, mEndLocation.getPosition().latitude),
                Math.max(mStartLocation.getPosition().longitude, mEndLocation.getPosition().longitude));

        LatLng bottomLeft = new LatLng(Math.min(mStartLocation.getPosition().latitude, mEndLocation.getPosition().latitude),
                Math.min(mStartLocation.getPosition().longitude, mEndLocation.getPosition().longitude));

        LatLng bottomRight = new LatLng(Math.min(mStartLocation.getPosition().latitude, mEndLocation.getPosition().latitude),
                Math.max(mStartLocation.getPosition().longitude, mEndLocation.getPosition().longitude));

        mPolygonOptions.add(topLeft);
        mPolygonOptions.add(topRight);
        mPolygonOptions.add(bottomRight);
        mPolygonOptions.add(bottomLeft);
        mPolygonOptions.add(topLeft);
        mPolygonOptions.strokeColor(Color.RED);
        mPolygonOptions.fillColor(Color.argb(50,0xff,0,0));

        mGeoPackageRegion = mMap.addPolygon(mPolygonOptions);
    }

    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(mStartLocation == null) {
                mStartLocation = mMap.addMarker(new MarkerOptions().position(latLng));
                mStartLocation.setDraggable(true);
            }
            else if(mEndLocation == null) {
                mEndLocation = mMap.addMarker(new MarkerOptions().position(latLng));
                mEndLocation.setDraggable(true);

                refreshGeoPackageRegion();
            }
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

        mLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }
        });

        mMap.setOnMapClickListener(mapClickListener);
        mMap.setOnMarkerDragListener(markerDragListener);
    }

    GoogleMap.OnMarkerDragListener markerDragListener = new GoogleMap.OnMarkerDragListener() {
        @Override public void onMarkerDragStart(Marker arg0) { }
        @Override public void onMarkerDrag(Marker arg0) { }

        @Override
        public void onMarkerDragEnd(Marker arg0) {
            refreshGeoPackageRegion();
        }
    };
}
