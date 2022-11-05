package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.authservice.PhoneAuth;
import com.example.myapplication.util.FunctionUtils;

import java.io.EOFException;

public class Register extends BaseActivity {

    private Button mybuttonregister2;
    private EditText myEtuser;
    private EditText myEtpassword;
    private EditText myConfirmEtpassword;
    private EditText myVerifyCode;

    private Button mybuttonhide;
    private Button mybuttonhide2;
    private Button myVerCode;

    private PhoneAuth phoneAuth = new PhoneAuth();

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

        myVerCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    String username = myEtuser.getText().toString();
                    String password = myEtpassword.getText().toString();
                    String confirmpassword = myConfirmEtpassword.getText().toString();

                    if (inputCheck(username, password, confirmpassword)) {
                        phoneAuth.requestVerifyCode(username, getApplicationContext());
                        countDownTime();
                    }
                }
            }
        });

        mybuttonregister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    String username = myEtuser.getText().toString();
                    String password = myEtpassword.getText().toString();
                    String confirmpassword = myConfirmEtpassword.getText().toString();
                    String verifycode = myVerifyCode.getText().toString();

                    if (!inputCheck(username, password, confirmpassword))
                        return;

                    //注册后自动登录成功
                    Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);

                    if (phoneAuth.createUser(username, verifycode, password)) {
                        Toast.makeText(getApplicationContext(), "注册成功，正在返回登录界面", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
                        PhoneAuth phoneAuth = new PhoneAuth();
                        phoneAuth.signOut();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "注册失败，账户已经注册或者密码强度过低", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        mybuttonhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    int type = myEtpassword.getInputType();
                    if (myEtpassword.getInputType() == 129) {
                        mybuttonhide.setBackgroundResource(R.drawable.eye);
                        myEtpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        mybuttonhide.setBackgroundResource(R.drawable.no_eye);
                        myEtpassword.setInputType(129);
                    }
                }
            }
        });

        mybuttonhide2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    int type = myConfirmEtpassword.getInputType();
                    if (myConfirmEtpassword.getInputType() == 129) {
                        mybuttonhide2.setBackgroundResource(R.drawable.eye);
                        myConfirmEtpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        mybuttonhide2.setBackgroundResource(R.drawable.no_eye);
                        myConfirmEtpassword.setInputType(129);
                    }
                }
            }
        });

    }

    /**
     * 检查输入是否符合要求
     * @param username 手机号
     * @param password 密码
     * @param confirmpassword 确认密码
     * @return true表示符合要求
     */
    private boolean inputCheck(String username, String password, String confirmpassword) {
        if (username.length() != 11) {
            Toast.makeText(getApplicationContext(), "手机号位数错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!password.equals(confirmpassword)) {
            Toast.makeText(getApplicationContext(), "前后密码输入不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.length() < 8) {
            Toast.makeText(getApplicationContext(), "密码长度不能小于8", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void countDownTime() {
        //用安卓自带的CountDownTimer实现
        CountDownTimer mTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                myVerCode.setText(millisUntilFinished / 1000 + 1 + "秒后重发");
            }

            @Override
            public void onFinish() {
                myVerCode.setEnabled(true);
                myVerCode.setText("获取验证码");
                cancel();
            }
        };
        mTimer.start();
        myVerCode.setEnabled(false);
    }
}