package com.heymeowcat.codefestadmin.Model;

import com.google.android.gms.maps.model.LatLng;

public class CustomLatLng {
    Double latitude;
    Double longitude;

    public CustomLatLng() {
    }

    public CustomLatLng(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude  ;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
