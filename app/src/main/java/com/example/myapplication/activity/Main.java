package com.example.myapplication.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.R;

public class Main extends AppCompatActivity {

    //声明控件
    private Button mybuttonlogin;
    private EditText myEtuser;
    private EditText myEtpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找到控件
        mybuttonlogin = findViewById(R.id.btn_login);
        myEtuser = findViewById(R.id.et_1);
        myEtpassword = findViewById(R.id.et_2);

        //实现跳转---方法1
        mybuttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = myEtuser.getText().toString();
                String password = myEtpassword.getText().toString();

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


                    startActivity(intent);
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

        //匹配对应用户名和密码进行登录操作

    }

}