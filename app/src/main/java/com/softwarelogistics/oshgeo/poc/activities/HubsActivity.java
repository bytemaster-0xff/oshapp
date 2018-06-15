package com.softwarelogistics.oshgeo.poc.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.HubsAdapter;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

import mil.nga.geopackage.GeoPackage;

public class HubsActivity extends AppCompatActivityBase {

    String mGeoPackageName;
    List<OpenSensorHub> mHubs;
    ListView mHubsListView;
    HubsAdapter mHubsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hubs);

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);

        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);

        mHubs = hubsContext.getHubs();
        mHubsListView = findViewById(R.id.list_hubs);
        mHubsAdapter = new HubsAdapter(this, R.layout.list_row_sensor_hub, mHubs);

        mHubsListView.setAdapter(mHubsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        setMenuItemFontIcon(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_add_item:
                showGeoPackageView();
                break;
        }
        return true;
    }

    private void showGeoPackageView() {
        Intent intent = new Intent(this, HubActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        startActivity(intent);
    }
}
