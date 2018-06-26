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

import java.util.List;

public class FeaturesAdapter extends ArrayAdapter<String> {
    private int mRowResourceId;
    private Typeface mFontAwesome;
    private List<String> mFeatures;
    private RemoveFeatureHandler mRemoveFeatureHandler;

    public FeaturesAdapter(@NonNull Context context, int resource, List<String> features,
                           RemoveFeatureHandler  removeFeatureHandler) {
        super(context, resource, features);
        mRowResourceId = resource;

        mFeatures = features;
        AssetManager assets = context.getAssets();
        mFontAwesome = Typeface.createFromAsset(assets, "fonts/fa-regular-400.ttf");
        mRemoveFeatureHandler  = removeFeatureHandler;
    }

    TextView.OnClickListener remoteFeatureTable = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            String hub = mFeatures.get((Integer) view.getTag());
            mRemoveFeatureHandler.onRemoveFeature(5);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView hubName= row.findViewById(R.id.row_feature_name);
        String hub = mFeatures.get(position);
        hubName.setText(hub);

        TextView removeDb = row.findViewById(R.id.row_feature_remove);
        removeDb.setTag(position);
        removeDb.setOnClickListener(remoteFeatureTable);
        removeDb.setTypeface(mFontAwesome);

        return row;
    }
}

