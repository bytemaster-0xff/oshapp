package com.softwarelogistics.oshgeo.poc.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.Sensor;

import java.util.List;

public class SensorsAdapter extends ArrayAdapter<OpenSensorHub> {
    private int mRowResourceId;

    private List<Sensor> mSensors;

    public SensorsAdapter(@NonNull Context context, List<Sensor> sensors, int resource) {
        super(context, resource);
        mRowResourceId = resource;
        mSensors = sensors;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView sensorView = row.findViewById(R.id.row_sensor_name);
        Sensor sensor = mSensors.get(position);
        sensorView.setText(sensor.Name);

        return row;
    }
}
