package com.example.myapplication.activity;

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
import com.huawei.hms.framework.common.StringUtils;
import com.huawei.hms.utils.StringUtil;

public class ResetPassword extends BaseActivity{
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
        setContentView(R.layout.activity_resetpassword);

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
                        phoneAuth.requestVerifyCodeForReset(username, getApplicationContext());
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

                    //重置成功后返回登录界面
                    Intent intent = new Intent(getApplicationContext(), Main.class);

                    if (phoneAuth.resetPassword(username, password, verifycode)) {
                        Toast.makeText(getApplicationContext(), "重置成功，请登录", Toast.LENGTH_SHORT).show();
                        // startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "重置失败，请确保账户已注册且新旧密码不相同", Toast.LENGTH_SHORT).show();
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
        else if(password.equals(username)) {
            Toast.makeText(getApplicationContext(), "密码与手机号不能相同", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            int typeNum = 0;    // 统计包含的字符类型数目
            if (password.matches(".*\\d+.*")) typeNum++;
            if (password.matches(".*[A-Z]+.*")) typeNum++;
            if (password.matches(".*[a-z]+.*")) typeNum++;
            if (password.matches(".*[^\\da-zA-Z]+.*")) typeNum++;

            if (typeNum < 2) {
                Toast.makeText(getApplicationContext(), "密码强度不足，需至少需要包含2种字符类型（大写字母、小写字母、数字、符号）", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private void countDownTime() {
        //用安卓自带的CountDownTimer实现
        CountDownTimer mTimer = new CountDownTimer(30 * 1000, 1000) {
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
