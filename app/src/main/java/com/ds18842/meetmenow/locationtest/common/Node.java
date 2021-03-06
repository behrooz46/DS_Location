package com.ds18842.meetmenow.locationtest.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Node implements Parcelable, Serializable{
    String name ;
    GeoLocation geoLocation ;

    public Node(String name, GeoLocation geoLocation) {
        this.name = name;
        this.geoLocation = geoLocation;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public String getName() {
        return name;
    }

    public Node(Parcel pc){
        this.name = pc.readString() ;
        this.geoLocation = pc.readParcelable(GeoLocation.class.getClassLoader()) ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(geoLocation, flags);
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void setName(String name) {
        this.name = name;
    }
}
