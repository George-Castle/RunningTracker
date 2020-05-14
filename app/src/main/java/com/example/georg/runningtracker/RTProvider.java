package com.example.georg.runningtracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RTProvider extends ContentProvider {
    private DBHelper dbHelper = null;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contract.AUTHORITY, "stats", 1);
        uriMatcher.addURI(Contract.AUTHORITY, "stats/#", 2);
        uriMatcher.addURI(Contract.AUTHORITY, "stats", 5);
        uriMatcher.addURI(Contract.AUTHORITY, "stats/D", 6); // URI for best distance
        uriMatcher.addURI(Contract.AUTHORITY,"*", 7);
        uriMatcher.addURI(Contract.AUTHORITY,"stats/P", 8); // URI for best pace
        uriMatcher.addURI(Contract.AUTHORITY,"stats/aT", 9); // URI for all time Total distance
        uriMatcher.addURI(Contract.AUTHORITY,"stats/tT", 10); // URI for today's Total distance
        uriMatcher.addURI(Contract.AUTHORITY,"stats/mT", 11); // URI for this month's Total distance
        uriMatcher.addURI(Contract.AUTHORITY,"stats/yT", 12); // URI for this year's Total distance
        uriMatcher.addURI(Contract.AUTHORITY,"stats/C", 13); // URI for best calories
        uriMatcher.addURI(Contract.AUTHORITY,"stats/tC", 14); // URI for today's Total calories
        uriMatcher.addURI(Contract.AUTHORITY,"stats/mC", 15); // URI for this month's Total calories
        uriMatcher.addURI(Contract.AUTHORITY,"stats/yC", 16); // URI for this year's Total calories
        uriMatcher.addURI(Contract.AUTHORITY,"stats/aC", 17); // URI for all time Total calories
    }

    @Override
    public boolean onCreate(){
        Log.d("g53mdo", "CP create");
        this.dbHelper = new DBHelper(this.getContext(), "stats", null, 7);
        return true;
    }
    @Override
    public String getType(Uri uri) {
        if (uri.getLastPathSegment()==null)
        {
            return "vnd.android.cursor.dir/RTProvider.data.text";
        }
        else
        {
            return "vnd.android.cursor.item/RTProvider.data.text";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri)) {
            case 1:
                tableName = "stats";
                break;
            case 3:
                tableName = "location";
                break;
            default:
                tableName = "stats";
                break;
        }

        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(nu, null);
        return nu;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Date date = new Date();
        switch(uriMatcher.match(uri)) {
            case 2:
                selection = "DATE = " + uri.getLastPathSegment();
            case 1:
                return db.query("stats", projection, selection, selectionArgs, null, null, sortOrder);
            case 5:
                String q5 = "SELECT _id, distance, time, pace, date, calories FROM stats";
                return db.rawQuery(q5, selectionArgs);
            case 6:
                String q6 = "SELECT MAX(distance) FROM stats"; // gets biggest number in distance column
                return db.rawQuery(q6, selectionArgs);
            case 7:
                String q7 = "SELECT * FROM stats";
                return db.rawQuery(q7, selectionArgs);
            case 8:
                String q8 = "SELECT MIN(NULLIF(pace,0)) FROM stats"; // selects smallest number from the pace column
                return db.rawQuery(q8, selectionArgs);
            case 9:
                String q9 = "SELECT Sum(distance) AS Total FROM stats"; // sums all distances in the table and returns as one cell called Total
                return db.rawQuery(q9, selectionArgs);
            case 10:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String tDate = dateFormat.format(date);
                String q10 = "SELECT Sum(distance) AS Total FROM stats WHERE date LIKE \'%" + tDate + "%\'";
                // gets today's full date and uses string matching to sum the distance of all records added today, returned as Total
                return db.rawQuery(q10, selectionArgs);
            case 11:
                SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                String mDate = monthFormat.format(date);
                String q11 = "SELECT Sum(distance) AS Total FROM stats WHERE date LIKE \'" + mDate + "%\'";
                // gets this month's date and uses string matching to sum the distance of all records added this month, returned as Total
                return db.rawQuery(q11, selectionArgs);
            case 12:
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                String yDate = yearFormat.format(date);
                String q12 = "SELECT Sum(distance) AS Total FROM stats WHERE date LIKE \'" + yDate + "%\'";
                // gets the year and uses string matching to sum the distance of all records added this year, returned as Total
                return db.rawQuery(q12, selectionArgs);
            case 13:
                String q13 = "SELECT MAX(calories) FROM stats"; // gets biggest number in calories column
                return db.rawQuery(q13, selectionArgs);
            case 14:
                SimpleDateFormat dateCFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String tcDate = dateCFormat.format(date);
                String q14 = "SELECT Sum(calories) AS Total FROM stats WHERE date LIKE \'%" + tcDate + "%\'";
                // gets today's full date and uses string matching to sum the calories of all records added today, returned as Total
                return db.rawQuery(q14, selectionArgs);
            case 15:
                SimpleDateFormat monthCFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                String mcDate = monthCFormat.format(date);
                String q15 = "SELECT Sum(calories) AS Total FROM stats WHERE date LIKE \'" + mcDate + "%\'";
                // gets this month's date and uses string matching to sum the calories of all records added this month, returned as Total
                return db.rawQuery(q15, selectionArgs);
            case 16:
                SimpleDateFormat yearCFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                String ycDate = yearCFormat.format(date);
                String q16 = "SELECT Sum(calories) AS Total FROM stats WHERE date LIKE \'" + ycDate + "%\'";
                // gets the year and uses string matching to sum the calories of all records added this year, returned as Total
                return db.rawQuery(q16, selectionArgs);
            case 17:
                String q17 = "SELECT Sum(calories) AS Total FROM stats"; // sums all calories in the table and returns as one cell called Total
                return db.rawQuery(q17, selectionArgs);
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = Contract._ID + "='" + uri.getLastPathSegment() + "'";
        db.update("stats", values, where, null  );
        return 1;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = Contract._ID + "='" + uri.getLastPathSegment() + "'"; //deletes table entries using their ID
        db.delete("stats", where, null);
        return 1;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        throw new UnsupportedOperationException("not implemented");
    }
}
