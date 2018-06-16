package com.softwarelogistics.oshgeo.poc.repos;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.List;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.core.contents.Contents;
import mil.nga.geopackage.core.contents.ContentsDao;

public class GeoPackageDataContext {
    private GeoPackage mGeoPackage;

    public GeoPackageDataContext(GeoPackage geoPackage){
        mGeoPackage = geoPackage;
    }

    public void createTables(LatLng northWest, LatLng southEast) {
        OSHDataContext oshDataContext = new OSHDataContext(mGeoPackage);
        oshDataContext.createTables(northWest, southEast);
    }

    public OSHDataContext getOSHDataContext() {
        return new OSHDataContext(mGeoPackage);
    }

    public List<Contents> getContents() throws SQLException {
        ContentsDao contentsDao = mGeoPackage.getContentsDao();
        return contentsDao.queryForAll();
    }
}
