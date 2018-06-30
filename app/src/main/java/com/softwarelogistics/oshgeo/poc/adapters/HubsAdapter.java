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

public class HubsAdapter extends ArrayAdapter<OpenSensorHub> {
    private int mRowResourceId;
    private List<OpenSensorHub> mHubs;
    private RemoveHubHandler mRemoveHubHandler;
    private EditHubHandler mEditHubHandler;

    public HubsAdapter(@NonNull Context context, int resource, List<OpenSensorHub> hubs,
                       RemoveHubHandler removeHubHandler, EditHubHandler editHubHandler) {
        super(context, resource, hubs);
        mRowResourceId = resource;

        mHubs = hubs;
        mRemoveHubHandler = removeHubHandler;
        mEditHubHandler = editHubHandler;
    }

    TextView.OnClickListener removeHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
        OpenSensorHub hub = mHubs.get((Integer) view.getTag());
        mRemoveHubHandler.onRemoveHub(hub);
        }
    };

    TextView.OnClickListener editHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
        OpenSensorHub hub = mHubs.get((Integer) view.getTag());
        mEditHubHandler.onEditHub(hub);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        TextView hubName= row.findViewById(R.id.row_hub_name);
        OpenSensorHub hub = mHubs.get(position);
        hubName.setText(hub.Name);

        TextView removeHub = row.findViewById(R.id.row_hub_textview_remove_hub);
        removeHub.setTag(position);
        removeHub.setOnClickListener(removeHandler);

        TextView editHub = row.findViewById(R.id.row_hub_textview_remove_hub);
        editHub.setTag(position);
        editHub.setOnClickListener(editHandler);

        return row;
    }
}
