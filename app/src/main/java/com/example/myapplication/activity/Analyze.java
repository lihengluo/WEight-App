package com.example.myapplication.activity;

import android.os.Bundle;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Analyze extends AppCompatActivity {

    private ImageView cameraPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze);

//        cameraPicture = findViewById(R.id.same_picture);
//        cameraPicture.setImageBitmap(Camera.bitmap);

    }
}
