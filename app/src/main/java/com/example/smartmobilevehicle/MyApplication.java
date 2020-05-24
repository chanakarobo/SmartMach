package com.example.smartmobilevehicle;

import android.app.Application;

public class MyApplication extends Application {

    public static String uid="";
    public static String userEmail="";
    public static String fullname;
    public static String email;
    public static String imageurl;

    public MyApplication() {

    }

     public MyApplication(String userId){
        this.uid=userId;
     }

    public MyApplication(String userId,String userEmail){

        this.uid=userId;
        this.userEmail=userEmail;
    }


    public static String getUid() {
        return uid;
    }

    public static String getUserEmail() {
        return userEmail;
    }


}
