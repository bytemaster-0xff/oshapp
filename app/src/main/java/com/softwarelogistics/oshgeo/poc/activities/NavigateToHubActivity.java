package com.softwarelogistics.oshgeo.poc.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwarelogistics.oshgeo.poc.R;

public class NavigateToHubActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager mSensorManager;

    private TextView mHeading;
    private Float mCurrentHeading;
    private ImageView mCompassImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_to_hub);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float degree = Math.round(sensorEvent.values[0]);
        mHeading.setText("Heading: " + Float.toString(degree) + " degrees");
        RotateAnimation ra = new RotateAnimation(
                mCurrentHeading,
                        -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
        0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        mCompassImage.startAnimation(ra);
        mCurrentHeading = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
