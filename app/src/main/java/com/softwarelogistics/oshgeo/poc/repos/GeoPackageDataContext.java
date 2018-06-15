package com.softwarelogistics.oshgeo.poc.repos;

import java.util.List;

import mil.nga.geopackage.GeoPackage;

public class GeoPackageDataContext {
    GeoPackage mGeoPackage;

    public GeoPackageDataContext(GeoPackage geoPackage){
        mGeoPackage = geoPackage;
    }

    public void createTables() {
        OSHDataContext oshDataContext = new OSHDataContext(mGeoPackage);
        oshDataContext.createTable();
    }

    public OSHDataContext getOSHDataContext() {
        return new OSHDataContext(mGeoPackage);
    }
}
