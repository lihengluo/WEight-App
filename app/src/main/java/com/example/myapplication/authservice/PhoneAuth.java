package com.example.myapplication.authservice;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.activity.Main;
import com.example.myapplication.util.ToastUtil;
import com.huawei.agconnect.auth.AGCAuthException;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.PhoneAuthProvider;
import com.huawei.agconnect.auth.PhoneUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.agconnect.exception.AGCException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

public class PhoneAuth extends Authentication{

    public PhoneAuth(){
        super();
    }

    @Override
    protected AGConnectAuthCredential credentialWithPassword(String accountStr, String passwordStr) {
        return PhoneAuthProvider.credentialWithPassword("86", accountStr, passwordStr);
    }

    @Override
    protected AGConnectAuthCredential credentialWithVerifyCode(String accountStr, String verifyCodeStr) {
        return PhoneAuthProvider.credentialWithVerifyCode("86", accountStr, null, verifyCodeStr);
    }

    @Override
    protected void agcRequestVerifyCode(String accountStr, Context context, VerifyCodeSettings settings) {
        Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode("86", accountStr, settings);
//        while (!task.isComplete());
//
//        if (!task.isSuccessful()) {
//            Exception e = task.getException();
//            if (e instanceof AGCAuthException) {
//                AGCAuthException agcAuthException = (AGCAuthException) e;
//                int errCode = agcAuthException.getCode();
//                String message = agcAuthException.getMessage();
//                Log.e("PhoneAuth agcRequestVerifyCode", "errorCode: " + errCode + ", message: " + message);
//            }
//        }

        task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
            @Override
            public void onSuccess(VerifyCodeResult verifyCodeResult) {
                //验证码申请成功
                Toast.makeText(context, "验证码发送成功", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "验证码发送失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean createUser(String accountStr, String verifyCOdeStr) {

        PhoneUser phoneUser = new PhoneUser.Builder()
                .setCountryCode("86")
                .setPhoneNumber(accountStr)
                .setVerifyCode(verifyCOdeStr)
                .build();
        Task<SignInResult> signInTask = AGConnectAuth.getInstance().createUser(phoneUser);
        while (!signInTask.isComplete());

        if (!signInTask.isSuccessful()) {
            Exception e = signInTask.getException();
            if (e instanceof AGCAuthException) {
                AGCAuthException agcAuthException = (AGCAuthException) e;
                int errCode = agcAuthException.getCode();
                String message = agcAuthException.getMessage();
                Log.e("PhoneAuth createUser", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return signInTask.isSuccessful();
    }

    @Override
    public boolean createUser(String accountStr, String verifyCOdeStr, String passwordStr) {

        PhoneUser phoneUser = new PhoneUser.Builder()
                .setCountryCode("86")
                .setPhoneNumber(accountStr)
                .setVerifyCode(verifyCOdeStr)
                .setPassword(passwordStr)
                .build();
        Task<SignInResult> signInTask = AGConnectAuth.getInstance().createUser(phoneUser);
        while (!signInTask.isComplete());

        if (!signInTask.isSuccessful()) {
            Exception e = signInTask.getException();
            if (e instanceof AGCAuthException) {
                AGCAuthException agcAuthException = (AGCAuthException) e;
                int errCode = agcAuthException.getCode();
                String message = agcAuthException.getMessage();
                Log.e("PhoneAuth createUser", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return signInTask.isSuccessful();
    }
}
