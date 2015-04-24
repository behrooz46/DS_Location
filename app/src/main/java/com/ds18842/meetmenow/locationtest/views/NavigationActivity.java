package com.ds18842.meetmenow.locationtest.views;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ds18842.meetmenow.locationtest.MeetMeNow;
import com.ds18842.meetmenow.locationtest.R;

public class NavigationActivity extends ActionBarActivity implements SensorEventListener {

    public static final int INTENT_TYPE_LOCATION_CHANGE = 1;
    private ImageView image;
    private MeetMeNow app;

    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private TextView tvHeading;
    private float magneticNorth;
    private Location destLocation ;
    private TextView tvDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        app = (MeetMeNow) getApplicationContext();

        image = (ImageView) findViewById(R.id.imageViewCompass);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }

    private float calculateDegree(){
        float heading = this.magneticNorth;
        Location src = app.logicManager.getMyLocation() ;
        Location dst = app.logicManager.getDstLocation() ;
        float bearing = src.bearingTo(dst);
        heading = (bearing - heading) * -1;
        return heading ;
    }

    public synchronized void updateDirection(){
        // get the angle around the z-axis rotated
        float degree = calculateDegree();
        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

        Location src = app.logicManager.getMyLocation() ;
        Location dst = app.logicManager.getDstLocation() ;
        float dis = src.distanceTo(dst) * (float)0.000621371;
        tvDistance.setText("Distance: " + Float.toString(dis) + " miles");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.magneticNorth = Math.round(event.values[0]);
        updateDirection();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

}
