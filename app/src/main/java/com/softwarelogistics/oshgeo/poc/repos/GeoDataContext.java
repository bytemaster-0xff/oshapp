package com.softwarelogistics.oshgeo.poc.repos;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.List;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.factory.GeoPackageFactory;

public class GeoDataContext {
    Context mContext;

    GeoPackageManager mGeoPackageManager;

    public GeoDataContext(Context context) {
        mContext = context;
        mGeoPackageManager = GeoPackageFactory.getManager(mContext);
    }

    public List<String> getPackages(){
        return mGeoPackageManager.databases();
    }

    public GeoPackageDataContext getPackage(String packageName){
        GeoPackage pkg = mGeoPackageManager.open(packageName);
        return new GeoPackageDataContext(pkg, packageName);
    }

    public boolean doesPackageExists(String packageName){
        return mGeoPackageManager.exists(packageName);
    }

    public GeoPackageDataContext createPackage(String packageName, LatLng northWest, LatLng southEast) {
        mGeoPackageManager.create(packageName);
        GeoPackageDataContext ctx = getPackage(packageName);
        ctx.createTables(northWest, southEast);
        return ctx;
    }

    public void removePackage(String packageName) {
        mGeoPackageManager.delete(packageName);
    }

    public OSHDataContext getOSHDataContext(String packageName) {
        GeoPackage pkg = mGeoPackageManager.open(packageName);
        return new OSHDataContext(pkg, packageName);
    }


    public boolean importPackage(File file) {
        return mGeoPackageManager.importGeoPackage(file);
    }
}
