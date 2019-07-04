package vn.ontaxi.common.model;

import java.util.Date;

public class LocationWithDriver {
    private double longitude;
    private double latitude;
    private double accuracy;
    private Date received_time;
    private String driverCode;
    private int versionCode;
    private String driverInfo;

    public LocationWithDriver() {
    }

    public LocationWithDriver(Location location, String driverCode, int versionCode, Date received_time) {
        this.received_time = received_time;
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
        setAccuracy(location.getAccuracy());
        setDriverCode(driverCode);
        setVersionCode(versionCode);
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

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driver) {
        this.driverCode = driver;
    }

    public Date getReceived_time() {
        return received_time;
    }

    public void setReceived_time(Date received_time) {
        this.received_time = received_time;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(String driverInfo) {
        this.driverInfo = driverInfo;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
