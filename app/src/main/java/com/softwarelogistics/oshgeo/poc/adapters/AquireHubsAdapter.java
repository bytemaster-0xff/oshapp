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
import com.google.android.gms.maps.model.LatLng;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.utils.GeoUtils;

import java.util.List;

public class AquireHubsAdapter extends ArrayAdapter<OpenSensorHub> {
    private int mRowResourceId;
    private List<OpenSensorHub> mHubs;
    private RefreshHubHandler mRefreshHubsHandler;
    private LatLng mCurrentLocation;

    public AquireHubsAdapter(@NonNull Context context, int resource, List<OpenSensorHub> hubs,
                             LatLng currentLocation, RefreshHubHandler refreshHubHandler) {
        super(context, resource, hubs);
        mRowResourceId = resource;

        mHubs = hubs;
        mRefreshHubsHandler = refreshHubHandler;
        mCurrentLocation = currentLocation;
    }

    TextView.OnClickListener refreshHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            OpenSensorHub hub = mHubs.get((Integer) view.getTag());
            mRefreshHubsHandler.onRefreshHub(hub);
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View row = inflater.inflate(mRowResourceId, parent, false);

        OpenSensorHub hub = mHubs.get(position);

        TextView txt = row.findViewById(R.id.row_acquire_hub_name);
        txt.setText(hub.Name);

        txt = row.findViewById(R.id.row_acquire_hub_description);
        txt.setText(hub.Description);

        txt = row.findViewById(R.id.row_acquire_hub_distance);
        double heading = GeoUtils.bearing(mCurrentLocation, hub.Location);
        txt.setText(String.format("%.1f°", heading));

        double distanceMeters = GeoUtils.distance(hub.Location, mCurrentLocation);
        txt = row.findViewById(R.id.row_acquire_hub_heading);
        if(distanceMeters > 999){
            txt.setText(String.format("%.2f km", distanceMeters / 1000.0));
        }
        else {
            txt.setText(String.format("%.2f m", distanceMeters));
        }

        txt = row.findViewById(R.id.row_acquire_hub_last_contact);
        if(hub.LastContact != null) {
            txt.setText(hub.LastContact.toString());
        }
        else {
            txt.setText("");
        }

        TextView refreshHub = row.findViewById(R.id.row_acquire_hub_refresh);
        refreshHub.setTag(position);
        refreshHub.setOnClickListener(refreshHandler);

        return row;
    }
}
