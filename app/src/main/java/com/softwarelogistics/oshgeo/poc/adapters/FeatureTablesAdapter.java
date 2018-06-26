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
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;

import java.util.List;

public class FeatureTablesAdapter extends ArrayAdapter<String> {
    private int mRowResourceId;
    private Typeface mFontAwesome;
    private List<String> mFeatureTables;
    private RemoveFeatureTableHandler mRemoveFeatureTableHandler;

    public FeatureTablesAdapter(@NonNull Context context, int resource, List<String> featureTables,
                                RemoveFeatureTableHandler  removeFeatureTableHandler) {
        super(context, resource, featureTables);
        mRowResourceId = resource;

        mFeatureTables = featureTables;
        AssetManager assets = context.getAssets();
        mFontAwesome = Typeface.createFromAsset(assets, "fonts/fa-regular-400.ttf");
        mRemoveFeatureTableHandler  = removeFeatureTableHandler;
    }

    TextView.OnClickListener remoteFeatureTable = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            String hub = mFeatureTables.get((Integer) view.getTag());
            mRemoveFeatureTableHandler.onRemoveFeatureTable(hub);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView hubName= row.findViewById(R.id.row_feature_table_name);
        String hub = mFeatureTables.get(position);
        hubName.setText(hub);

        TextView removeDb = row.findViewById(R.id.row_feature_table_remove);
        removeDb.setTag(position);
        removeDb.setOnClickListener(remoteFeatureTable);
        removeDb.setTypeface(mFontAwesome);

        return row;
    }
}

