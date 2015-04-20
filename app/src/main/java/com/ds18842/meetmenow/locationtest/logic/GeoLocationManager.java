package com.ds18842.meetmenow.locationtest.logic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.ds18842.meetmenow.locationtest.common.GeoLocation;
import com.ds18842.meetmenow.locationtest.common.ILocationHandler;
import com.ds18842.meetmenow.locationtest.views.MainActivity;

public class GeoLocationManager extends Service implements LocationListener {
    private final LocationManager locationManager;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int ACCURACY_THRESHOLD_METER = 20;
    private final Context context;

    private Location location;
    private ILocationHandler handler;


    public GeoLocationManager(Context context){
        this.context = context ;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        addProvider(LocationManager.NETWORK_PROVIDER);
        addProvider(LocationManager.GPS_PROVIDER);
    }

    private void addProvider(String provider) {
        if ( locationManager.isProviderEnabled(provider) ) {
            Log.v("log", "Requesting Location Update from " + provider);
            this.locationManager.requestLocationUpdates(provider, 0, 0, this);
            onLocationChanged(locationManager.getLastKnownLocation(provider));
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}

    public void onLocationChanged(Location newLocation){
        if (isBetterLocation(newLocation, location)){
            location = newLocation ;
            if (handler != null) {
                handler.updateLocation(new GeoLocation(location));
            }
            sendLocationToUI(location);
        }
    }

    private void sendLocationToUI(Location location) {
//        Intent i=new Intent(context, MainActivity.class);
//        i.putExtra("type", MainActivity.INTENT_TYPE_LOCATION_CHANGE);
//
//        i.putExtra("lng", location.getLongitude());
//        i.putExtra("lat", location.getLatitude());
//        i.putExtra("time", location.getTime());
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Log.v("log", location.toString());
//        context.startActivity(i);
    }

    public boolean isValidLocation(){
        return location != null && location.getAccuracy() < ACCURACY_THRESHOLD_METER;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocationHandler(ILocationHandler handler){
        this.handler = handler ;
    }


    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (location == null){
            return false ;
        }

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
