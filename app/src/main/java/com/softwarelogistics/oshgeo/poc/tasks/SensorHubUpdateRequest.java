package com.softwarelogistics.oshgeo.poc.tasks;

import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import mil.nga.geopackage.BoundingBox;

public class SensorHubUpdateRequest {
    public OpenSensorHub Hub;
    public OSHDataContext DataContext;
}
