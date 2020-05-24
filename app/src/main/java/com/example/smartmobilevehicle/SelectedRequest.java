package com.example.smartmobilevehicle;

public class SelectedRequest {

    String userName;
    String userUid;

    public SelectedRequest(String userName, String userUid) {
        this.userName = userName;
        this.userUid = userUid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUid() {
        return userUid;
    }
}
