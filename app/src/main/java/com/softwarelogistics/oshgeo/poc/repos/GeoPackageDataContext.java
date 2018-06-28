package com.softwarelogistics.oshgeo.poc.repos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.core.contents.Contents;
import mil.nga.geopackage.core.contents.ContentsDao;
import mil.nga.geopackage.factory.GeoPackageFactory;

public class GeoPackageDataContext {
    private GeoPackage mGeoPackage;
    private String mPackageName;

    public GeoPackageDataContext(GeoPackage geoPackage, String packageName){
        mGeoPackage = geoPackage;
        mPackageName = packageName;
    }

    public void createTables(LatLng northWest, LatLng southEast) {
        OSHDataContext oshDataContext = new OSHDataContext(mGeoPackage, mPackageName);
        oshDataContext.createTables(northWest, southEast);
    }

    public OSHDataContext getOSHDataContext() {
        return new OSHDataContext(mGeoPackage, mPackageName);
    }

    public List<Contents> getContents() throws SQLException {
        ContentsDao contentsDao = mGeoPackage.getContentsDao();
        return contentsDao.queryForAll();
    }


    public void exportGeoPackage(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String packageName = "d";

            GeoPackageManager manager = GeoPackageFactory.getManager(context);

            File exportDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
            }

            File exportedFile = new File(exportDirectory, packageName + ".gpkg");

            if (exportedFile.exists()) {
                exportedFile.delete();
            }

            manager.exportGeoPackage(packageName, exportDirectory);

            //        Log.i(LOG_NAME, "Created: " + exportedFile.getPath());

            //          Log.i(LOG_NAME, "To copy GeoPackage, run: "

//                    + "adb pull /storage/emulated/0/Documents/example.gpkg ~/git/geopackage-android");

        } else {

//            Log.w(LOG_NAME,

            //"To export the GeoPackage, grant GeoPackageSDKTests Storage permission on the emulator or phone");

        }
    }
}
