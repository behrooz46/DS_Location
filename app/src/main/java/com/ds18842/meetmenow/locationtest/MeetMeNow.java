package com.ds18842.meetmenow.locationtest;

import android.app.Application;

import com.ds18842.meetmenow.locationtest.logic.GeoLocationManager;
import com.ds18842.meetmenow.locationtest.logic.LogicManager;
import com.ds18842.meetmenow.locationtest.network.NetworkManager;
import com.ds18842.meetmenow.locationtest.routing.RoutingManager;

public class MeetMeNow extends Application{
    private GeoLocationManager geoLocationManager;
    private LogicManager logicManager;
    private NetworkManager networkManager;
    private RoutingManager routingManager;

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
        networkManager = new NetworkManager(this, logicManager);
        routingManager = new RoutingManager(this, logicManager);

        networkManager.setReceiver(routingManager);
        routingManager.setReceiver(logicManager);

        logicManager.setSender(routingManager);
        routingManager.setSender(networkManager);
    }
}
