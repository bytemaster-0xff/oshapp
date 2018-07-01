package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;

import java.io.File;

import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.factory.GeoPackageFactory;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mShowDatabases;
    private LinearLayout mShowSensorHubs;
    private LinearLayout mShowMap;
    private LinearLayout mExport;
    private LinearLayout mShowFeatures;
    private LinearLayout mShowAquire;

    private TextView mCurrentDBName;
    private String mCurrentPackageName;

    final  int FINE_LOCATION_PERMISSION_REQUEST = 900;
    private boolean hasLocationPermissions = false;

    public final static String EXTRA_DB_NAME = "SELECTEDDBNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowDatabases = findViewById(R.id.main_packages_menu);
        mShowDatabases.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatabases();
            }
        });

        mShowSensorHubs = findViewById(R.id.main_hubs_menu);
        mShowSensorHubs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSensorHubs();
            }
        });
        mShowSensorHubs.setVisibility(View.INVISIBLE);

        mShowMap = findViewById(R.id.main_chart_menu);
        mShowMap.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaps();
            }
        });
        mShowMap.setVisibility(View.INVISIBLE);

        mExport = findViewById(R.id.main_export_menu);
        mExport.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExport();
            }
        });
        mExport.setVisibility(View.INVISIBLE);

        mShowFeatures = findViewById(R.id.main_features_menu);
        mShowFeatures.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeatures();
            }
        });
        mShowFeatures.setVisibility(View.INVISIBLE);

        mShowAquire = findViewById(R.id.main_acquire_menu);
        mShowAquire.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAquire();
            }
        });
        mShowAquire.setVisibility(View.INVISIBLE);

        //  mContentsListView = findViewById(R.id.main_list_contents);

        mCurrentDBName = findViewById(R.id.textview_current_dbname);
        mCurrentDBName.setText("-none-");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hasLocationPermissions = false;
            mShowDatabases.setVisibility(View.INVISIBLE);
            requestLocationPermissions();
        }
        else {
            hasLocationPermissions = true;
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(  this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                FINE_LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasLocationPermissions = true;
                    mShowDatabases.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showExport() {


        String packageFileName = String.format("%s.gpkg", mCurrentPackageName);

        GeoPackageManager manager = GeoPackageFactory.getManager(this);

        File exportDirectory =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!exportDirectory.exists()) {
            exportDirectory.mkdir();
        }

        File exportedFile = new File(exportDirectory, packageFileName);
        if (exportedFile.exists()) {
            exportedFile.delete();
        }

        manager.exportGeoPackage(mCurrentPackageName, exportDirectory);


/*        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri path = Uri.fromFile(exportedFile);

        //Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", exportedFile);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {"asd@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Exported Geo Package");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));*/
    }

    private void showAquire() {
        Intent myIntent = new Intent(this, AcquireActivity.class);
        myIntent.putExtra(MainActivity.EXTRA_DB_NAME, mCurrentPackageName);
        this.startActivityForResult(myIntent, 100);
    }

    private void showFeatures() {
        Intent myIntent = new Intent(this, FeatureTablesActivity.class);
        myIntent.putExtra(MainActivity.EXTRA_DB_NAME, mCurrentPackageName);
        this.startActivityForResult(myIntent, 100);
    }

    private void showMaps() {
        Intent myIntent = new Intent(this, MapFeaturesActivity.class);
        myIntent.putExtra(MainActivity.EXTRA_DB_NAME, mCurrentPackageName);
        this.startActivityForResult(myIntent, 100);
    }

    private void showSensorHubs() {
        Intent myIntent = new Intent(this, HubsActivity.class);
        myIntent.putExtra(MainActivity.EXTRA_DB_NAME, mCurrentPackageName);
        this.startActivityForResult(myIntent, 100);
    }

    private void showDatabases(){
        Intent myIntent = new Intent(this, GeoPackagesActivity.class);
        this.startActivityForResult(myIntent, 100);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mCurrentPackageName != null) {
            GeoDataContext ctx = new GeoDataContext(this);

            if(!ctx.doesPackageExists(mCurrentPackageName)){
                mShowSensorHubs.setVisibility(View.GONE);
                mShowMap.setVisibility(View.GONE);
                mShowFeatures.setVisibility(View.GONE);
                mShowAquire.setVisibility(View.GONE);
                mExport.setVisibility(View.GONE);
                mCurrentPackageName = null;
                mCurrentDBName.setText("-none-");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == GeoPackagesActivity.EXTRA_DB_SELECTED_RESULTCODE) {
            mShowSensorHubs.setVisibility(View.VISIBLE);
            mShowMap.setVisibility(View.VISIBLE);
            mShowFeatures.setVisibility(View.VISIBLE);
            mShowAquire.setVisibility(View.VISIBLE);

            mCurrentPackageName = data.getStringExtra(MainActivity.EXTRA_DB_NAME);
            mCurrentDBName.setText(data.getStringExtra(MainActivity.EXTRA_DB_NAME));

            //TODO: Should not be that difficult to export a file!
            mExport.setVisibility(View.GONE);

            GeoDataContext ctx = new GeoDataContext(this);
            GeoPackageDataContext pkgCtx = ctx.getPackage(mCurrentPackageName);

        }
        else {

        }
    }
}
