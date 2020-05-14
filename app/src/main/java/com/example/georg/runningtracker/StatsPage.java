package com.example.georg.runningtracker;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class StatsPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_page);
        final ListView lv = (ListView) findViewById(R.id.ListView);
        initListView(lv);
        // displays all database entries in a list view allowing user to browse previous runs
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {

                new AlertDialog.Builder(StatsPage.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("DELETE")
                        .setMessage("Would you like to delete this entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri stat = Uri.parse(Contract.STATS_URI+"/"+Long.toString(id));
                                int c = getContentResolver().delete(stat, null, null);
                                if(c == 1) {
                                    Intent intent = new Intent(StatsPage.this, StatsPage.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }// delete selected entries from database

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }
    public void initListView(ListView lv){
        String columns[] = new String[]
                {
                        Contract._ID,
                        Contract.DISTANCE,
                        Contract.TIME,
                        Contract.PACE,
                        Contract.DATE,
                        Contract.CALORIES
                };

        String colsToDisplay [] = new String[]
                {
                        Contract.DISTANCE,
                        Contract.CALORIES,
                        Contract.PACE
                };

        int[] colResIds = new int[]
                {
                        R.id.value,
                        R.id.value1,
                        R.id.value2
                };

        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(Contract.STATS_URI,
                columns,
                null,
                null,
                null);


        lv.setAdapter(new SimpleCursorAdapter(this,
                R.layout.item_layout,
                c, colsToDisplay,
                colResIds, 0));

    }
    public void onClickTotals(View v){

        Intent intent = new Intent(StatsPage.this, TotalsPage.class);
        startActivity(intent);

    }
    public void onClickBests(View v){
        Intent intent = new Intent(StatsPage.this, BestsPage.class);
        startActivity(intent);

    }
}
