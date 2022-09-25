package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.authservice.PhoneAuth;

import java.io.EOFException;

public class Register extends AppCompatActivity {

    private Button mybuttonregister2;
    private EditText myEtuser;
    private EditText myEtpassword;
    private EditText myConfirmEtpassword;
    private EditText myVerifyCode;

    private Button mybuttonhide;
    private Button mybuttonhide2;
    private Button myVerCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myEtuser = findViewById(R.id.et_1);
        myEtpassword = findViewById(R.id.et_2);
        myConfirmEtpassword = findViewById(R.id.et_3);
        myVerifyCode = findViewById(R.id.et_4);

        mybuttonhide = findViewById(R.id.btn_hide);
        mybuttonhide2 = findViewById(R.id.btn_hide2);
        mybuttonregister2 = findViewById(R.id.btn_register);
        myVerCode = findViewById(R.id.get_vercode);

        mybuttonregister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = myEtuser.getText().toString();
                String password = myEtpassword.getText().toString();
                String confirmpassword = myConfirmEtpassword.getText().toString();
                String verifycode = myVerifyCode.getText().toString();


                //注册后自动登录成功
                Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);
                PhoneAuth phoneAuth = new PhoneAuth();

                myVerCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(password.equals(confirmpassword)){
                            phoneAuth.requestVerifyCode(username, getApplicationContext());
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "前后密码输入不一致", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if(phoneAuth.createUser(username, verifycode, password)){
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                }

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

        mybuttonhide2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = myConfirmEtpassword.getInputType();
                if (myConfirmEtpassword.getInputType() == 129) {
                    mybuttonhide2.setBackgroundResource(R.drawable.eye);
                    myConfirmEtpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    mybuttonhide2.setBackgroundResource(R.drawable.no_eye);
                    myConfirmEtpassword.setInputType(129);
                }
            }
        });

    }
}