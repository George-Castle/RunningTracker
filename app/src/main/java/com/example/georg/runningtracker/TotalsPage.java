package com.example.georg.runningtracker;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class TotalsPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totals);
        ContentResolver cr = getContentResolver();
        String columns[] = new String[]
                {
                        "Total"
                };
        allTime(columns, cr); // gets all time distance travelled and calories burnt and sets the text view
        today(columns, cr); // gets today's distance travelled and calories burnt and sets the text view
        month(columns, cr); // gets this month's distance travelled and calories burnt and sets the text view
        year(columns, cr); // gets this year's distance travelled and calories burnt and sets the text view

    }
    public void today(String[] columns, ContentResolver cr){
        Uri distUri = Uri.parse(Contract.STATS_URI + "/" + "tT");
        Cursor distCursor = cr.query(distUri,
                columns,
                null,
                null,
                null);
        Uri calsUri = Uri.parse(Contract.STATS_URI + "/" + "tC");
        Cursor calsCursor = cr.query(calsUri,
                columns,
                null,
                null,
                null);
        TextView dist = findViewById(R.id.textView11);
        distCursor.moveToFirst();
        calsCursor.moveToFirst();
        int bDistance = distCursor.getInt(0);
        int bCals = calsCursor.getInt(0);
        if(bDistance == 0)
            dist.setText("Today: <1 mile " + Integer.toString(bCals) + " cals");
        else
            dist.setText("Today: " + Integer.toString(bDistance) + " miles " + Integer.toString(bCals) + " cals");
        distCursor.close();
        calsCursor.close();

    }
    public void month(String[] columns, ContentResolver cr){
        Uri distUri = Uri.parse(Contract.STATS_URI + "/" + "mT");
        Cursor distCursor = cr.query(distUri,
                columns,
                null,
                null,
                null);
        Uri calsUri = Uri.parse(Contract.STATS_URI + "/" + "mC");
        Cursor calsCursor = cr.query(calsUri,
                columns,
                null,
                null,
                null);
        TextView dist = findViewById(R.id.textView8);
        distCursor.moveToFirst();
        calsCursor.moveToFirst();
        int bDistance = distCursor.getInt(0);
        int bCals = calsCursor.getInt(0);
        if(bDistance == 0)
            dist.setText("Month: <1 mile " + Integer.toString(bCals) + " cals");
        else
            dist.setText("Month: " + Integer.toString(bDistance) + " miles " + Integer.toString(bCals) + " cals");
        distCursor.close();
        calsCursor.close();
    }
    public void year(String[] columns, ContentResolver cr){
        Uri distUri = Uri.parse(Contract.STATS_URI + "/" + "yT");
        Cursor distCursor = cr.query(distUri,
                columns,
                null,
                null,
                null);
        Uri calsUri = Uri.parse(Contract.STATS_URI + "/" + "yC");
        Cursor calsCursor = cr.query(calsUri,
                columns,
                null,
                null,
                null);
        TextView dist = findViewById(R.id.textView9);
        distCursor.moveToFirst();
        calsCursor.moveToFirst();
        int bDistance = distCursor.getInt(0);
        int bCals = calsCursor.getInt(0);
        if(bDistance == 0)
            dist.setText("Year: <1 mile " + Integer.toString(bCals) + " cals");
        else
            dist.setText("Year: " + Integer.toString(bDistance) + " miles " + Integer.toString(bCals) + " cals");
        distCursor.close();
        calsCursor.close();
    }
        public void allTime(String[] columns, ContentResolver cr){
            Uri distUri = Uri.parse(Contract.STATS_URI + "/" + "aT");
            Cursor distCursor = cr.query(distUri,
                    columns,
                    null,
                    null,
                    null);
            Uri calsUri = Uri.parse(Contract.STATS_URI + "/" + "aC");
            Cursor calsCursor = cr.query(calsUri,
                    columns,
                    null,
                    null,
                    null);
            TextView dist = findViewById(R.id.textView10);
            distCursor.moveToFirst();
            calsCursor.moveToFirst();
            int bDistance = distCursor.getInt(0);
            int bCals = calsCursor.getInt(0);
            if(bDistance == 0)
                dist.setText("All time: <1 mile " + Integer.toString(bCals) + " cals");
            else
                dist.setText("All time: " + Integer.toString(bDistance) + " miles " + Integer.toString(bCals) + " cals");
            distCursor.close();
            calsCursor.close();
        }
}


