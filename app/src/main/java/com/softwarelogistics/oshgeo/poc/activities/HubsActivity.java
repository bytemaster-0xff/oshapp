package com.softwarelogistics.oshgeo.poc.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.EditHubHandler;
import com.softwarelogistics.oshgeo.poc.adapters.HubsAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.RemoveHubHandler;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

public class HubsActivity extends AppCompatActivityBase implements RemoveHubHandler, EditHubHandler {

    String mGeoPackageName;
    List<OpenSensorHub> mHubs;
    ListView mHubsListView;
    HubsAdapter mHubsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hubs);

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        populateHubs();
    }

    private void populateHubs() {
        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);

        mHubs = hubsContext.getHubs();
        mHubsListView = findViewById(R.id.list_hubs);
        mHubsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenSensorHub hub = mHubs.get(i);
                openHub(hub.Id);
            }
        });

        mHubsAdapter = new HubsAdapter(this, R.layout.list_row_sensor_hub, mHubs, this, this);

        mHubsListView.setAdapter(mHubsAdapter);
        mHubsListView.invalidate();
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
                addNewHub();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateHubs();
    }

    private void openHub(long hubId){
        Intent intent = new Intent(this, SensorsActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        intent.putExtra(SensorsActivity.EXTRA_HUB_ID, hubId);
        startActivity(intent);
    }

    private void addNewHub() {
        Intent intent = new Intent(this, HubActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        startActivity(intent);
    }

    @Override
    public void onRemoveHub(final OpenSensorHub hub) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Hub?")
                .setMessage("Are you really sure you want to remove the hub?  This can not be un-done.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        GeoDataContext ctx = new GeoDataContext(HubsActivity.this);
                        OSHDataContext oshCtx = ctx.getOSHDataContext(mGeoPackageName);
                        oshCtx.removeHub(hub);
                        Toast.makeText(HubsActivity.this, "Hub Removed", Toast.LENGTH_SHORT).show();
                        populateHubs();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    @Override
    public void onEditHub(OpenSensorHub hub) {
        Intent intent = new Intent(this, HubActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        intent.putExtra(HubActivity.HUB_ID, hub.Id);
        startActivity(intent);
    }
}
