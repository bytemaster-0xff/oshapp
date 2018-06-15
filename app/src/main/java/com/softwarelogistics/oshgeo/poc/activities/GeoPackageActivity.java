package com.softwarelogistics.oshgeo.poc.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.GeoLocation;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.GeoPackageDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.utils.ValidationUtils;

public class GeoPackageActivity extends AppCompatActivity {

    EditText mDatabaseName;
    Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_package);

        mDatabaseName = findViewById(R.id.edit_package_name);
        mSaveButton = findViewById(R.id.button_save_package);
        mSaveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDatabase();
            }
        });
    }

    private void saveDatabase() {
        GeoDataContext ctx = new GeoDataContext(this);
        String dbName = mDatabaseName.getText().toString();
        findViewById(R.id.main_layout_geo_package).requestFocus();
        if(ValidationUtils.isValidDBName(dbName)) {
            ctx.createPackage(mDatabaseName.getText().toString());
            this.finish();
        }
        else {
            Toast.makeText(this, "Invalid Database Name - Name can only contain lower case letters and numbers and must begin with a letter and be between 3 and 20 characters long.", Toast.LENGTH_LONG).show();
        }
    }
}
