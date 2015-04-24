package com.ds18842.meetmenow.locationtest.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ds18842.meetmenow.locationtest.R;
import com.ds18842.meetmenow.locationtest.MeetMeNow;
import com.ds18842.meetmenow.locationtest.common.GeoLocation;
import com.ds18842.meetmenow.locationtest.common.Packet;

import org.w3c.dom.Text;

public class MainActivity extends ActionBarActivity{

    private EditText edit_main_to, edit_main_msg;
    private TextView txt_result;
    private Button btn_sendRequest;
    private MeetMeNow app;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        app = (MeetMeNow) getApplicationContext();

        txt_result = (TextView) findViewById(R.id.txt_result);
        edit_main_to = (EditText) findViewById(R.id.edit_main_to);
        edit_main_msg = (EditText) findViewById(R.id.edit_main_msg);
        btn_sendRequest = (Button) findViewById(R.id.btn_sendRequest);

        btn_sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dst = edit_main_to.getText().toString();
                String msg = edit_main_msg.getText().toString();
                boolean result = app.logicManager.sendMessageTo(dst, msg);
                txt_result.setVisibility(View.VISIBLE);
                if (result) {
                    txt_result.setText("Message sent to " + dst + ".");
                } else {
                    txt_result.setText("No user found with name " + dst + " in your network.");
                }
            }
        });

        showRequest("Test", null, null);

    }


    public void showResponse(String name, Boolean response, GeoLocation pos) {
    }

    public void showRequest(final String name, String instruction, GeoLocation pos){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Meet Me Now Request!")
                .setMessage("Do you want to meet with " + name + " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        app.logicManager.sendResponseTo(name, true);
                        Intent i = new Intent(app, NavigationActivity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        app.logicManager.sendResponseTo(name, false);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_email)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.logicManager.setMainActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.logicManager.setMainActivity(this);

        txt_result.setVisibility(View.INVISIBLE);
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
