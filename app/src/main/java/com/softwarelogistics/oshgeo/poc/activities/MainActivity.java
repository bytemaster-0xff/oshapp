package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.utils.FileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.factory.GeoPackageFactory;

import static android.content.Intent.EXTRA_ALLOW_MULTIPLE;
import static com.nononsenseapps.filepicker.AbstractFilePickerActivity.EXTRA_PATHS;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mShowDatabases;
    private LinearLayout mShowSensorHubs;
    private LinearLayout mShowMap;
    private LinearLayout mExport;
    private LinearLayout mImport;
    private LinearLayout mShowFeatures;
    private LinearLayout mShowAquire;

    private TextView mCurrentDBName;
    private String mCurrentPackageName;

    final int FINE_LOCATION_PERMISSION_REQUEST = 900;
    final int FILE_ACCESS_PERMISSION_REQUEST = 901;
    final int FILE_SELECT_CODE = 902;
    final int DIRECTORY_SELECT_CODE = 903;
    final int DB_SELECT_CODE = 904;

    private boolean hasLocationPermissions = false;
    private boolean hasFilePermissions = false;

    public final static String TAG = "OSHAPP";
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

        mImport = findViewById(R.id.main_geo_package);
        mImport.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                importGeoPacakge();
            }
        });
        mImport.setVisibility(View.VISIBLE);

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
        mCurrentDBName.setText("please open or create a package");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hasLocationPermissions = false;
            mShowDatabases.setVisibility(View.INVISIBLE);
            requestLocationPermissions();
        }
        else {
            hasLocationPermissions = true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            hasFilePermissions = false;
            requestFilePermissions();
        }
        else {
            hasFilePermissions = true;
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(  this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                FINE_LOCATION_PERMISSION_REQUEST);
    }

    private void requestFilePermissions() {
        ActivityCompat.requestPermissions(  this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                FILE_ACCESS_PERMISSION_REQUEST);
    }

    void importGeoPacakge() {
        if(!hasFilePermissions){
            Toast.makeText(this, "You must allow file permissions, please restart the app and accept file permissions.",
                    Toast.LENGTH_LONG).show();
            return;
        }


        try {
            Intent selectDirectoyIntent = new Intent(this, FilePickerActivity.class);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
            startActivityForResult(selectDirectoyIntent, FILE_SELECT_CODE);

        } catch (Exception e) {
            Log.e(TAG, "exception", e);
            e.printStackTrace();

            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("*/*");
        /*intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }*/
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
            break;
            case FILE_ACCESS_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasFilePermissions = true;
                }
            }
        }
    }

    private void showExport() {
        if(!hasFilePermissions){
            Toast.makeText(this, "You must allow file permissions.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent selectDirectoyIntent = new Intent(this, FilePickerActivity.class);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
            selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
            startActivityForResult(selectDirectoyIntent, DIRECTORY_SELECT_CODE);

        } catch (Exception e) {
            Log.e(TAG, "exception", e);
            e.printStackTrace();

            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
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
        this.startActivityForResult(myIntent, DB_SELECT_CODE);
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
                mCurrentDBName.setText("please open or create a package");
            }
        }
    }

    @NonNull
    public static List<Uri> getSelectedFilesFromResult(@NonNull Intent data) {
        List<Uri> result = new ArrayList<>();
        if (data.getBooleanExtra(EXTRA_ALLOW_MULTIPLE, false)) {
            List<String> paths = data.getStringArrayListExtra(EXTRA_PATHS);
            if (paths != null) {
                for (String path : paths) {
                    result.add(Uri.parse(path));
                }
            }
        } else {
            result.add(data.getData());
        }
        return result;
    }

    private void exportPackage(final File exportDirectory) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(MainActivity.TAG, "Starting Export Process");

                    String packageFileName = String.format("%s.gpkg", mCurrentPackageName);
                    GeoPackageManager manager = GeoPackageFactory.getManager(MainActivity.this);
                    manager.exportGeoPackage(mCurrentPackageName, packageFileName, exportDirectory);

                    Log.d(MainActivity.TAG, "package exproted");
                }
                catch (Exception ex) {
                    ex.printStackTrace();

                    Log.d(MainActivity.TAG, ex.getCause().getLocalizedMessage());
                    Log.d(MainActivity.TAG, ex.getMessage());
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case DIRECTORY_SELECT_CODE:
                if(resultCode == Activity.RESULT_OK) {
                     Uri outputUri =  data.getData();
                     String path = outputUri.getPath();
                     path = path.substring(5);

                    final File exportDirectory = new File(path);
                    Toast.makeText(this, "Exporting to: " + exportDirectory.getPath(), Toast.LENGTH_LONG).show();

                    exportPackage(exportDirectory);
                }

                break;

            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    File geoPackageFile = null;

                    List<Uri> files = getSelectedFilesFromResult(data);
                    for (Uri uri: files) {
                        geoPackageFile = Utils.getFileForUri(uri);
                        break;
                    }

                    if(geoPackageFile != null) {
                        try {
                            GeoDataContext ctx = new GeoDataContext(this);
                            ctx.importPackage(geoPackageFile);
                            Toast.makeText(this, "Package Imported", Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
            case DB_SELECT_CODE:
                if (resultCode == GeoPackagesActivity.EXTRA_DB_SELECTED_RESULTCODE) {
                    mShowSensorHubs.setVisibility(View.VISIBLE);
                    mShowMap.setVisibility(View.VISIBLE);
                    mShowFeatures.setVisibility(View.VISIBLE);
                    mShowAquire.setVisibility(View.VISIBLE);
                    mExport.setVisibility(View.VISIBLE);

                    mCurrentPackageName = data.getStringExtra(MainActivity.EXTRA_DB_NAME);
                    mCurrentDBName.setText(data.getStringExtra(MainActivity.EXTRA_DB_NAME));



                    GeoDataContext ctx = new GeoDataContext(this);
                    GeoPackageDataContext pkgCtx = ctx.getPackage(mCurrentPackageName);
                }
                break;
        }
        }
    }

