package com.softwarelogistics.oshgeo.poc.adapters;

import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;

public interface AcquireListHandler {
    void onRefreshHub(OpenSensorHub hubId);
    void onConnectHub(OpenSensorHub hubId);
    void onNavigateToHub(OpenSensorHub hubId);
    void onShowHubHandler(OpenSensorHub hubId);
}
