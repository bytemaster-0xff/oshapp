package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.Capabilities;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSCapabilitiesResponseHandler;
import com.softwarelogistics.oshgeo.poc.tasks.GetSOSCapabilitiesTask;

public class SensorsActivity extends AppCompatActivity {

    public static final String EXTRA_HUB_ID = "HUB_ID";

    OpenSensorHub mHub;
    String mDatabaseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        mDatabaseName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        long hubId = getIntent().getLongExtra(SensorsActivity.EXTRA_HUB_ID, 0);

        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext geoCtx = ctx.getOSHDataContext(mDatabaseName);

        mHub = geoCtx.getHub(hubId);

        GetSOSCapabilitiesTask task = new GetSOSCapabilitiesTask();
        task.responseHandler = new GetSOSCapabilitiesResponseHandler() {
            @Override
            public void gotCapabilities(Capabilities capabilities) {

            }
        };

        task.execute(mHub);
    }
}
