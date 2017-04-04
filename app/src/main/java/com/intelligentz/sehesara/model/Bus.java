package com.intelligentz.sehesara.model;

/**
 * Created by Lakshan on 2017-03-25.
 */

public class Bus {
    private String name;
    private double latitude;
    private double longitude;
    private String duration;

    public Bus() {
    }

    public Bus(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Bus(String name, double latitude, double longitude, String duration) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
