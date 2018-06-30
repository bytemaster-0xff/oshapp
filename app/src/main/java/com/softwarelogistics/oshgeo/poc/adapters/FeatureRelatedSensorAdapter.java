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
import com.softwarelogistics.oshgeo.poc.models.FeatureRelatedSensor;

import java.util.List;

public class FeatureRelatedSensorAdapter extends ArrayAdapter<FeatureRelatedSensor> {
    private int mRowResourceId;
    private List<FeatureRelatedSensor> mRelatedSensors;
    private RemoveRelatedSensorHandler mRemoveRelatedFeatureHandler;

    public FeatureRelatedSensorAdapter(@NonNull Context context, int resource, List<FeatureRelatedSensor> relatedSensors,
                                       RemoveRelatedSensorHandler removeHandler) {
        super(context, resource, relatedSensors);
        mRowResourceId = resource;
        mRemoveRelatedFeatureHandler = removeHandler;
        mRelatedSensors = relatedSensors;
    }


    TextView.OnClickListener removeHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            FeatureRelatedSensor relatedSensor = mRelatedSensors.get((Integer) view.getTag());
            mRemoveRelatedFeatureHandler.onRemoveRelatedSensor(relatedSensor);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView sensorName = row.findViewById(R.id.row_feature_related_name);
        FeatureRelatedSensor hub = mRelatedSensors.get(position);
        sensorName .setText(hub.Name);

        TextView removeFeatureLink = row.findViewById(R.id.row_feature_related_remove);
        removeFeatureLink.setTag(position);
        removeFeatureLink.setOnClickListener(removeHandler);

        return row;
    }
}
