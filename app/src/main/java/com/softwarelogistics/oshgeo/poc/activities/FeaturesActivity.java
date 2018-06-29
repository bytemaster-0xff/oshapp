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

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.EditFeatureHandler;
import com.softwarelogistics.oshgeo.poc.adapters.FeaturesAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.RemoveFeatureHandler;
import com.softwarelogistics.oshgeo.poc.models.MapFeature;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.List;

public class FeaturesActivity extends AppCompatActivityBase implements RemoveFeatureHandler, EditFeatureHandler {

    public final static String FEATURE_TABLE_NAME  = "FEATURE_TABLE_NAME";

    private FeaturesAdapter mFeaturesAdapter;
    private String mGeoPackageName;
    private List<MapFeature> mMapFeatures;
    private ListView mMapFeaturesList;
    private String mFeatureTableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
        mMapFeaturesList = findViewById(R.id.features_list);
        mMapFeaturesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MapFeature feature = mMapFeatures.get(i);
                openFeature(feature);
            }
        });

        mGeoPackageName = this.getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mFeatureTableName = this.getIntent().getStringExtra(FeatureActivity.FEATURE_TABLE_NAME);

        populateFeatures();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFeatures();
    }

    private void openFeature(MapFeature feature){
        Intent intent = new Intent(this, FeatureActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        intent.putExtra(FeatureActivity.FEATURE_TABLE_NAME, mFeatureTableName);
        intent.putExtra(FeatureActivity.FEATURE_ID, feature.Id);
        startActivity(intent);
    }

    private void populateFeatures() {
        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);
        mMapFeatures = hubsContext.getFeatures(mFeatureTableName);
        mFeaturesAdapter = new FeaturesAdapter(this, R.layout.list_row_feature, mMapFeatures, this, this);
        mMapFeaturesList.setAdapter(mFeaturesAdapter);
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
                Intent intent = new Intent(this, FeatureActivity.class);
                intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
                intent.putExtra(FeatureActivity.FEATURE_TABLE_NAME, mFeatureTableName);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onRemoveFeature(final long featureId) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Features?")
                .setMessage("Are you really sure you want to remove the features?  This can not be un-done.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        GeoDataContext ctx = new GeoDataContext(FeaturesActivity.this);
                        final GeoPackageDataContext pgk = ctx.getPackage(mGeoPackageName);
                        OSHDataContext oshCtx = pgk.getOSHDataContext();
                        oshCtx.removeFeature(mFeatureTableName, featureId);
                        populateFeatures();

                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    @Override
    public void onEditFeature(long featureId) {
        Intent intent = new Intent(this, FeatureActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        intent.putExtra(FeatureActivity.FEATURE_TABLE_NAME, mFeatureTableName);
        intent.putExtra(FeatureActivity.FEATURE_ID, featureId);
        startActivity(intent);
    }
}
