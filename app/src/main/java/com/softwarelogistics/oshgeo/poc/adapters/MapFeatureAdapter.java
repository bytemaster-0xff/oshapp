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
import com.softwarelogistics.oshgeo.poc.models.MapFeature;

import java.util.List;

public class MapFeatureAdapter extends ArrayAdapter<MapFeature> {
    MapFeatureHandler mMapFeatureHandler;
    List<MapFeature> mMapFeatures;
    int mMapFeaturRowId;


    public MapFeatureAdapter(@NonNull Context context, int resource, List<MapFeature> mapFeatures, MapFeatureHandler mapFeatureHandler) {
        super(context, resource, mapFeatures);
        mMapFeatureHandler = mapFeatureHandler;
        mMapFeaturRowId = resource;
        mMapFeatures = mapFeatures;
    }

    TextView.OnClickListener showMapFeatureDetail = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            MapFeature mapFeature = mMapFeatures.get((Integer) view.getTag());
            mMapFeatureHandler.showMapFeatureDetails(mapFeature);
        }
    };

    TextView.OnClickListener showMapFeature = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            MapFeature mapFeature = mMapFeatures.get((Integer) view.getTag());
            mMapFeatureHandler.showMapFeature(mapFeature);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mMapFeaturRowId, parent, false);

        TextView featureName = row.findViewById(R.id.row_map_feature_name);
        MapFeature feature = mMapFeatures.get(position);
        featureName .setText(feature.Name);


        TextView removeDb = row.findViewById(R.id.row_map_feature_view);
        removeDb.setTag(position);
        removeDb.setOnClickListener(showMapFeatureDetail);

        TextView editFeature = row.findViewById(R.id.row_map_feature_navigate);
        editFeature.setTag(position);
        editFeature.setOnClickListener(showMapFeature);

        return row;
    }
}
