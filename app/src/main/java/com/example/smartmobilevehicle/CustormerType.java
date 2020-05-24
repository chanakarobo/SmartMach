package com.example.smartmobilevehicle;

public class CustormerType {

    String custermerType;
    boolean status;

    public CustormerType(String custermerType, boolean status) {
        this.custermerType = custermerType;
        this.status = status;
    }

    public String getCustermerType() {
        return custermerType;
    }

    public boolean getStatus() {
        return status;
    }
}
