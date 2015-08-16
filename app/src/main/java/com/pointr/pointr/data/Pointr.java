package com.pointr.pointr.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Represents the Data fetched for a location request
 */
public class Pointr {
    private String num; //id
    private LatLng loc;

    public Pointr(String num, LatLng loc) {
        this.num = num;
        this.loc = loc;
    }

    public Pointr(String num, Float lat, Float lng) {
        this.num =  num;
        this.loc = new LatLng((double) lat, (double) lng);
    }

    public String getNum() {
        return num;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "Num:" + this.num + ", Location " + loc.latitude + ", " + loc.longitude;
    }
}
