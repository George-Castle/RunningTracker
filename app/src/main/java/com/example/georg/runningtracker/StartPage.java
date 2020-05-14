package com.example.georg.runningtracker;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }
    // allows user to select walk run or jog and sets the minTime and minDistance for location updates accordingly
    // then passes these values to MainActivity
    public void onClickWalk(View v){
        Intent intent = new Intent(StartPage.this, MainActivity.class);
        intent.putExtra("minTime", 1000);
        intent.putExtra("minDistance", 1);
        startActivity(intent);
    }
    public void onClickJog(View v){
        Intent intent = new Intent(StartPage.this, MainActivity.class);
        intent.putExtra("minTime", 10);
        intent.putExtra("minDistance", 1);
        startActivity(intent);
    }
    public void onClickRun(View v){
        Intent intent = new Intent(StartPage.this, MainActivity.class);
        intent.putExtra("minTime", 1);
        intent.putExtra("minDistance", 1);
        startActivity(intent);
    }
}
