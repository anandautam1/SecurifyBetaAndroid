package org.farmate.securifybeta.database;

/**
 * Created by Ananda on 15/09/2017.
 */

// local database sync with the securify database schema

public class usersLocal {

    private int userID;
    private String fname;
    private String lname;
    private String email;
    private String phone;
    private String pass_hashed;
    private String pass_salt;
    private String role;
    private double gps_long;
    private double gps_lati;
    private int isOnline;
    private String lastUpdated;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
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

    public String getPass_hashed() {
        return pass_hashed;
    }

    public void setPass_hashed(String pass_hashed) {
        this.pass_hashed = pass_hashed;
    }

    public String getPass_salt() {
        return pass_salt;
    }

    public void setPass_salt(String pass_salt) {
        this.pass_salt = pass_salt;
    }

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

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}