package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Goods;
import com.example.myapplication.R;
import com.example.myapplication.authservice.PhoneAuth;
import com.example.myapplication.database.CloudDB;
import com.example.myapplication.function.CloudFunction;
import com.example.myapplication.storage.CloudStorage;

import java.io.File;

public class Analyze extends AppCompatActivity {

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

    private boolean uploadToCloud(File imageFile, Goods good) {
        String[] time = CloudFunction.getFunction().getTime();
        PhoneAuth phoneAuth = new PhoneAuth();
        String uid = phoneAuth.getCurrentUserUid();

        String cloudPath = uid+"/"+time[0]+"/"+time[1]+".jpg";

        CloudStorage storage = CloudStorage.getStorage();
        CloudDB datebase = CloudDB.getDatabase(getApplicationContext());

        if (!storage.uploadUserFile(cloudPath, imageFile)) {
            return false;
        }

        if (!datebase.upsertUserDietRecord(uid, time[0], time[1], good)) {
            // 上传失败时，将云存储上的图片删除
            while (!storage.deleteUserFile(cloudPath));
            return false;
        }
        return true;
    }
}
