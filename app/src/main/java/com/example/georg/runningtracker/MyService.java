package com.example.georg.runningtracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;
import android.widget.TextView;
public class MyService extends Service {
    RemoteCallbackList<MyBinder> remoteCallbackList = new RemoteCallbackList<MyBinder>();
    protected Counter counter;
    public static int minTime = MainActivity.minTime; // min time value specified by users selection on start page
    public static int minDistance = MainActivity.minDistance;// min distance value specified by users selection on start page
    public static long time = 0;
    public static long still = 0;
    public static boolean stillRunning;
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private MyBroadcastReceiver BatReceiver = new MyBroadcastReceiver();
    protected class Counter extends Thread implements Runnable {

        public double dist = 0;
        public Location loc;
        public boolean running = true;
        public boolean stopped = false;
        public double speed;
        public double calories = 0;
        private double prevLoc = 0;
        public Counter() {
            this.start();
        }

        public void run() {
            while(this.running) {
                try {
                    Thread.sleep(1000);
                    time = time + 1; // time counter to reset chronometer when MainActivity destroyed
                    Log.d("g53mdp", Long.toString(time));
                } catch (Exception e) {
                    return;
                }
                try {
                    dist = locationListener.getDistanceTravelled(); // continually return current distance and location
                    loc = locationListener.retLocation();
                    double km = dist/1000;
                    double miles = km/1.609; //convert current distance to miles
                    if(prevLoc == 0)
                        prevLoc = miles;
                    speed = locationListener.getSpeed(); //get the current speed of the user
                    if(speed > 0.00 && speed <= 3.4759) // if speed is greater than zero and less than or equal to walking speed
                        calories += (miles-prevLoc)*100;// add calories burnt whilst walking this distance
                    if(speed > 3.4759 && speed <=6.08283 )// if speed is greater than walking speed and less than or equal to running speed
                        calories += (miles-prevLoc)*120;// add calories burnt whilst jogging this distance
                    if(speed > 6.08283)// if speed is greater than running speed
                        calories += (miles-prevLoc)*150;// add calories burnt whilst running this distance
                    //dynamically change how many calories burnt by walking running or jogging depending on users speed in knots
                    //populations average walking, running and jogging speeds used for thresholds
                    //accumulates total calories to return to main

                    if((miles - prevLoc) == 0 && !stopped){ // if user is still change boolean and record time
                        still = time;
                        stopped = true;
                    }
                    if(stopped && (miles - prevLoc) != 0){// if user has started moving again reset boolean and still time
                        still = 0;
                        stopped = false;
                    }
                    if(still > 0) {
                        if ((time - still) > 1200 && stopped) { // if the user has been still and tracking for more than 20 minutes destroy service
                            stopSelf();
                        }
                    }

                    prevLoc = miles; //update previous location
                } catch(SecurityException e) {
                    Log.d("g53mdp", e.toString());
                }
                doCallbacks(dist, loc, calories);

            }
            time = 0;

            Log.d("g53mdp", "Service thread exiting");
        }

    }
    public void doCallbacks(double dist , Location loc, double calories) {
        final int n = remoteCallbackList.beginBroadcast();
        for (int i=0; i<n; i++) {
            remoteCallbackList.getBroadcastItem(i).callback.progressEvent(dist, loc, calories); //send distance, location and calories values to main
        }
        remoteCallbackList.finishBroadcast();
    }
    public class MyBinder extends Binder implements IInterface
    {
        @Override
        public IBinder asBinder() {
            return this;
        }

        public void registerCallback(ICallback callback) {
            this.callback = callback;
            remoteCallbackList.register(MyBinder.this);
        }
        public void unregisterCallback(ICallback callback) {
            remoteCallbackList.unregister(MyBinder.this);
        }
        ICallback callback;
    }
    public void Track(int minTime, int minDistance ){
        locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationListener.resetDistanceTravelled();
        registerReceiver(BatReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW)); // register receiver to listen for low battery

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    minTime, // minimum time interval between updates
                    minDistance, // minimum distance between updates, in metres
                    locationListener); // start receiving location updates
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }

    }
    public void StopTrack(){
        try {
            locationManager.removeUpdates(locationListener); //stop receiving location updates
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }
        unregisterReceiver(BatReceiver); // stop the broadcast receiver from listening
        locationManager = null;
        MainActivity.finalTime = time;
    }

    @Override
    public void onCreate() {
        Log.d("g53mdp", "service onCreate");
        super.onCreate();
        counter = new Counter();
        stillRunning = true;
        Track(minTime, minDistance);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d("g53mdp", "service onBind");
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("g53mdp", "service onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("g53mdp", "service onDestroy");
        counter.running = false;
        stillRunning = false;
        counter = null;
        StopTrack();
        super.onDestroy(); //reset values and destroy service
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("g53mdp", "service onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("g53mdp", "service onUnbind");
        return super.onUnbind(intent);
    }
}
