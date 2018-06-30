package com.softwarelogistics.oshgeo.poc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

public class AcquireHubsAdapter extends ArrayAdapter<OpenSensorHub> {
    private int mRowResourceId;
    private List<OpenSensorHub> mHubs;
    private AcquireListHandler mRefreshHubsHandler;
    private LatLng mCurrentLocation;
    private String mSSID;

    public AcquireHubsAdapter(@NonNull Context context, int resource, List<OpenSensorHub> hubs, String ssid,
                              LatLng currentLocation, AcquireListHandler refreshHubHandler) {
        super(context, resource, hubs);
        mRowResourceId = resource;

        mSSID = ssid;
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

    TextView.OnClickListener connectWiFiHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            OpenSensorHub hub = mHubs.get((Integer) view.getTag());
            mRefreshHubsHandler.onConnectHub(hub);
        }
    };

    TextView.OnClickListener showHubHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            OpenSensorHub hub = mHubs.get((Integer) view.getTag());
            mRefreshHubsHandler.onShowHubHandler(hub);
        }
    };

    TextView.OnClickListener navigateToHubHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            OpenSensorHub hub = mHubs.get((Integer) view.getTag());
            mRefreshHubsHandler.onNavigateToHub(hub);
        }
    };

    TextView.OnClickListener showSensorsHandler = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            OpenSensorHub hub = mHubs.get((Integer) view.getTag());
            mRefreshHubsHandler.onShowSensors(hub);
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
        if(heading < 0){
            heading = 360.0 + heading;
        }
        txt.setText(String.format("%.1f°", heading));

        double distanceMeters = GeoUtils.distance(hub.Location, mCurrentLocation);
        txt = row.findViewById(R.id.row_acquire_hub_heading);
        if(distanceMeters > 999){
            txt.setText(String.format("%.2f km", distanceMeters / 1000.0));
        }
        else {
            txt.setText(String.format("%.2f m", distanceMeters));
        }


        TextView statusText = row.findViewById(R.id.row_acquire_hub_status);
        txt = row.findViewById(R.id.row_acquire_hub_last_contact);
        if(hub.LastContact != null) {
            statusText.setText("OK");
            txt.setText(hub.LastContact.toString());
        }
        else {
            statusText.setText("Not Ready");
            txt.setText("");
        }

        TextView showHub = row.findViewById(R.id.row_acquire_show);
        showHub.setTag(position);
        showHub.setOnClickListener(showHubHandler);

        TextView showSensors = row.findViewById(R.id.row_acquire_view_sensors);
        showSensors.setTag(position);
        showSensors.setOnClickListener(showSensorsHandler);

        TextView refreshHub = row.findViewById(R.id.row_acquire_hub_refresh);
        refreshHub.setTag(position);


        TextView connectHub = row.findViewById(R.id.row_acquire_wifi);
        if(hub.LocalWiFi) {
            connectHub.setTag(position);
            connectHub.setVisibility(View.VISIBLE);
            if(mSSID != null && mSSID.equalsIgnoreCase(hub.SSID)){
                connectHub.setTextColor(Color.GREEN);
                refreshHub.setTextColor(Color.GREEN);
                refreshHub.setOnClickListener(refreshHandler);
            }
            else {
                refreshHub.setTextColor(Color.LTGRAY);
                connectHub.setTextColor(Color.GRAY);
                refreshHub.setOnClickListener(null);
            }

            connectHub.setOnClickListener(connectWiFiHandler);
        }
        else {
            refreshHub.setOnClickListener(refreshHandler);
            connectHub.setVisibility(View.GONE);
            refreshHub.setTextColor(Color.GREEN);
        }

        TextView navigateToHub = row.findViewById(R.id.row_acquire_hub_navigate);
        navigateToHub.setTag(position);
        navigateToHub.setOnClickListener(navigateToHubHandler);

        return row;
    }
}
