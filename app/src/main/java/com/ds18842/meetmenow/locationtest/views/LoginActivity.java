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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ds18842.meetmenow.locationtest.MeetMeNow;
import com.ds18842.meetmenow.locationtest.R;

public class LoginActivity extends ActionBarActivity {

    public static final int INTENT_TYPE_LOCATION_CHANGE = 1;
    private EditText txt_login_name, txt_login_email;
    private Button btn_login_join;
    private MeetMeNow app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = (MeetMeNow) getApplicationContext();
        txt_login_name = (EditText) findViewById(R.id.txt_login_name);
        txt_login_email = (EditText) findViewById(R.id.txt_login_email);
        btn_login_join = (Button) findViewById(R.id.btn_login_join);

        btn_login_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.logicManager.joinTheNetwork(txt_login_name.getText().toString());
                loginCheck();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginCheck() ;
    }

    private void loginCheck() {
        if (app.logicManager.isLoogedIn()){
            Intent i = new Intent(app, MainActivity.class);
            startActivity(i);
        }
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
}
