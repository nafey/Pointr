package com.pointr.pointr.util;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.pointr.pointr.ui.MyApplication;

public class MyLocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LOG__MyLocationProvider";

    private static MyLocationProvider instance;
    private static LatLng myLocation;

    public static final int STATUS_NEW = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_FAILED = 2;

    private int status;
    private GoogleApiClient googleApiClient;

    private MyLocationProvider() {
        this.status = MyLocationProvider.STATUS_NEW;

        googleApiClient = new GoogleApiClient.Builder(MyApplication.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.googleApiClient.connect();
    }

    public static MyLocationProvider get() {
        if (instance == null) {
            instance = new MyLocationProvider();
        }

        return instance;
    }

    public int getStatus() {
        return status;
    }

    public LatLng getMyLocation() {
        return myLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected()");
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                MyLocationProvider.myLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            }

            this.status = MyLocationProvider.STATUS_READY;
            Log.d(TAG, "Location is " + myLocation.toString());
        } catch (Exception ex) {
            this.status = MyLocationProvider.STATUS_FAILED;
            Log.d(TAG, "Error caught in fetching location");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        this.status = MyLocationProvider.STATUS_FAILED;
    }
}