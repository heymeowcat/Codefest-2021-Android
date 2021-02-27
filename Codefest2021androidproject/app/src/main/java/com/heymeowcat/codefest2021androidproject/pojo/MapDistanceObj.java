package com.heymeowcat.codefest2021androidproject.pojo;

public class MapDistanceObj {
    int distanceInMeters;
    String distanceInText;

    public MapDistanceObj() {
    }

    public MapDistanceObj(int distanceInMeters, String distanceInText) {
        this.distanceInMeters = distanceInMeters;
        this.distanceInText = distanceInText;
    }

    public int getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(int distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public String getDistanceInText() {
        return distanceInText;
    }

    public void setDistanceInText(String distanceInText) {
        this.distanceInText = distanceInText;
    }
}
