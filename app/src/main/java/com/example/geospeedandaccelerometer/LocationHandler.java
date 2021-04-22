package com.example.geospeedandaccelerometer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationHandler {

    private static final String TAG = "LocationManagerUtil";
    private int minimumRefreshTime;
    private int minimumRefreshDistance;
    private LocationManager locationManager;
    private OnLocationUpdateListener listener;
    private LocationListener locationListener;
    private AppCompatActivity activity;

    public LocationHandler(AppCompatActivity activity, int minimumRefreshTime, int minimumRefreshDistance) {
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.minimumRefreshTime = minimumRefreshTime;
        this.minimumRefreshDistance = minimumRefreshDistance;
    }

    public void setLocationUpdateListener(OnLocationUpdateListener listener) {
        this.listener = listener;
    }


    public void startLocationUpdate() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[startLocationUpdate] is CALLED\n");
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (listener != null) {
                    listener.onLocationUpdated(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



        if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // when permissions are NOT all GRANTED
            // check whether any of their acquisition requires popup
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                new AlertDialog.Builder(this.activity)
                        .setTitle("Please grant all these permissions")
                        .setMessage("Otherwise, the application would not work properly")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION},
                                        999
                                );
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission without prompting user
                ActivityCompat.requestPermissions(
                        this.activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        999
                );
            }
        } else {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.minimumRefreshTime, this.minimumRefreshDistance, this.locationListener);
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.minimumRefreshTime, this.minimumRefreshDistance, this.locationListener);
            stringBuilder.append("both [#GPS_PROVIDER] and [#NETWORK_PROVIDER] are set up for [#locationManager]");
        }
    }

    public void stopLocationUpdate() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[stopLocationUpdate] is CALLED\n");
        if(this.locationManager != null && this.locationListener != null) {
            locationManager.removeUpdates(locationListener);
            stringBuilder.append("[#locationListener] is removed from [#locationManager]");
        }
    }

    public void setMinRefreshTimeAndMinDistance(int timeInMilli, int distanceInMeter) {
        if(timeInMilli <= 0 || distanceInMeter < 0) {
            return;
        }
        this.minimumRefreshTime = timeInMilli;
        this.minimumRefreshDistance = distanceInMeter;
        updateLocationRequest(this.minimumRefreshTime, this.minimumRefreshDistance);
    }

    public void setMinimumRefreshTime(int minimumRefreshTime) {
        if (minimumRefreshTime < 0) return;
        this.minimumRefreshTime = minimumRefreshTime;
        updateLocationRequest(minimumRefreshTime, this.minimumRefreshDistance);
    }

    public void updateLocationRequest(long minimumRefreshTime, int minimumRefreshDistance) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[updateLocationRequest] is CALLED\n");
        Log.i(TAG, "updateLocationRequest: updating location request with new refresh time: " + minimumRefreshTime + " and new refresh distance " + minimumRefreshDistance);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (this.locationManager != null && this.locationListener != null) {
            locationManager.removeUpdates(locationListener);
            stringBuilder.append("[#locationListener] is REMOVED from [#locationManager]\n");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , minimumRefreshTime, minimumRefreshDistance, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , minimumRefreshTime, minimumRefreshDistance, locationListener);
            stringBuilder.append("[#locationManager] re-register [#GPS_PROVIDER] and [NETWORK_PROVIDER] and [#locationListener] with updated [#minimumRefreshTime = " + minimumRefreshTime+"] and [#minimumRefreshDistance = " + minimumRefreshDistance+"]");
        }
    }

    public void setMinimumRefreshDistance(int minimumRefreshDistance) {
        if (minimumRefreshDistance < 0) return;
        this.minimumRefreshDistance = minimumRefreshDistance;
        updateLocationRequest(this.minimumRefreshTime, minimumRefreshDistance);

    }

    public interface OnLocationUpdateListener {
        void onLocationUpdated(Location location);
    }

}
