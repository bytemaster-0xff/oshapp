package com.softwarelogistics.oshgeo.poc.tasks;

import com.softwarelogistics.oshgeo.poc.models.SensorValue;

import java.util.List;

public interface GetSensorValuesResponseHandler {
    void gotSensorValues(List<SensorValue> sensorValueList);
}
