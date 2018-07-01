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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.MapFeatureAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.MapFeatureHandler;
import com.softwarelogistics.oshgeo.poc.models.MapFeature;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.ArrayList;
import java.util.List;

public class MapFeaturesActivity extends AppCompatActivity
        implements OnMapReadyCallback, MapFeatureHandler {


    private MapFeatureAdapter mMapFeatureAdapter;
    private String mGeoPackageName;
    private FusedLocationProviderClient mLocationClient;
    private ListView mFeaturesList;
    private SupportMapFragment mMapFragment;
    private LatLng mCurrentLocation;
    private GoogleMap mMap;
    private List<Marker> mHubMarkers;
    private List<MapFeature> mFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_features);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.features_map);
        mMapFragment.getMapAsync(this);

        mFeaturesList = findViewById(R.id.map_feature_features_list);

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mHubMarkers = new ArrayList<>();

        mFeatures = new ArrayList<>();

        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);
        List<String> featureTables = hubsContext.getFeatureTables();
        for(String featureTable : featureTables){
            List<MapFeature> features = hubsContext.getFeatures(featureTable);
            mFeatures.addAll(features);
        }

        mMapFeatureAdapter = new MapFeatureAdapter(this, R.layout.list_row_map_feature, mFeatures, this);
        mFeaturesList.setAdapter(mMapFeatureAdapter);

    }

    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
        mCurrentLocation = latLng;
        mCurrentLocation = latLng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
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
            }
        });

        mMap.setOnMapClickListener(mapClickListener);
    }

    @Override
    public void showMapFeature(MapFeature feature) {

    }

    @Override
    public void showMapFeatureDetails(MapFeature feature) {

    }
}
