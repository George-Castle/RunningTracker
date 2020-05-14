package com.example.georg.runningtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("g53mdp", "MyBroadcastReceiver onReceive");

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int charging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if(level < 16 && charging != BatteryManager.BATTERY_PLUGGED_AC  && charging != BatteryManager.BATTERY_PLUGGED_USB  && charging != BatteryManager.BATTERY_PLUGGED_WIRELESS) {
            Toast.makeText(context, "BATTERY LOW STILL TRACKING", Toast.LENGTH_LONG).show();
            MainActivity.lowBat = true; // if broadcast receiver detects low battery change MainActivity global boolean lowBat
            //battery must be below 16% and not charging
        }
    }
}
