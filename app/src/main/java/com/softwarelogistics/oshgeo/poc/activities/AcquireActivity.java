package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.softwarelogistics.oshgeo.poc.adapters.AcquireHubsAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.AcquireListHandler;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.SensorReading;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSSensorValuesTask;
import com.softwarelogistics.oshgeo.poc.tasks.GetSensorValuesResponseHandler;
import com.softwarelogistics.oshgeo.poc.tasks.ProgressHandler;
import com.softwarelogistics.oshgeo.poc.tasks.SensorHubUpdateRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private TextView mWiFiConnectionStatus;
    private TextView mInternetConnectionStatus;
    private TextView mSSID;

    private String mCurrentSSID;

    private List<Marker> mHubMarkers;

    private Timer mRefreshConnectionStatusTimer;

    private RelativeLayout mSensorBusyMask;
    private TextView mProgressMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquire);
        mSSID = findViewById(R.id.acquire_ssid);
        mWiFiConnectionStatus = findViewById(R.id.acquire_wifi_status);
        mInternetConnectionStatus = findViewById(R.id.acquire_internet_status);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.aquire_map);
        mMapFragment.getMapAsync(this);
        mHubsList = findViewById(R.id.acquire_list_hubs);
        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mHubMarkers = new ArrayList<>();
        checkConnectionStatus();

        mSensorBusyMask = findViewById(R.id.acquire_progress_mask);
        mProgressMessage = findViewById(R.id.acquire_progress_status);
    }

    private void refreshHubs() {
        if(mMap != null) {
            GeoDataContext ctx = new GeoDataContext(this);
            OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);
            mHubs = hubsContext.getHubs();

            for (Marker marker : mHubMarkers) {
                marker.remove();
            }

            for (OpenSensorHub hub : mHubs) {
                MarkerOptions newMarkerOptions = new MarkerOptions().position(hub.Location);
                newMarkerOptions.title(hub.Name);
                Marker newMarker = mMap.addMarker(newMarkerOptions);
                mHubMarkers.add(newMarker);
            }

            mHubAdapter = new AcquireHubsAdapter(this, R.layout.list_row_aquire_hub, mHubs, mCurrentSSID,
                    mCurrentLocation, this);

            mHubsList.setAdapter(mHubAdapter);
        }
    }

    //TODO: OK, timer isn't right here, but I wasn't getting the network change broadcast and it's down to the wire...so a hackin' I will go!
    @Override
    protected void onResume() {
        super.onResume();

        mRefreshConnectionStatusTimer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                AcquireActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkConnectionStatus();
                    }
                });

            }
        };
        mRefreshConnectionStatusTimer.scheduleAtFixedRate(t,1000,3000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRefreshConnectionStatusTimer.cancel();
        mRefreshConnectionStatusTimer = null;
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

    public void checkConnectionStatus() {
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                String ssid = connectionInfo.getSSID();
                ssid = ssid.replace("\"","");
                mWiFiConnectionStatus.setTextColor(Color.GREEN);
                mSSID.setText(ssid);
                if(mCurrentSSID == null || !ssid.equalsIgnoreCase(mCurrentSSID)){
                    mCurrentSSID = ssid;
                    refreshHubs();
                }
            }
            else {
                mWiFiConnectionStatus.setTextColor(Color.LTGRAY);
                mSSID.setText("-");

                if(mCurrentSSID != null){
                    refreshHubs();
                }

                mCurrentSSID = null;
            }

            mInternetConnectionStatus.setTextColor(Color.GREEN);
        }
        else {
            mWiFiConnectionStatus.setTextColor(Color.LTGRAY);
            mSSID.setText("-");
            if(mCurrentSSID != null){
                refreshHubs();
            }
            mCurrentSSID = null;
            mInternetConnectionStatus.setTextColor(Color.LTGRAY);
        }
    }


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
    public void onRefreshHub(final OpenSensorHub hub) {
        GetSOSSensorValuesTask task = new GetSOSSensorValuesTask();
        task.responseHandler = new GetSensorValuesResponseHandler() {
            @Override
            public void gotSensorValues(List<SensorValue> sensorValueList) {
                SensorReading reading = new SensorReading();
                reading.HubId = hub.Id;
                reading.Timestamp = new Date();
                GeoDataContext ctx = new GeoDataContext(AcquireActivity.this);
                OSHDataContext oshCtx = ctx.getOSHDataContext(mGeoPackageName);
                hub.LastContact = new Date();
                oshCtx.updateHub(hub);

                mSensorBusyMask.setVisibility(View.GONE);
                refreshHubs();
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
        GeoDataContext ctx = new GeoDataContext(this);

        request.Hub = hub;
        request.DataContext = ctx.getOSHDataContext(mGeoPackageName);
        task.execute(request);
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hub.Location, 13));
    }

    @Override
    public void onShowSensors(OpenSensorHub hub){
        Intent intent = new Intent(this, SensorsActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        intent.putExtra(SensorsActivity.EXTRA_HUB_ID, hub.Id);
        startActivity(intent);

    }
}
