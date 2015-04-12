package com.ds18842.meetmenow.locationtest.common;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class GeoLocation implements Parcelable, Serializable {
    Double lng;
    Double lat ;
    Long time;
    Float accuracy ;

    public GeoLocation(Location location){
        this.lng = location.getLongitude() ;
        this.lat = location.getLongitude() ;
        this.time = location.getTime() ;
        this.accuracy = location.getAccuracy() ;
    }

    public GeoLocation(Parcel pc){
        this.lng = pc.readDouble() ;
        this.lat = pc.readDouble() ;
        this.time = pc.readLong() ;
        this.accuracy = pc.readFloat() ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lng);
        dest.writeDouble(lat);
        dest.writeLong(time);
        dest.writeFloat(accuracy);
    }


    public Double getLng() { return lng; }

    public Double getLat() { return lat; }


    public double getDistance(GeoLocation l){
        if (l == null)
            return 1e10;

        return distFrom(l.getLat(), l.getLng(), getLat(), getLng());
    }

    public double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }
}
