package com.softwarelogistics.oshgeo.poc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.softwarelogistics.oshgeo.poc.models.SensorValue;

public class SensorValuesAdapter  extends ArrayAdapter<SensorValue> {

    public SensorValuesAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
