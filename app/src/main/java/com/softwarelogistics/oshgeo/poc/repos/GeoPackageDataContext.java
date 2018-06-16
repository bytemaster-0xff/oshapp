package com.softwarelogistics.oshgeo.poc.repos;

import mil.nga.geopackage.GeoPackage;

public class GeoPackageDataContext {
    GeoPackage mGeoPackage;

    public GeoPackageDataContext(GeoPackage geoPackage){
        mGeoPackage = geoPackage;
    }

    public void createTables() {
        OSHDataContext oshDataContext = new OSHDataContext(mGeoPackage);
        oshDataContext.createTables();
    }

    public OSHDataContext getOSHDataContext() {
        return new OSHDataContext(mGeoPackage);
    }
}
