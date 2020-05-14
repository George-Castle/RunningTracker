package com.example.georg.runningtracker;

import android.location.Location;

public interface ICallback {
    void progressEvent(double dist, Location loc, double calories); // return distance, location and accumulated calories from service
}
