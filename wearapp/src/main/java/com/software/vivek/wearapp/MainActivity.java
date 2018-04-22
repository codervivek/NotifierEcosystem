package com.software.vivek.wearapp;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.software.vivek.wearapp.JsonReader;

import org.json.JSONObject;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private JSONObject json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
        try {
            json = JsonReader.readJsonFromUrl("https://us-central1-notifierecosytem2.cloudfunctions.net/courseList");
        }
        catch (Exception e){
            Log.e("Error","Not able to access url");
            Toast.makeText(this,"Check your internet connection",Toast.LENGTH_SHORT).show();
        }
//        mTextView.append(json.toString());
        Log.e("jjhsdj",json.toString());
    }
}
