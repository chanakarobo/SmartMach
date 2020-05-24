package com.example.smartmobilevehicle;

public class UserDetails {

    String uid;
    String fullName;
    String email;
    String mobile;
    String nic;
    String emergencyContact;
    String address;
    String vehicleNumber;
    String insueranceCompany;
    String imageURL;
    String companyId;
    int status;
    int suvasariyaStatus;

    public UserDetails(String uid, String fullName, String email, String mobile, String nic, String emergencyContact, String address, String vehicleNumber, String insueranceCompany,String imageURL,String companyId,int status,int suvasariyaStatus) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.nic = nic;
        this.emergencyContact = emergencyContact;
        this.address = address;
        this.vehicleNumber = vehicleNumber;
        this.insueranceCompany = insueranceCompany;
        this.imageURL=imageURL;
        this.companyId=companyId;
        this.status=status;
        this.suvasariyaStatus=suvasariyaStatus;
    }

    public String getUid() {
        return uid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getNic() {
        return nic;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getAddress() {
        return address;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getInsueranceCompany() {
        return insueranceCompany;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getCompanyId() {
        return companyId;
    }

    public int getStatus() {
        return status;
    }

    public int getSuvasariyaStatus() {
        return suvasariyaStatus;
    }
}
