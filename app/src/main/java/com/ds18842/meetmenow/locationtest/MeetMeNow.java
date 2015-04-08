package com.ds18842.meetmenow.locationtest;

import android.app.Application;
import android.content.Intent;

import com.ds18842.meetmenow.locationtest.location.GeoLocationProvider;

public class MeetMeNow extends Application{
    private GeoLocationProvider geoLocationProvider;

    public MeetMeNow(){

    }

    public GeoLocationProvider getGeoLocationProvider() {
        return geoLocationProvider;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        geoLocationProvider = new GeoLocationProvider(this);
    }
}
