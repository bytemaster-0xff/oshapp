package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
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

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.utils.FileUtils;

import java.io.File;
import java.net.URISyntaxException;

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

    final int FINE_LOCATION_PERMISSION_REQUEST = 900;
    final int FILE_ACCESS_PERMISSION_REQUEST = 901;
    final int FILE_SELECT_CODE = 902;
    final int DB_SELECT_CODE = 903;

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            hasLocationPermissions = false;
            requestFilePermissions();
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

    private void requestFilePermissions() {
        ActivityCompat.requestPermissions(  this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                FILE_ACCESS_PERMISSION_REQUEST);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.findItem(R.id.main_menu_import_geo_package).setEnabled(true);
        menu.findItem(R.id.main_menu_export_geo_package).setEnabled(true);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.main_menu_import_geo_package:
                importGeoPacakge();
                break;

        }

        return true;
    }

    void importGeoPacakge() {
        if(!hasFilePermissions){
            Toast.makeText(this, "You must allow file permissions.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();

                    File geoPackageFile = FileUtils.getFile(this, uri);

                    try {
                        GeoDataContext ctx = new GeoDataContext(this);
                        ctx.importPackage(geoPackageFile);
                        Log.d(TAG, "package imported");
                    }
                    catch (Exception ex){
                        Log.d(TAG, ex.getLocalizedMessage());
                    }
                }



                break;
            case DB_SELECT_CODE:
                if (resultCode == GeoPackagesActivity.EXTRA_DB_SELECTED_RESULTCODE) {
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
                break;
        }
        }
    }

