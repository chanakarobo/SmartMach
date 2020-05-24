package com.example.smartmobilevehicle;

public class MachanicRateValue {

    String machanicUid;
    String machanicStatus;
    int machanicValue;

    public MachanicRateValue(String machanicUid, String machanicStatus, int machanicValue) {
        this.machanicUid = machanicUid;
        this.machanicStatus = machanicStatus;
        this.machanicValue = machanicValue;
    }

    public String getMachanicUid() {
        return machanicUid;
    }

    public String getMachanicStatus() {
        return machanicStatus;
    }

    public int getMachanicValue() {
        return machanicValue;
    }
}
