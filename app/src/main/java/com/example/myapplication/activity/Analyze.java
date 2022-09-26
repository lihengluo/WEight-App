package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Analyze extends BaseActivity {

    private ImageView cameraPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze);

        cameraPicture = findViewById(R.id.same_picture);
        cameraPicture.setImageBitmap(Camera.bitmap);

        final Intent myIntend = getIntent();
        String foodname = myIntend.getStringExtra("foodname");
        float heats = myIntend.getFloatExtra("heats",0);
        float fat = myIntend.getFloatExtra("fat", 0);
        float protein = myIntend.getFloatExtra("protein", 0);
        float Carbohydrates = myIntend.getFloatExtra("Carbohydrates", 0);
        float Ca = myIntend.getFloatExtra("Ca", 0);
        float Fe = myIntend.getFloatExtra("Fe", 0);
        Log.v("tag", "---"+String.valueOf(heats));
    }
}
