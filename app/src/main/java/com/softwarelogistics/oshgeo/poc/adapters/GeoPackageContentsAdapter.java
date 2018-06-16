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

import java.util.List;

import mil.nga.geopackage.core.contents.Contents;

public class GeoPackageContentsAdapter  extends ArrayAdapter<Contents> {

    private List<Contents> mContents;
    private int mRowResourceId;

    public GeoPackageContentsAdapter(@NonNull Context context, int resource, List<Contents> contents) {
        super(context, resource, contents);

        mRowResourceId = resource;
        mContents = contents;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        Contents contents = mContents.get(position);

        ((TextView)row.findViewById(R.id.row_geo_package_contents_text_view_name)).setText(contents.getTableName());
        ((TextView)row.findViewById(R.id.row_geo_package_contents_text_view_type)).setText(contents.getDataTypeString());
        ((TextView)row.findViewById(R.id.row_geo_package_contents_text_view_description)).setText(contents.getDescription());

        return row;
    }

}
