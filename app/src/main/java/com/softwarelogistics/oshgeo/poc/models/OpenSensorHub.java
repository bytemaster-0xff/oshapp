package com.softwarelogistics.oshgeo.poc.models;

import android.net.wifi.WifiConfiguration;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class OpenSensorHub {
    public long Id;
    public String Name;
    public String Description;

    public LatLng Location;
    public boolean SecureConnection;
    public String URI;
    public String Path;
    public long Port;

    public Date LastContact;

    public String HubAuthType;
    public String HubUserId;
    public String HubPassword;

    public boolean LocalWiFi;
    public String SSID;
    public String SSIDPassword;

    public void TryConnect() {
        /* https://stackoverflow.com/questions/8818290/how-do-i-connect-to-a-specific-wi-fi-network-in-android-programmatically */

        WifiConfiguration wifi = new WifiConfiguration();
        wifi.SSID = "\"" + SSID + "\"";
    }

}
