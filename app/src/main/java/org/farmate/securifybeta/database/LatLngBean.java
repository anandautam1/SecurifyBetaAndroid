package org.farmate.securifybeta.database;

/**
 * Created by Ananda on 17/09/2017.
 */

public class LatLngBean
{
    private String Title="";
    private String Snippet="";
    private String Latitude="";
    private String  Longitude="";

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public String getSnippet() {
        return Snippet;
    }
    public void setSnippet(String snippet) {
        Snippet = snippet;
    }
    public String getLatitude() {
        return Latitude;
    }
    public void setLatitude(String latitude) {
        Latitude = latitude;
    }
    public String getLongitude() {
        return Longitude;
    }
    public void setLongitude(String longitude) {
        Longitude = longitude;
    }


}