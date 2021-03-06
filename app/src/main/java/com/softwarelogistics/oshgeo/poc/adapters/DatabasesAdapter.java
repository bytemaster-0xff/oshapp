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

public class DatabasesAdapter extends ArrayAdapter<String> {

    Typeface mFontAwesome;
    List<String> mDatabases;
    int mRowResourceId;

    RemoveDatabaseHandler mRemoveDBHandler;

    public DatabasesAdapter(@NonNull Context context, int resource, List<String> databases, RemoveDatabaseHandler removeDbHandler) {
        super(context, resource, databases);

        mDatabases = databases;
        mRowResourceId = resource;
        mRemoveDBHandler = removeDbHandler;

        AssetManager assets = context.getAssets();
        mFontAwesome = Typeface.createFromAsset(assets, "fonts/fa-regular-400.ttf");
    }

    TextView.OnClickListener removeHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            String dbName = mDatabases.get((Integer) view.getTag());
            mRemoveDBHandler.onRemoveDatabase(dbName);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView dbName = row.findViewById(R.id.row_db_textview_name);
        dbName.setText(mDatabases.get(position));

        TextView removeDb = row.findViewById(R.id.row_db_textview_remove_db);
        removeDb.setTag(position);
        removeDb.setOnClickListener(removeHandler);
        removeDb.setTypeface(mFontAwesome);

        return row;
    }
}
