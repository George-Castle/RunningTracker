package com.example.georg.runningtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        Log.d("g53mdo", "DBHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE stats (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "distance DOUBLE NOT NULL," +
                "time DOUBLE NOT NULL," +
                "pace DOUBLE NOT NULL," +
                "date VARCHAR(128) NOT NULL," +
                "calories DOUBLE NOT NULL" +
                ");");
        // create stats table for storing run data
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS stats");
        onCreate(db);
    }


}
