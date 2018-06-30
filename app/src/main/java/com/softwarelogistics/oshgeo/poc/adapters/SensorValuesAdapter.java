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
import com.softwarelogistics.oshgeo.poc.models.SensorValue;

import java.util.List;

public class SensorValuesAdapter  extends ArrayAdapter<SensorValue> {
    private int mRowResourceId;
    private  List<SensorValue> mValues;

    public SensorValuesAdapter(@NonNull Context context, int resource, List<SensorValue> values) {
        super(context, resource, values);
        mRowResourceId = resource;
        mValues = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);
        SensorValue value = mValues.get(position);

        TextView txtView = row.findViewById(R.id.row_sensor_value_timestamp);
        txtView.setText(value.Timestamp.toString());

        txtView = row.findViewById(R.id.row_sensor_value_label);
        txtView.setText(value.Label.toString());

        txtView = row.findViewById(R.id.row_sensor_value_value);
        txtView.setText(value.StrValue.toString().trim());

        return row;
    }

}
