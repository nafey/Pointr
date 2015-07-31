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

    public String getNum() {
        return num;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }
}
