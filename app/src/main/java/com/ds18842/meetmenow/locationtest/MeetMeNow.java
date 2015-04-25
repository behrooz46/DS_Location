package com.ds18842.meetmenow.locationtest;

import android.app.Application;

import com.ds18842.meetmenow.locationtest.logic.GeoLocationManager;
import com.ds18842.meetmenow.locationtest.logic.LogicManager;
import com.ds18842.meetmenow.locationtest.network.NetworkManager;
import com.ds18842.meetmenow.locationtest.network.PeerManager;
import com.ds18842.meetmenow.locationtest.routing.RoutingManager;

public class MeetMeNow extends Application{
    private GeoLocationManager geoLocationManager;
    public LogicManager logicManager;
    private NetworkManager networkManager;
    private RoutingManager routingManager;
    private PeerManager peerManager;

    public MeetMeNow(){

    }

    public GeoLocationManager getGeoLocationProvider() {
        return geoLocationManager;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        logicManager = new LogicManager(this);
        geoLocationManager = new GeoLocationManager(this);
        peerManager = new PeerManager(this);
        networkManager = new NetworkManager(this, logicManager, peerManager);
        routingManager = new RoutingManager(this, logicManager, peerManager);

        //========================================= Set Location Handler
        geoLocationManager.setLocationHandler(logicManager);
        //========================================= Set Upstream Message
        networkManager.setReceiver(routingManager);
        routingManager.setReceiver(logicManager);
        peerManager.setReceiver(networkManager);
        //========================================= Set Downstrean Message
        logicManager.setSender(routingManager);
        routingManager.setSender(networkManager);

        peerManager.discoverPeers();
    }
}
