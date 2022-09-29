package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Goods;
import com.example.myapplication.R;
import com.example.myapplication.authservice.PhoneAuth;
import com.example.myapplication.database.CloudDB;
import com.example.myapplication.function.CloudFunction;
import com.example.myapplication.storage.CloudStorage;
import com.example.myapplication.util.FunctionUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.PrintStream;

public class Analyze extends AppCompatActivity {

    PhoneAuth phoneAuth = new PhoneAuth();

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze);

        ImageView cameraPicture = findViewById(R.id.food_picture);

        TextView foodNameText = findViewById(R.id.foodname);

        TextView heatText = findViewById(R.id.heat);
        TextView fatText = findViewById(R.id.text_fat);
        TextView proteinText = findViewById(R.id.protein);
        TextView carbohydrateText = findViewById(R.id.carbohydrate);
        TextView caText = findViewById(R.id.text_ca);
        TextView feText = findViewById(R.id.text_fe);

        Button uploadDataBtn = findViewById(R.id.upload_to_cloud);
        uploadDataBtn.setEnabled(true);

        // action bar
        final ImageView back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Intent myIntend = getIntent();

        String foodname = myIntend.getStringExtra("foodname");
        foodNameText.setText(foodname);
        float heats = myIntend.getFloatExtra("heats",0);
        heatText.setText(String.format("%.2f", heats));
        float fat = myIntend.getFloatExtra("fat", 0);
        fatText.setText(String.format("%.2f", fat));
        float protein = myIntend.getFloatExtra("protein", 0);
        proteinText.setText(String.format("%.2f", protein));
        float Carbohydrates = myIntend.getFloatExtra("Carbohydrates", 0);
        carbohydrateText.setText(String.format("%.2f", Carbohydrates));
        float Ca = myIntend.getFloatExtra("Ca", 0);
        caText.setText(String.format("%.2f", Ca));
        float Fe = myIntend.getFloatExtra("Fe", 0);
        feText.setText(String.format("%.2f", Fe));

        String imgpath = myIntend.getStringExtra("imgpath");
        cameraPicture.setImageBitmap(BitmapFactory.decodeFile(imgpath));


        uploadDataBtn.setOnClickListener(view -> {
            if (!FunctionUtils.isFastDoubleClick()) {
                if (uploadDataBtn.isEnabled()) {
                    if (!phoneAuth.isUserSignIn()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "您还未登录，请登录后该功能", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        if (!uploadToCloud(new File(imgpath), new Goods(null, foodname, heats, fat, protein, Carbohydrates, Ca, Fe))) {
                            Toast toast = Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "上传已完成", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            uploadDataBtn.setEnabled(false);
                        }
                    }
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "分析结果已上传，请勿重复上传！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
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
