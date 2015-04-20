package com.ds18842.meetmenow.locationtest.views;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ds18842.meetmenow.locationtest.R;
import com.ds18842.meetmenow.locationtest.MeetMeNow;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

    public static final int INTENT_TYPE_LOCATION_CHANGE = 1;
    private TextView txt_latValue, txt_lngValue, txt_timeValue;
    private Button btn_updateLocation;
    private ImageView image;
    private MeetMeNow app;

    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private TextView tvHeading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (MeetMeNow) getApplicationContext();

        txt_latValue = (TextView) findViewById(R.id.txt_latValue);
        txt_lngValue = (TextView) findViewById(R.id.txt_lngValue);
        txt_timeValue = (TextView) findViewById(R.id.txt_timeValue);
        btn_updateLocation = (Button) findViewById(R.id.btn_updateLocation);
        btn_updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location pos = app.getGeoLocationProvider().getLocation();
                locationChange(pos.getLongitude(), pos.getLongitude(), pos.getTime());
            }
        });


        image = (ImageView) findViewById(R.id.imageViewCompass);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if( intent.getIntExtra("type", 0) == INTENT_TYPE_LOCATION_CHANGE ){
            locationInent(intent);
        }
    }

    private void locationInent(Intent intent) {
        double lng = intent.getDoubleExtra("lng", 0);
        double lat = intent.getDoubleExtra("lat", 0);
        long time = intent.getLongExtra("updatedAt", 0);
        locationChange(lng, lat, time);
    }

    private void locationChange(double lng, double lat, long time) {
        txt_lngValue.setText(lng + "");
        txt_latValue.setText(lat + "");
        txt_timeValue.setText(time + "");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

}
