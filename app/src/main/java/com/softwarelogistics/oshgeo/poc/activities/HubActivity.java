package com.softwarelogistics.oshgeo.poc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.GeoLocation;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

public class HubActivity extends AppCompatActivity {
    EditText mHubName;
    EditText mHubSSID;
    EditText mHubPassword;
    EditText mIPAddress;
    Button mSaveHub;
    TextView mSetLocation;
    String mGeoPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        mHubName = findViewById(R.id.edit_hub_name);
        mHubSSID = findViewById(R.id.edit_hub_ssid);
        mHubPassword = findViewById(R.id.edit_hub_wifi_pwd);
        mIPAddress = findViewById(R.id.edit_hub_ip_addr);
        mSetLocation = findViewById(R.id.link_set_location);

        mSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocation();
            }
        });

        mSaveHub = findViewById(R.id.button_save_hub);
        mSaveHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHub();
            }
        });

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
    }

    private void setLocation() {

    }

    private void saveHub() {
        GeoDataContext ctx = new GeoDataContext(this);

        GeoPackageDataContext geoCtx = ctx.getPackage(mGeoPackageName);
        OSHDataContext oshCtx = geoCtx.getOSHDataContext();

        OpenSensorHub hub = new OpenSensorHub();
        hub.Location = new GeoLocation();
        hub.Location.Latitude = 22.5;
        hub.Location.Longitude = -87.4;
        hub.IPAddress = mIPAddress.getText().toString();
        hub.Name = mHubName.getText().toString();
        hub.SSID = mHubSSID.getText().toString();
        hub.SSIDPassword = mHubPassword.getText().toString();

        oshCtx.addHub(hub);

        finish();
    }
}
