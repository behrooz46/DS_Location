package com.ds18842.meetmenow.locationtest.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Node implements Parcelable, Serializable{
    String name ;
    GeoLocation geoLocation ;

    String address;

    public Node(String name, GeoLocation geoLocation, String address) {
        this.name = name;
        this.geoLocation = geoLocation;
        this.address = address;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Node(Parcel pc){
        this.name = pc.readString() ;
        this.geoLocation = pc.readParcelable(GeoLocation.class.getClassLoader()) ;
        this.address = pc.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(geoLocation, flags);
        dest.writeString(address);
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
