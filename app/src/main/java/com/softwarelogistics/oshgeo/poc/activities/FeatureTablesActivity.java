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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.FeatureTablesAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.RemoveFeatureTableHandler;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.utils.ValidationUtils;

import java.util.List;

public class FeatureTablesActivity extends AppCompatActivityBase implements RemoveFeatureTableHandler {

    ListView mFeaturesListView;
    String mGeoPackageName;
    List<String> mFeatureTableNames;

    EditText mFeatureName;
    EditText mFeatureDescription;
    Button mSaveNewFeatureTable;
    Button mCancelNewFeatureTable;
    LinearLayout mNewFeature;

    FeatureTablesAdapter mFeatureTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_tables);

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        GeoDataContext ctx = new GeoDataContext(this);
        final GeoPackageDataContext pgk = ctx.getPackage(mGeoPackageName);

        mFeaturesListView = findViewById(R.id.features_tables_list);
        mFeaturesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String tableName = mFeatureTableNames.get(i);
                openFeatureTable(tableName);
            }
        });

        mFeatureName = findViewById(R.id.feature_table_name);
        mFeatureDescription = findViewById(R.id.feature_table_description);
        mNewFeature = findViewById(R.id.feature_table_new);
        mSaveNewFeatureTable = findViewById(R.id.feature_table_save);
        mSaveNewFeatureTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OSHDataContext oshCtx = pgk.getOSHDataContext();

                if(ValidationUtils.isValidDBName(mFeatureName.getText().toString())) {
                    if(oshCtx.featureTableNameInUse(mFeatureName.getText().toString())){
                        Toast.makeText(FeatureTablesActivity.this, "Feature table name already in use.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    oshCtx.createFeatureTable(mFeatureName.getText().toString(), mFeatureDescription.getText().toString());
                    mNewFeature.setVisibility(View.GONE);
                    populateFeatureTables();
                }
                else{
                    Toast.makeText(FeatureTablesActivity.this, "Invalid Feature Table Name - Name can only contain lower case letters and numbers and must begin with a letter and be between 3 and 20 characters long.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mCancelNewFeatureTable = findViewById(R.id.feature_table_cancel);
        mCancelNewFeatureTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewFeature.setVisibility(View.GONE);
            }
        });

        populateFeatureTables();
    }

    private void populateFeatureTables() {
        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext oshCtx= ctx.getOSHDataContext(mGeoPackageName);
        mFeatureTableNames = oshCtx.getFeatureTables();
        mFeatureTableAdapter = new FeatureTablesAdapter(this, R.layout.list_row_feature_table, mFeatureTableNames, this);
        mFeaturesListView.setAdapter(mFeatureTableAdapter);
        mFeaturesListView.invalidate();
    }

    private void openFeatureTable(String tableName){
        Intent intent = new Intent(this, FeaturesActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        intent.putExtra(FeaturesActivity.FEATURE_TABLE_NAME, tableName);
        startActivity(intent);
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
                addFeatureTable();
                break;
        }
        return true;
    }

    private void addFeatureTable() {
        mNewFeature.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRemoveFeatureTable(final String tableName) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Features Table?")
                .setMessage("Are you really sure you want to remove the features table?  This can not be un-done.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    GeoDataContext ctx = new GeoDataContext(FeatureTablesActivity.this);
                    final GeoPackageDataContext pgk = ctx.getPackage(mGeoPackageName);
                    OSHDataContext oshCtx = pgk.getOSHDataContext();
                    oshCtx.removeFeatureTable(tableName);
                    populateFeatureTables();
                }})
            .setNegativeButton(android.R.string.no, null).show();

    }
}
