package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.GeoPackageContentsAdapter;
import com.softwarelogistics.oshgeo.poc.models.GeoLocation;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.utils.ValidationUtils;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class HubActivity extends AppCompatActivity {
    Switch mSecure;
    Switch mPrivateWiFi;
    Switch mAuthType;

    EditText mHubUserName;
    EditText mHubUserPassword;
    EditText mHubName;
    EditText mHubSSID;
    EditText mHubWiFiPassword;
    EditText mIPAddress;
    EditText mPort;
    Button mSaveHub;
    TextView mSetLocation;
    String mGeoPackageName;

    LatLng mSensorLocation;

    LinearLayout mWiFiSettings;
    LinearLayout mHubAuthSettings;

    final static int SELECTLOCATION_REQUESTION_ID = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        mSecure = findViewById(R.id.edit_hub_https);
        mSecure.setTextOn("HTTPS");
        mSecure.setTextOff("HTTP");

        mWiFiSettings = findViewById(R.id.edit_hub_wifi_settings);
        mWiFiSettings.setVisibility(View.GONE);

        mPrivateWiFi = findViewById(R.id.edit_hub_private_wifi);
        mPrivateWiFi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mWiFiSettings.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
        mHubName = findViewById(R.id.edit_hub_name);
        mHubSSID = findViewById(R.id.edit_hub_ssid);
        mHubWiFiPassword = findViewById(R.id.edit_hub_wifi_pwd);
        mIPAddress = findViewById(R.id.edit_hub_ip_addr);
        mPort = findViewById(R.id.edit_hub_port);
        mPort.setText ("8181");

        mAuthType = findViewById(R.id.edit_hub_requires_password);
        mAuthType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mHubAuthSettings.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
        mHubUserName = findViewById(R.id.edit_hub_login_user_name);
        mHubUserPassword = findViewById(R.id.edit_hub_login_user_password);
        mHubAuthSettings = findViewById(R.id.edit_hub_password_settings);
        mHubAuthSettings.setVisibility(View.GONE);

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
        Intent pickLocationIntent = new Intent(this, LocationPicker.class);
        startActivityForResult(pickLocationIntent, SELECTLOCATION_REQUESTION_ID );
    }

    private void saveHub() {
        GeoDataContext ctx = new GeoDataContext(this);

        GeoPackageDataContext geoCtx = ctx.getPackage(mGeoPackageName);
        OSHDataContext oshCtx = geoCtx.getOSHDataContext();

        OpenSensorHub hub = new OpenSensorHub();

        if(mSensorLocation != null) {
            hub.Location =new LatLng(mSensorLocation.latitude, mSensorLocation.longitude);
        }
        else {
            mSetLocation.setError("Please select location of hub");
            Toast.makeText(this,"Please select location of hub", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mHubName.getText().length() == 0){
            mHubName.setError("Hub name is required");
            Toast.makeText(this,"Invalid port number", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            hub.Port = Integer.parseInt(String.valueOf(mPort.getText()));
        }
        catch (NumberFormatException ex) {
            mPort.setError("Port should be a valid port number.");
            Toast.makeText(this,"Invalid port number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!ValidationUtils.isValidIPOrDomain(String.valueOf(mIPAddress.getText()))){
            mIPAddress.setError("Invalid Hub Address");
            Toast.makeText(this,"Invalid Hub Address", Toast.LENGTH_SHORT).show();
            return;
        }

        hub.SecureConnection = mSecure.isChecked();
        hub.URI = mIPAddress.getText().toString();
        hub.Name = mHubName.getText().toString();

        if(mPrivateWiFi.isChecked()) {
            hub.LocalWiFi = mPrivateWiFi.isChecked();
            hub.SSID = mHubSSID.getText().toString();
            hub.SSIDPassword = mHubWiFiPassword.getText().toString();
        }

        if(mAuthType.isChecked()){
            hub.HubPassword = mHubUserPassword.getText().toString();
            hub.HubUserId = mHubUserName.getText().toString();
        }
        else {
            hub.HubPassword = null;
            hub.HubUserId = null;
            hub.HubAuthType = "anonymous";
        }

        oshCtx.addHub(hub);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECTLOCATION_REQUESTION_ID  &&
            resultCode == LocationPicker.EXTRA_SET_LOCATION) {
            double lat = data.getDoubleExtra(LocationPicker.EXTRA_LOCATION_SET_LATITUDE, 0);
            double lng = data.getDoubleExtra(LocationPicker.EXTRA_LOCATION_SET_LONGITUDE, 0);
            mSensorLocation = new LatLng(lat, lng);

        }
    }
}
