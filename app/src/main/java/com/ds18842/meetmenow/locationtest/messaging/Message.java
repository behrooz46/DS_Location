//package com.ds18842.meetmenow.locationtest.messaging;
//
//import java.io.Serializable;
//
///**
// * Created by niro on 4/9/15.
// */
//public class Message implements Serializable {
//    private String messageContent;
//    //private Location location;
//    private boolean isInit = false;
//    private boolean isLocation = false;
//    private LocationInfo location = new LocationInfo();
//
//    public Message() {
//
//    }
//
//    public boolean getIsLocation() {
//        return isLocation;
//    }
//
//    public void setIsLocation(boolean isLocation) {
//        this.isLocation = isLocation;
//
//    }
//
//    public String getMessageContent() {
//        return messageContent;
//    }
//
//    public LocationInfo getLocation() {
//        return location;
//    }
//
//    public void setMessageContent(String messageContent) {
//        this.messageContent = messageContent;
//    }
//
//    public void setLocation(double latitude, double longitude, long time) {
//        this.location.setLatitude(latitude);
//        this.location.setLongitude(longitude);
//        this.location.setTime(time);
//    }
//
//    public boolean getIsInit() {
//        return isInit;
//    }
//
//    public void setIsInit(boolean isInit) {
//        this.isInit = isInit;
//    }
//
//
//    protected class LocationInfo implements Serializable {
//        private double latitude;
//        private double longitude;
//        private long time;
//
//        public LocationInfo() {
//        }
//
//        public double getLatitude() {
//            return latitude;
//        }
//
//        public void setLatitude(double latitude) {
//            this.latitude = latitude;
//        }
//
//        public double getLongitude() {
//            return longitude;
//        }
//
//        public void setLongitude(double longitude) {
//            this.longitude = longitude;
//        }
//
//        public long getTime() {
//            return time;
//        }
//
//        public void setTime(long time) {
//            this.time = time;
//        }
//    }
//}
