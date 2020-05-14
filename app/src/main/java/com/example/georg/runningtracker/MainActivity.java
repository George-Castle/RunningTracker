package com.example.georg.runningtracker;

import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private com.example.georg.runningtracker.MyService.MyBinder myService = null;
    private final String CHANNEL_ID = "100";
    int NOTIFICATION_ID = 001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQ_LOCATION = 11;
    public static long finalTime = 1;
    public static int minDistance;
    public static int minTime;
    public static boolean lowBat = false;
    private double totalCalories = 0;
    private boolean tracking;
    private boolean startTag = false;
    public static GoogleMap mMap;

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
        // used to round a double to a desired amount of decimal places
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent receive = getIntent();
        minTime = receive.getIntExtra("minTime", 5);
        minDistance = receive.getIntExtra("minDistance", 1);
        // receive the minimum distance and time from the start page depending what option the user chose
        getPermissions();
        if(MyService.stillRunning) { //if service is still tracking in background and user reopens app using notification need to set variables to correct values and rebind
            startTag = true;
            this.bindService(new Intent(this, com.example.georg.runningtracker.MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);//rebind to running service
            tracking = true;
            Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
            simpleChronometer.setBase(simpleChronometer.getBase() - (MyService.time*1000)); //use service timer to reset chronometer to current time
            simpleChronometer.start(); // start chronometer from current run time
            createNotification(); //create a notification to remind user that the app is tracking
        }


    }
    public void onLowBattery(){
        if(tracking) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Battery Low");
            wl.acquire();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Battery Low")
                    .setMessage("Would you like to stop tracking?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StopTracking(findViewById(R.id.content));
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            wl.release();

            lowBat = false;
        } // if the broadcast receiver has detected low battery and the application is currently tracking,
          // display an alert dialog to the user asking them if they want to stop tracking
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
        outState.putLong("time",simpleChronometer.getBase());
        // save the current time value so that the chronometer can be
        // restored when activity is destroyed, allowing screen rotation
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);

        if (savedInstanceState != null && tracking == true) {
            simpleChronometer.setBase(savedInstanceState.getLong("time"));
            simpleChronometer.start();
        }
        // if the service is still tracking when activity is restored from screen rotation then
        // restore the elapsed time on the chronometer
    }
    public void createNotification(){
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(MainActivity.this, MainActivity.class); //reopen Main Activity when notification pressed
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Running Tracker")
                .setContentText("Currently tracking, reopen app?")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        // creates a notification which allows the user to return to the app if it has been minimised whilst tracking
    }
    public void StartTracking(View v){
        if(!tracking) {
            startService(); //start service and receiving location updates
            Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer
            simpleChronometer.setBase(SystemClock.elapsedRealtime());
            simpleChronometer.start(); // start chronometer from 0
            createNotification(); //create a notification to remind user that the app is tracking
        }else{
            Toast toast = Toast.makeText(this, "Already tracking!", Toast.LENGTH_SHORT); //if button pressed when already tracking
            toast.show();
        }
    }
    public void StopTracking(View v){
        if(tracking) {
            Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
            simpleChronometer.stop();

            Location location = new MyLocationListener().retLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            location = null; //destroy location listener
            MarkerOptions mOptions = new MarkerOptions().position(latLng).title("Finished").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            mMap.addMarker(mOptions); // get final location and display the finished marker on map
            // stop tracking location by stopping the service
            stopService();
            long elapsedMillis = SystemClock.elapsedRealtime() - simpleChronometer.getBase(); //get final time from chronometer
            simpleChronometer.setBase(SystemClock.elapsedRealtime());
            StoreData(elapsedMillis); // store data from run in database

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID); //remove notification as tracking has stopped
        }else {
            Toast toast = Toast.makeText(this, "Not tracking", Toast.LENGTH_SHORT);
            toast.show();
            // if button is pressed when not tracking alert user and do nothing
        }


    }
    public void startService(){
        this.startService(new Intent(this, com.example.georg.runningtracker.MyService.class));
        this.bindService(new Intent(this, com.example.georg.runningtracker.MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        tracking = true;
        //start and bind service to MainActivity, set tracking global to true
    }
    public void stopService(){
        if(tracking){
            this.stopService(new Intent(this, com.example.georg.runningtracker.MyService.class));
            this.unbindService(serviceConnection);
            tracking = false;
            startTag = false;
        }
        // unbind and stop service from tracking and reset global booleans
    }
    public void StoreData(long elapsedMillis){
        MyLocationListener locationListener = new MyLocationListener();
        double dist = locationListener.getDistanceTravelled(); //get final distance from location listener
        if(dist != 0) {
            double km = dist / 1000; //convert distance to kilometers
            double miles = km / 1.609; // convert kilometers to miles
            double pace = (elapsedMillis / 60000.00) / miles; // divide how many minutes passed by how many miles to find how many minutes per mile
            double time = (double) elapsedMillis; // cast time passed to a double
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date(); //get today's date
            locationListener.resetDistanceTravelled(); //set distance back to 0
            ContentValues newValues = new ContentValues();
            newValues.put(Contract.DISTANCE, round(miles, 2));
            newValues.put(Contract.TIME, round(time / 60000.00, 2)); // divide time in to minutes
            newValues.put(Contract.PACE, round(pace, 2));
            newValues.put(Contract.DATE, dateFormat.format(date));
            newValues.put(Contract.CALORIES, round(totalCalories, 2)); //store accumulated calories for run
            getContentResolver().insert(Contract.STATS_URI, newValues); // insert all stats from the run in to the stats database
            Toast toast = Toast.makeText(this, "Data stored successfully", Toast.LENGTH_SHORT);
            toast.show();
            locationListener = null;
        }
    }
    public void onClickStats(View v){
        Intent intent = new Intent(MainActivity.this, StatsPage.class);
        startActivity(intent); // open stats page
    }
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("g53mdp", "MainActivity onServiceConnected");
            myService = (com.example.georg.runningtracker.MyService.MyBinder) service;
            myService.registerCallback(callback);
            Log.d("g53mdp", "MainActivity onServiceConnected1");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("g53mdp", "MainActivity onServiceDisconnected");
            myService.unregisterCallback(callback);
            myService = null;
        }
    };
    ICallback callback = new ICallback() {
        @Override
        public void progressEvent(final double dist, final Location location, final double calories) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = findViewById(R.id.textView16);
                    TextView te = findViewById(R.id.textView15);
                    TextView tc = findViewById(R.id.textView5);
                    Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);

                    long elapsedMillis = SystemClock.elapsedRealtime() - simpleChronometer.getBase(); // get run time from chronometer
                    double pace = 0;
                    double km = dist/1000;
                    double miles = km/1.609; //convert current distance to miles
                    totalCalories = calories; //accumulated calories
                    if(dist > 0)
                        pace = (elapsedMillis/60000.00)/miles; //work out current pace
                    if(tracking) {
                        tv.setText("Distance (mi): " + Double.toString(round(miles,2)));
                        te.setText("Avg. Pace (min/mi): " + Double.toString(round(pace,2))); // if currently tracking continually update distance, pace and calories textviews
                        tc.setText("Calories: " + Double.toString(round(totalCalories,2)));
                        if(location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                            mMap.moveCamera(cameraUpdate); // continually update camera position to lock on to users current location whilst tracking
                            if(!startTag){
                                MarkerOptions mOptions = new MarkerOptions().position(latLng).title("Started").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                mMap.addMarker(mOptions); // if tracking has just been started place a start tag on map
                                startTag = true;
                            }
                            MarkerOptions mOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.favicon));
                            mMap.addMarker(mOptions); // add small green marker to map to display route taken whilst tracking
                        }
                        if(lowBat){// whilst tracking check if broadcast receiver has set a global variable to show the battery level is low
                            onLowBattery(); //if it has then show alert dialog to prompt to stop tracking
                        }
                    } else {
                        tv.setText("Distance (mi): 0.0");
                        te.setText("Avg. Pace (min/mi): 0.0"); // whilst not tracking set textviews back to 0
                        tc.setText("Calories: 0.0");
                    }

                }
            });
        }
    };
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("g53mdp", "MainActivity onDestroy");
        if(serviceConnection!=null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        } // unbind MainActivity and service
    }
    public void getPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                initMap();
            }else{
                ActivityCompat.requestPermissions(this ,permissions, PERMISSION_REQ_LOCATION);
            }
        }else{
            ActivityCompat.requestPermissions(this ,permissions, PERMISSION_REQ_LOCATION);
        }
        // check if FINE and COARSE location permission has been given and if not ask for it
        // if user declines keep asking
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_REQ_LOCATION:{
                if(grantResults.length >0)
                    for(int i = 0 ; i< grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                    }
                    initMap();//set up map if location permission is granted
                }
            }
        }
    private void initMap(){
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setMyLocationEnabled(true); // initialise google map and display zoom settings and user location
            }
        });
    }
}

