package com.softwarelogistics.oshgeo.poc;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softwarelogistics.oshgeo.poc.utils.RelatedTableUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.attributes.AttributesColumn;
import mil.nga.geopackage.attributes.AttributesDao;
import mil.nga.geopackage.attributes.AttributesRow;
import mil.nga.geopackage.attributes.AttributesTable;
import mil.nga.geopackage.db.GeoPackageDataType;
import mil.nga.geopackage.extension.related.ExtendedRelation;
import mil.nga.geopackage.extension.related.RelatedTablesExtension;
import mil.nga.geopackage.extension.related.UserMappingDao;
import mil.nga.geopackage.extension.related.UserMappingRow;
import mil.nga.geopackage.extension.related.UserMappingTable;
import mil.nga.geopackage.extension.related.dublin.DublinCoreMetadata;
import mil.nga.geopackage.extension.related.dublin.DublinCoreType;
import mil.nga.geopackage.extension.related.media.MediaDao;
import mil.nga.geopackage.extension.related.media.MediaRow;
import mil.nga.geopackage.extension.related.media.MediaTable;
import mil.nga.geopackage.factory.GeoPackageFactory;
import mil.nga.geopackage.features.user.FeatureCursor;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.io.BitmapConverter;
import mil.nga.geopackage.map.geom.GoogleMapShape;
import mil.nga.geopackage.map.geom.GoogleMapShapeConverter;
import mil.nga.geopackage.user.custom.UserCustomColumn;
import mil.nga.sf.Geometry;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.features_map);
        mapFragment.getMapAsync(this);
    }

    protected  void loadGeoPacakge(String dbName) {
        GeoPackageManager manager = GeoPackageFactory.getManager(this);

        List<String> databases = manager.databases();
        for(String name : databases)
        {
            Log.w("pos",name);
        }

        GeoPackage pkg = manager.open(dbName);
        List<String> features = pkg.getFeatureTables();
        List<String> tiles = pkg.getTileTables();

        Log.w("pos","Package Opened");

        for(String name : features)
        {
            Log.w("pos","FEATURES: " + name);
        }

        for(String name : tiles)
        {
            Log.w("pos","TILES: " + name);
        }

        String featureTable = features.get(0);
        FeatureDao featureDao = pkg.getFeatureDao(featureTable);
        GoogleMapShapeConverter converter = new GoogleMapShapeConverter(featureDao.getProjection());
        FeatureCursor featureCursor = featureDao.queryForAll();

        try{
            while(featureCursor.moveToNext()){
                FeatureRow featureRow = featureCursor.getRow();
                GeoPackageGeometryData geometryData = featureRow.getGeometry();
                Geometry geometry = geometryData.getGeometry();
                GoogleMapShape shape = converter.toShape(geometry);
                GoogleMapShape mapShape = GoogleMapShapeConverter.addShapeToMap(mMap, shape);
                // ...
            }
        }finally{
            featureCursor.close();
        }
    }


    private  void importPackage(int resId)
    {
        GeoPackageManager manager = GeoPackageFactory.getManager(this);
        InputStream inputStream = this.getResources().openRawResource(R.raw.hacknhunt_ybor);

        boolean imported = manager.importGeoPackage("dbname", inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ybor = new LatLng(27.96137, -82.44136);
        mMap.addMarker(new MarkerOptions().position(ybor).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ybor, 13));
        loadGeoPacakge("dbname");
    }
}
