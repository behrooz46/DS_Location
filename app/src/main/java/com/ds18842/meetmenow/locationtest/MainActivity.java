package com.ds18842.meetmenow.locationtest;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    public static final int INTENT_TYPE_LOCATION_CHANGE = 1;
    private TextView txt_latValue, txt_lngValue, txt_timeValue;
    private Button btn_updateLocation;
    private MeetMeNow app;

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



}
