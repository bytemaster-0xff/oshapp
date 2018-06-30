package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.AcquireHubsAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.AcquireListHandler;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.ArrayList;
import java.util.List;

public class AcquireActivity extends AppCompatActivity
        implements OnMapReadyCallback, AcquireListHandler {

    private AcquireHubsAdapter mHubAdapter;
    private String mGeoPackageName;
    private GoogleMap mMap;
    private List<OpenSensorHub> mHubs;
    private LatLng mCurrentLocation;
    private FusedLocationProviderClient mLocationClient;
    private ListView mHubsList;
    private SupportMapFragment mMapFragment;

    private List<Marker> mHubMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquire);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.aquire_map);
        mMapFragment.getMapAsync(this);
        mHubsList = findViewById(R.id.acquire_list_hubs);
        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mHubMarkers = new ArrayList<>();
    }

    private void refreshHubs() {
        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);
        mHubs = hubsContext.getHubs();

        for(Marker marker : mHubMarkers){
            marker.remove();
        }

        for(OpenSensorHub hub : mHubs){
            MarkerOptions newMarkerOptions = new MarkerOptions().position(hub.Location);
            newMarkerOptions.title(hub.Name);
            Marker newMarker = mMap.addMarker(newMarkerOptions);
            mHubMarkers.add(newMarker);
        }

        mHubAdapter = new AcquireHubsAdapter(this, R.layout.list_row_aquire_hub, mHubs,
                mCurrentLocation,this);

        mHubsList.setAdapter(mHubAdapter);
    }

    private void refreshHub(OpenSensorHub hub){

    }

    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
        mCurrentLocation = latLng;
        mCurrentLocation = latLng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        refreshHubs();
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
    public void onRefreshHub(OpenSensorHub hub) {

    }

    @Override
    public void onConnectHub(OpenSensorHub hub) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + hub.SSID + "\"";
        config.preSharedKey = "\"" + hub.SSIDPassword + "\"";
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(config);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + hub.SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
    }

    @Override
    public void onNavigateToHub(OpenSensorHub hub) {

    }

    @Override
    public void onShowHubHandler(OpenSensorHub hub) {

    }
}
