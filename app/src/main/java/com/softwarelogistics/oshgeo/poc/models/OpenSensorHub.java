package com.softwarelogistics.oshgeo.poc.models;

import android.net.wifi.WifiConfiguration;

public class OpenSensorHub {
    public GeoLocation Location;
    public String IPAddress;
    public String Name;
    public String SSID;
    public String SSIDPassword;

    public void TryConnect() {
        /* https://stackoverflow.com/questions/8818290/how-do-i-connect-to-a-specific-wi-fi-network-in-android-programmatically */

        WifiConfiguration wifi = new WifiConfiguration();
        wifi.SSID = "\"" + SSID + "\"";
    }
}
