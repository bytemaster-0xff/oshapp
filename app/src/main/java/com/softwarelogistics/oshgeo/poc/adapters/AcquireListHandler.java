package com.softwarelogistics.oshgeo.poc.adapters;

import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;

public interface AcquireListHandler {
    void onRefreshHub(OpenSensorHub hub);
    void onShowSensors(OpenSensorHub hub);
    void onConnectHub(OpenSensorHub hub);
    void onNavigateToHub(OpenSensorHub hub);
    void onShowHubHandler(OpenSensorHub hub);
}
