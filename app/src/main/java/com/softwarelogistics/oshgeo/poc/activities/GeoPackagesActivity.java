package com.softwarelogistics.oshgeo.poc.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.DatabasesAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.RemoveDatabaseHandler;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;

import java.util.List;

public class GeoPackagesActivity extends AppCompatActivityBase implements RemoveDatabaseHandler {
    ListView mDatabases;
    List<String> mPackages;

    DatabasesAdapter mDBAdapter;

    public final static int EXTRA_DB_SELECTED_RESULTCODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_packages);

        mDatabases = findViewById(R.id.list_databases);

        populateDatabases();

        mDatabases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String packageName = mPackages.get(i);
                returnDatabaseName(packageName);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateDatabases();
    }


    private void populateDatabases() {
        GeoDataContext ctx = new GeoDataContext(this);
        mPackages = ctx.getPackages();

        mDBAdapter = new DatabasesAdapter(this, R.layout.list_row_database, mPackages, this);
        mDatabases.setAdapter(mDBAdapter);
        mDatabases.invalidate();
    }

    private void returnDatabaseName(String dbName){
        Intent data = new Intent();
        data.putExtra(MainActivity.EXTRA_DB_NAME, dbName);
        setResult(EXTRA_DB_SELECTED_RESULTCODE, data);
        finish();
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
        Intent myIntent = new Intent(this, GeoPackageActivity.class);
        this.startActivity(myIntent);
    }

    @Override
    public void onRemoveDatabase(final String dbName) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Database?")
                .setMessage("Are you really sure you want to remove the database?  This can not be un-done.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        GeoDataContext ctx = new GeoDataContext(GeoPackagesActivity.this);
                        ctx.removePackage(dbName);
                        populateDatabases();
                        Toast.makeText(GeoPackagesActivity.this, "Database Removed", Toast.LENGTH_SHORT).show();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
