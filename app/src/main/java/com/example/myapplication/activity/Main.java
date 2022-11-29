package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.authservice.PhoneAuth;
import com.example.myapplication.util.FunctionUtils;
import com.example.myapplication.R;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.api.AGConnectApi;
import com.huawei.agconnect.auth.AGCAuthException;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;

import java.io.IOException;
import java.io.InputStream;

public class Main extends BaseActivity {

    //声明控件
    private Button mybuttonlogin;
    private Button mybuttonregister;
    private Button myButtonForgetPassword;
    private EditText myEtuser;
    private EditText myEtpassword;
    private Button mybutttonskip;
    private Button mybuttonhide;
    private HuaweiIdAuthButton hwButtonlogin;

    PhoneAuth phoneAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstRun();
    }

    // 初始化Main activity
    private void init() {
        phoneAuth = new PhoneAuth();
        //找到控件
        mybuttonlogin = findViewById(R.id.btn_login);
        myEtuser = findViewById(R.id.et_1);
        myEtpassword = findViewById(R.id.et_2);
        mybutttonskip = findViewById(R.id.btn_skip);
        mybuttonhide = findViewById(R.id.btn_hide);
        mybuttonregister = findViewById(R.id.btn_reg);
        myButtonForgetPassword = findViewById(R.id.forget);
        hwButtonlogin = findViewById(R.id.btn_hw_login);

        //实现跳转---方法1
        mybuttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    String username = myEtuser.getText().toString();
                    String password = myEtpassword.getText().toString();

                    //弹出内容设置
                    String ok = "登录成功!";
                    String fail = "密码或者账号有误，请重新登录！";

                    Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);

                    if (phoneAuth.signInWithPassword(username, password)) {
                        intent.putExtra("登录信息", "0");
                        startActivity(intent);
                        finish();
                    } else {
                        if (phoneAuth.signInWithPassword(username, password)) { // AGC认证服务第一次登录会因超时失败，需要再次尝试
                            intent.putExtra("登录信息", "0");
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast toastcenter = Toast.makeText(getApplicationContext(), fail, Toast.LENGTH_SHORT);
                            toastcenter.setGravity(Gravity.CENTER, 0, 0);
                            toastcenter.show();
                        }
                    }
                }
            }
        });

        mybuttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    Intent intent2 = new Intent(getApplicationContext(), Register.class);
                    startActivity(intent2);
//                    finish();
                }
            }
        });

        myButtonForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    Intent intent2 = new Intent(getApplicationContext(), ResetPassword.class);
                    startActivity(intent2);
//                    finish();
                }
            }
        });

        mybutttonskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);
                    intent.putExtra("登录信息", "2");
                    Toast.makeText(getApplicationContext(), "已跳过登录阶段", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
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

        hwButtonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hwButtonlogin.setClickable(false);
                Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);
                AGConnectAuth.getInstance().signIn(Main.this, AGConnectAuthCredential.HMS_Provider).addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                            @Override
                            public void onSuccess(SignInResult signInResult) {
                                intent.putExtra("登录信息", "0");
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                hwButtonlogin.setClickable(true);
                                if (e instanceof AGCAuthException) {
                                    AGCAuthException agcAuthException = (AGCAuthException) e;
                                    int errCode = agcAuthException.getCode();
                                    String message = agcAuthException.getMessage();
                                    Log.e("HUAWEI signin fail", "errorCode: " + errCode + ", message: " + message);
                                }

                                Toast toastcenter = Toast.makeText(getApplicationContext(), "认证失败请重试", Toast.LENGTH_SHORT);
                                toastcenter.setGravity(Gravity.CENTER, 0, 0);
                                toastcenter.show();
                            }
                        });
            }
        });

        try {
            AGConnectOptionsBuilder builder = new AGConnectOptionsBuilder();
            InputStream in = getAssets().open("agconnect-services.json");    //如果使用了AGC插件，删除此行
            builder.setInputStream(in);
            builder.setClientId("1013786228048285184");
            builder.setClientSecret("33CC0F811187DE6F94331A237E7BBAE6F82C726ED66B9661EA999FB3CB607A04");
            builder.setApiKey("DAEDAN5u969uSRbCvWNMv8mIhLdm3yg4U7q1E8G1fnXv0rExU2oFzi0OnEkZwlISL9eGFWqbz2r78/UUpeOeyRRKAwu9kWBlH+bR0w==");
            builder.setCPId("70086000030669687");
            builder.setProductId("99536292102712188");
            builder.setAppId("107299899");
            AGConnectInstance.initialize(this, builder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 发现已经有用户登录时立即跳转
        if (phoneAuth.isUserSignIn()) {
            Toast.makeText(getApplicationContext(), "您已登录，正在跳转...", Toast.LENGTH_SHORT).show();
            try {
                Thread.currentThread().sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), Bottom_bar.class);
            intent.putExtra("登录信息", "1");
            startActivity(intent);
            finish();
        }
    }

    //传递跳转信息 0代表登陆后跳转，1代表已登录直接跳转，2代表跳过登录界面的跳转；

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AGConnectApi.getInstance().activityLifecycle().onActivityResult(requestCode, resultCode, data);
    }

    private void firstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("First", true);
        if (first_run) {
            PrivateDialog.getInstace().message("").sure("同意并继续").cancle("退出应用")
                    .setOnTipItemClickListener(new PrivateDialog.OnTipItemClickListener() {
                        @Override
                        public void cancleClick() {
                            finishAffinity();
                        }

                        @Override
                        public void sureClick() {
                            sharedPreferences.edit().putBoolean("First", false).commit();
                            init();
                        }

                        @Override
                        public void termsClick() {
                            final Uri uri= Uri.parse("http://weight.hb.cn/");
                            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(intent);
                            //跳转至隐私政策
//                        startActivity((new Intent(MainActivity.this, TermActivity.class)));
                        }
                    }).create(this);
        }
        else {
            init();
        }
    }
}