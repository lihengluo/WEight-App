package com.example.myapplication.activity;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.upload.UploadEngine;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.R;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class Main extends AppCompatActivity {

    //声明控件
    private Button mybuttonlogin;
    private Button mybuttonregister;
    private EditText myEtuser;
    private EditText myEtpassword;
    private Button mybutttonskip;
    private Button mybuttonhide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找到控件
        mybuttonlogin = findViewById(R.id.btn_login);
        myEtuser = findViewById(R.id.et_1);
        myEtpassword = findViewById(R.id.et_2);
        mybutttonskip = findViewById(R.id.btn_skip);
        mybuttonhide = findViewById(R.id.btn_hide);

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
//            }
//        }
//        UploadEngine uploadEngine = new UploadEngine(Main.this.getApplicationContext());
        //实现跳转---方法1
        mybuttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = myEtuser.getText().toString();
                String password = myEtpassword.getText().toString();

//                uploadEngine.uploadToDetect(Environment.getExternalStorageDirectory() + "/Pictures/3.jpg", 24, 0.32, 0.6);
                //弹出内容设置
                String ok = "登录成功!";
                String fail = "密码或者账号有误，请重新登录！";

                Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);
                //假设正确的账号和密码分别为llh,123456
                if(username.equals("llh") && password.equals("123456")){ //如果正确的话进行跳转
                    //toast普通版
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_SHORT).show();

                    //封装好的类
                    ToastUtil.showMessage(Main.this, ok);

                }
                else{ //弹出登录失败toast
                    //toast提升版 居中显示
                    Toast toastcenter = Toast.makeText(getApplicationContext(), fail, Toast.LENGTH_SHORT);
                    toastcenter.setGravity(Gravity.CENTER, 0, 0);
                    toastcenter.show();

                    ToastUtil.showMessage(Main.this, fail);
                }
            }
        });

        mybutttonskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);
                Toast toastcenter = Toast.makeText(getApplicationContext(), "已跳过登录阶段", Toast.LENGTH_SHORT);
                startActivity(intent);
            }
        });

        mybuttonhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = myEtpassword.getInputType();
                if (myEtpassword.getInputType() == 129) {
                    mybuttonhide.setBackgroundResource(R.drawable.eye);
                    myEtpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    mybuttonhide.setBackgroundResource(R.drawable.no_eye);
                    myEtpassword.setInputType(129);
                }
            }
        });

        try {
            AGConnectOptionsBuilder builder = new AGConnectOptionsBuilder();
            InputStream in = getAssets().open("agconnect-services.json");    //如果使用了AGC插件，删除此行
            builder.setInputStream(in);
            builder.setClientId("981817313709805184");
            builder.setClientSecret("216C89C06BD713DC0F94A440D6ACDB4052B12B4468331D675F412EFFD60270DC");
            builder.setApiKey("DAEDAOdUgkjCnRD4/xfDwBnv3MOJzw6aUT0CQ0VxUgfSPe99ZDD0lmvHfffuffK4uHp4bTYpQeXRdWDm2EXsH7I2G/O/0EuFy5Tzcw==");
            builder.setCPId("420086000304642213");
            builder.setProductId("99536292102615511");
            builder.setAppId("107062115");
            AGConnectInstance.initialize(this, builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}