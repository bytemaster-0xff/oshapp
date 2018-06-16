package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.softwarelogistics.oshgeo.poc.R;

public class MainActivity extends AppCompatActivity {

    Button mShowDatabases;
    Button mShowSensorHubs;
    Button mShowMap;
    TextView mCurrentDBName;
    String mCurrentPackageName;

    final  int FINE_LOCATION_PERMISSION_REQUEST = 900;
    boolean hasLocationPermissions = false;

    public final static String EXTRA_DB_NAME = "SELECTEDDBNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowDatabases = findViewById(R.id.button_show_databases);
        mShowDatabases.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatabases();
            }
        });

        mShowSensorHubs = findViewById(R.id.button_show_sensor_hubs);
        mShowSensorHubs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSensorHubs();
            }
        });
        mShowSensorHubs.setVisibility(View.INVISIBLE);

        mShowMap = findViewById(R.id.button_show_maps);
        mShowMap.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });
        mShowMap.setVisibility(View.INVISIBLE);

        mCurrentDBName = findViewById(R.id.textview_current_dbname);
        mCurrentDBName.setText("Please Open or Create a Database");

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

    private void showMap() {
        Intent myIntent = new Intent(this, MapsActivity.class);
        this.startActivity(myIntent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == GeoPackagesActivity.EXTRA_DB_SELECTED_RESULTCODE) {
            mShowMap.setVisibility(View.VISIBLE);
            mShowSensorHubs.setVisibility(View.VISIBLE);
            mCurrentPackageName = data.getStringExtra(MainActivity.EXTRA_DB_NAME);
            mCurrentDBName.setText(data.getStringExtra(MainActivity.EXTRA_DB_NAME));
        }
        else {

        }
    }
}
