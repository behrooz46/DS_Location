package com.ds18842.meetmenow.locationtest.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ds18842.meetmenow.locationtest.common.Node;
import com.ds18842.meetmenow.locationtest.network.infrastructure.Neighbour;

import java.util.ArrayList;

public class PeerManager {
    private final static int TIME_INTERVAL_BETWEEN_SCANS = 60 * 1000 ;

    private final Context context;
    private final BroadcastReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter;

    public PeerManager(Context context){
        this.context = context ;

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //TODO if it's a new device, try to connect with UUID,
                    //TODO connect : create the socket, send & receive location
                    //TODO if it's an old device, we will update TTL on neighbour
                    Log.v("behrooz", device.getName() + " - " + device.getAddress());
                }
            }
        };

    }


    public void startDiscovery(BluetoothAdapter mBluetoothAdapter){
        this.mBluetoothAdapter = mBluetoothAdapter ;
        Log.v("behrooz", "start discovery");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
        this.mBluetoothAdapter.startDiscovery();
        //===================================
        new Thread()
        {
            public void run() {
                while(true){
                    //TODO run peer clean up with TTL

                    if (PeerManager.this.mBluetoothAdapter.isDiscovering())
                        PeerManager.this.mBluetoothAdapter.cancelDiscovery();
                    PeerManager.this.mBluetoothAdapter.startDiscovery();
                    try {
                        sleep(TIME_INTERVAL_BETWEEN_SCANS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    //TODO establish connection : exchange location
    //TODO after exchanging location, add that to list of neighbors and update logic about the new node
    //TODO keep list of neighbours updated

    public ArrayList<Neighbour> getNeighbors() {
        //TODO return all neighbour
        return new ArrayList<Neighbour>();
    }

    public Neighbour getNeighbor(Node next) {
        //TODO return neighbour from node
        return null ;
    }
}
