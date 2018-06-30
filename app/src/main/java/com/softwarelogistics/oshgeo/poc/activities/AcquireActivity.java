package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.AquireHubsAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.RefreshHubHandler;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

public class AcquireActivity extends AppCompatActivity
        implements OnMapReadyCallback, RefreshHubHandler {

    AquireHubsAdapter mHubAdapter;
    private String mGeoPackageName;
    private GoogleMap mMap;
    private List<OpenSensorHub> mHubs;
    private LatLng mCurrentLocation;
    private FusedLocationProviderClient mLocationClient;
    private ListView mHubsList;
    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquire);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.aquire_map);
        mMapFragment.getMapAsync(this);
        mHubsList = findViewById(R.id.acquire_list_hubs);
        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void refreshHubs() {
        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);
        mHubs = hubsContext.getHubs();

        mHubAdapter = new AquireHubsAdapter(this, R.layout.list_row_aquire_hub, mHubs,
                mCurrentLocation,this);

        mHubsList.setAdapter(mHubAdapter);
    }

    private void refreshHub(OpenSensorHub hub){

    }

    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            mCurrentLocation = latLng;
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
                mCurrentLocation = latLng;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                refreshHubs();
            }
        });

        mMap.setOnMapClickListener(mapClickListener);
    }

    @Override
    public void onRefreshHub(OpenSensorHub hubId) {

    }
}
