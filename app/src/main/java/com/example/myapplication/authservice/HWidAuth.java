package com.example.myapplication.authservice;

import android.app.Activity;
import android.util.Log;

import com.example.myapplication.activity.Main;
import com.huawei.agconnect.auth.AGCAuthException;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.TokenResult;
import com.huawei.hmf.tasks.Task;

public class HWidAuth {
    public HWidAuth() {}

    public boolean deleteUser() {
        Task<Void> deleteUserTask = AGConnectAuth.getInstance().deleteUser();
        while (!deleteUserTask.isComplete());

        if (!deleteUserTask.isSuccessful()) {
            Exception e = deleteUserTask.getException();
            if (e instanceof AGCAuthException) {
                AGCAuthException agcAuthException = (AGCAuthException) e;
                int errCode = agcAuthException.getCode();
                String message = agcAuthException.getMessage();
                Log.w("Authentication deleteUser", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return deleteUserTask.isSuccessful();
    }
}
