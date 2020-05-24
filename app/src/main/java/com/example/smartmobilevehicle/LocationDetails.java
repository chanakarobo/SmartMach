package com.example.smartmobilevehicle;

public class LocationDetails {

    String uid;
    double longtitude;
    double latitude;
    String cusType;
    String city;
    String userEmail;
    boolean request;
    boolean cancelRequest;
    boolean notification;



    public LocationDetails(String uid, double latitude, double longtitude,boolean request,String cusType,String city,String userEmail,boolean cancelRequest,boolean notification) {
        this.uid = uid;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.request=request;
        this.cusType=cusType;
        this.city=city;
        this.userEmail=userEmail;
        this.cancelRequest=cancelRequest;
        this.notification=notification;
    }

    public LocationDetails(String uid,double latitude,double longtitude){

        this.uid=uid;
        this.latitude=latitude;
        this.longtitude=longtitude;

    }


    public String getUid() {

        return uid;
    }

    public double getLongtitude() {

        return longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean isRequest() {
        return request;
    }

    public String getCusType() {
        return cusType;
    }

    public String getCity() {
        return city;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean isNotification() {
        return notification;
    }

    public boolean isCancelRequest() {
        return cancelRequest;
    }
}
