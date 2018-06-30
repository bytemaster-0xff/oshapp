package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;
import com.softwarelogistics.oshgeo.poc.utils.GeoUtils;

//TODO: This was hacked together at the last minute...

public class NavigateToHubActivity extends AppCompatActivity
        implements SensorEventListener {

    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    public final static String EXTRA_HUB_ID = "HUB_ID";

    private String mGeoPackageName;
    private long mHubId;

    LatLng mCurrentLocation;

    private SensorManager mSensorManager;

    private OpenSensorHub mHub;

    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private TextView mDistance;
    private TextView mHubName;
    private TextView mCurrentHeadingView;

    private TextView mRightArrow;
    private TextView mLeftArrow;
    private TextView mBearingToHubView;
    private Float mCurrentHeading;
    private double mBearingToHub;
    private ImageView mCompassImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_to_hub);

        mCurrentHeading = 0.0f;

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mHubId = getIntent().getLongExtra(EXTRA_HUB_ID, 0);

        mBearingToHubView = findViewById(R.id.navigate_to_hub_heading);
        mCompassImage = findViewById(R.id.navigate_to_hub_image);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mCurrentHeadingView = findViewById(R.id.navigate_to_hub_current_heading);

        mRightArrow = findViewById(R.id.activity_navigate_right);
        mRightArrow.setVisibility(View.GONE);
        mLeftArrow = findViewById(R.id.activity_navigate_left);
        mRightArrow.setVisibility(View.GONE);

        mDistance = findViewById(R.id.navigate_to_hub_distance);
        mHubName = findViewById(R.id.navigate_to_hub_hub_name);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        mLocationRequest.setInterval(2000);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    refreshLocation(latLng);
                }
            };
        };

        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext oshCtx = ctx.getOSHDataContext(mGeoPackageName);
        mHub = oshCtx.getHub(mHubId);
        mHubName.setText(mHub.Name);

        refreshPosition();
    }

    private void refreshLocation(LatLng location) {
        mCurrentLocation = location;
        mBearingToHub = GeoUtils.bearing(mCurrentLocation, mHub.Location);
        double distance = GeoUtils.distance(mCurrentLocation, mHub.Location);
        if(distance > 999.0) {
            distance = distance / 1000.0f;
            mDistance.setText(String.format("%.3f km", distance));
        }
        else {
            mDistance.setText(String.format("%.3f m", distance));
        }
    }

    protected  void refreshPosition(){
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Sorry, you do not appear to have location permissions enabled.  Please restart the app and allow access to location.", Toast.LENGTH_LONG).show();
            return;
        }

        mLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                refreshLocation(latLng);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Sorry, you do not appear to have location permissions enabled.  Please restart the app and allow access to location.", Toast.LENGTH_LONG).show();
            return;
        }

        if(mLocationClient != null) {
            mLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
        mLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float degree = Math.round(sensorEvent.values[0]);

        RotateAnimation ra = new RotateAnimation(mCurrentHeading,-degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        mCompassImage.startAnimation(ra);
        mCurrentHeading = -degree;

        double delta = mBearingToHub - (int)degree;
        int actualHeading = (int)degree;

        mBearingToHubView.setText(String.format("%d°",(int)mBearingToHub));
        mCurrentHeadingView.setText(String.format("%d°",actualHeading));

        Log.d("osh.log", String.format("%d - %d - %d", (int)delta, (int)mBearingToHub, (int)degree));

        if(mBearingToHub > 180){
            mRightArrow.setVisibility(View.VISIBLE);
            mLeftArrow.setVisibility(View.GONE);
        }
        else {
            mLeftArrow.setVisibility(View.VISIBLE);
            mRightArrow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
