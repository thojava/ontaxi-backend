package vn.ontaxi.model;

import java.io.Serializable;

public class Location implements Serializable {
    private double longitude;
    private double latitude;
    private double accuracy;

    public Location() {
    }

    public Location(double latitude, double longitude, double accuracy) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
