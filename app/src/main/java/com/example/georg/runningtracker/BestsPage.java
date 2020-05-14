package com.example.georg.runningtracker;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.net.URI;

public class BestsPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bests);
        ContentResolver cr = getContentResolver();
        String columns[] = new String[]
                {
                        Contract._ID,
                        Contract.DISTANCE,
                        Contract.TIME,
                        Contract.PACE,
                        Contract.DATE,
                        Contract.CALORIES
                };
        setBestDistance(columns, cr);
        setBestPace(columns, cr);
        setBestCalories(columns, cr);

    }
    public void setBestDistance(String[] columns, ContentResolver cr){
        Uri distUri = Uri.parse(Contract.STATS_URI + "/" + "D"); //use specific URI through content provider to access best distance
        Cursor distCursor = cr.query(distUri,
                columns,
                null,
                null,
                null);
        TextView dist = findViewById(R.id.textView7);
        distCursor.moveToFirst();
        double bDistance = distCursor.getDouble(0);
        dist.setText("Longest distance: " + Double.toString(bDistance) + " miles"); // update textview with best distance stored in database
        distCursor.close();
    }
    public void setBestPace(String[] columns, ContentResolver cr){
        Uri paceUri = Uri.parse(Contract.STATS_URI + "/" + "P");//use specific URI through content provider to access best pace
        Cursor paceCursor = cr.query(paceUri,
                columns,
                null,
                null,
                null);
        TextView pace = findViewById(R.id.textView12);
        paceCursor.moveToFirst();
        double bPace = paceCursor.getDouble(0);
        pace.setText("Best pace: " + Double.toString(bPace) + " min/mile"); // update textview with best pace stored in database
        paceCursor.close();
    }
    public void setBestCalories(String[] columns, ContentResolver cr){
        Uri calsUri = Uri.parse(Contract.STATS_URI + "/" + "C");//use specific URI through content provider to access best calories
        Cursor calsCursor = cr.query(calsUri,
                columns,
                null,
                null,
                null);
        TextView cals = findViewById(R.id.textView13);
        calsCursor.moveToFirst();
        double bCals = calsCursor.getDouble(0);
        cals.setText("Most Calories: " + Double.toString(bCals)); // update textview with best calories stored in database
        calsCursor.close();
    }
}
