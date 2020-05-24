package com.example.smartmobilevehicle;

public class RequestDetail {

    double latitude;
    double logtitude;
    String machanicUid;
    boolean finishStatus;

    public RequestDetail(double latitude, double logtitude, String machanicUid,boolean finishStatus) {
        this.latitude = latitude;
        this.logtitude = logtitude;
        this.machanicUid = machanicUid;
        this.finishStatus = finishStatus;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLogtitude() {
        return logtitude;
    }

    public String getUid() {
        return machanicUid;
    }

    public boolean isFinishStatus() {
        return finishStatus;
    }
}
