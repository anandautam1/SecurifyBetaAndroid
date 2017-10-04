package org.farmate.securifybeta.database;

/**
 * Created by Ananda on 15/09/2017.
 */

// local database sync with the securify database schema

public class jobsLocal {

    private int jobID;
    private int userID;
    private String jobNickName;
    private String registrationNumberString;
    private String email;
    private String phone;
    private String image_uri;
    private String role;
    private double gps_long;
    private double gps_lati;
    private String lastServiced;
    private String lastUpdated;

    public int getJobID() { return jobID; }

    public void setJobID( int jobID) {this.jobID = jobID;}

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getJobNickName() {
        return jobNickName;
    }

    public void setJobNickName(String jobNickName) {
        this.jobNickName = jobNickName;
    }

    public String getRegistrationNumberString() {
        return registrationNumberString;
    }

    public void setRegistrationNumberString(String registrationNumberString) {
        this.registrationNumberString = registrationNumberString;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage_uri(String image_uri) {this.image_uri = image_uri;}

    public String getImage_uri() {return this.image_uri;}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getGps_long() {
        return gps_long;
    }

    public void setGps_long(Double gps_long) {
        this.gps_long = gps_long;
    }

    public double getGps_lati() {
        return gps_lati;
    }

    public void setGps_lati(Double gps_lati) {
        this.gps_lati = gps_lati;
    }

    public String getLastServiced() {return lastServiced;}

    public void setLastServiced(String lastServiced) {this.lastServiced = lastServiced;}

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}