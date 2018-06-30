package com.softwarelogistics.oshgeo.poc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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

public class FeaturesAdapter extends ArrayAdapter<MapFeature> {
    private int mRowResourceId;
    private Typeface mFontAwesome;
    private List<MapFeature> mFeatures;
    private RemoveFeatureHandler mRemoveFeatureHandler;
    private EditFeatureHandler mEditFeatureHandler;
    private ViewFeatureAttributesHandler mViewFeaturAttributesHandler;

    public FeaturesAdapter(@NonNull Context context, int resource, List<MapFeature> features,
                           ViewFeatureAttributesHandler viewFeatureHandler,
                           RemoveFeatureHandler  removeFeatureHandler, EditFeatureHandler editFeatureHandler) {
        super(context, resource, features);
        mRowResourceId = resource;
        mEditFeatureHandler = editFeatureHandler;
        mViewFeaturAttributesHandler = viewFeatureHandler;

        mFeatures = features;
        AssetManager assets = context.getAssets();
        mFontAwesome = Typeface.createFromAsset(assets, "fonts/fa-regular-400.ttf");
        mRemoveFeatureHandler  = removeFeatureHandler;
    }

    TextView.OnClickListener removeFeatureTable = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
        MapFeature mapFeature = mFeatures.get((Integer) view.getTag());
        mRemoveFeatureHandler.onRemoveFeature(mapFeature.Id);
    }
    };

    TextView.OnClickListener editFeatureTable = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
        MapFeature mapFeature = mFeatures.get((Integer) view.getTag());
        mEditFeatureHandler.onEditFeature(mapFeature.Id);
        }
    };

    TextView.OnClickListener viewFeatureAttributes = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            MapFeature mapFeature = mFeatures.get((Integer) view.getTag());
            mViewFeaturAttributesHandler.onViewFeatureAttributes(mapFeature.Id);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView featureName = row.findViewById(R.id.row_feature_name);
        MapFeature feature = mFeatures.get(position);
        featureName .setText(feature.Name);

        TextView viewFeature = row.findViewById(R.id.row_feature_view);
        viewFeature.setTag(position);
        viewFeature.setOnClickListener(viewFeatureAttributes);

        TextView removeDb = row.findViewById(R.id.row_feature_remove);
        removeDb.setTag(position);
        removeDb.setOnClickListener(removeFeatureTable);

        TextView editFeature = row.findViewById(R.id.row_feature_edit);
        editFeature.setTag(position);
        editFeature.setOnClickListener(editFeatureTable);

        return row;
    }
}

