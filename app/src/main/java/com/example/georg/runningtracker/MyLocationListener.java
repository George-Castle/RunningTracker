package com.example.georg.runningtracker;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {
    static double distance;
    static Location prevLocation = null;
    static Location currentLocation = null;
    static float speed;
    public boolean tracking = true;

    @Override
    public void onLocationChanged(Location location) {
        if (prevLocation == null) {
            prevLocation = location;
        }
        speed = location.getSpeed(); //get speed to change if walking jogging or running
        currentLocation = location;
        distance += location.distanceTo(prevLocation); // accumulate distance from previous location to new location
        prevLocation = location; // then update previous location to new location
    }
    public double getSpeed(){
        return speed;
    }
    public double getDistanceTravelled(){
        return distance;
    }
    public void resetDistanceTravelled(){
        distance = 0;
        prevLocation = null;
        tracking = false;
    }
    public Location retLocation(){
        return currentLocation;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // information about the signal, i.e. number of satellites
        Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
    }
    @Override
    public void onProviderEnabled(String provider) {
        // the user enabled (for example) the GPS
        Log.d("g53mdp", "onProviderEnabled: " + provider);
    }
    @Override
    public void onProviderDisabled(String provider) {
        // the user disabled (for example) the GPS
        Log.d("g53mdp", "onProviderDisabled: " + provider);
    }

}

