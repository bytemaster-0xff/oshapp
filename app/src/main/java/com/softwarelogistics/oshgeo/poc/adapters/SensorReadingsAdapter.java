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
import com.softwarelogistics.oshgeo.poc.models.SensorReading;

import java.util.List;

public class SensorReadingsAdapter extends ArrayAdapter<SensorReading> {
    private int mRowResourceId;
    private  List<SensorReading> mReadings;

    public SensorReadingsAdapter(@NonNull Context context, int resource, List<SensorReading> readings) {
        super(context, resource, readings);
        mReadings = readings;
        mRowResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView hubName = row.findViewById(R.id.row_sensor_reading_timestamp);
        SensorReading reading = mReadings.get(position);
        hubName.setText(reading.Timestamp.toString());

        return row;
    }
}
