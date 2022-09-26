package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Goods;
import com.example.myapplication.R;
import com.example.myapplication.authservice.PhoneAuth;
import com.example.myapplication.database.CloudDB;
import com.example.myapplication.function.CloudFunction;
import com.example.myapplication.storage.CloudStorage;

import java.io.File;

public class Analyze extends BaseActivity {

    private ImageView cameraPicture;
    PhoneAuth phoneAuth = new PhoneAuth();

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
        String imgpath = myIntend.getStringExtra("imgpath");
        if (!phoneAuth.isUserSignIn()) {
            Toast.makeText(getApplicationContext(), "用户未登录！！！", Toast.LENGTH_SHORT).show();
        } else {
            if (!uploadToCloud(new File(imgpath), new Goods(null, foodname, heats, fat, protein, Carbohydrates, Ca, Fe))) {
                Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean uploadToCloud(File imageFile, Goods good) {
        String[] time = CloudFunction.getFunction().getTime();

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
