package com.example.georg.runningtracker;

import android.net.Uri;

public class Contract {

    public static final String AUTHORITY = "com.example.georg.runningtracker.RTProvider";
    public static final Uri STATS_URI = Uri.parse("content://"+AUTHORITY+"/stats");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");

    public static final String _ID = "_id";
    public static final String DISTANCE = "distance";
    public static final String TIME = "time";
    public static final String PACE = "pace";
    public static final String DATE = "date";
    public static final String CALORIES = "calories";


    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/RTProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/RTProvider.data.text";
}

