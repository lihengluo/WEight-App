package com.example.myapplication.authservice;

import android.content.Context;
import android.util.Log;

import com.huawei.agconnect.auth.AGCAuthException;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.Task;

import java.util.Locale;

public abstract class Authentication {
    protected String account;
    protected String password;
    protected String verifyCode;

    public Authentication() {}

    /**
     * 判断是否已有用户登录
     * @return true表示已有用户登录
     */
    public boolean isUserSignIn() {
        return AGConnectAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * 登出当前用户
     */
    public void signOut() {
        AGConnectAuth.getInstance().signOut();
    }

    /**
     * 获取当前登录用户的UID
     * @return UID
     */
    public String getCurrentUserUid() {
        AGConnectUser connectUser = AGConnectAuth.getInstance().getCurrentUser();
        if (connectUser == null) {
            Log.w("Authentication getCurrentUserUid", "NO User Sign in");
            return null;
        }
        return connectUser.getUid();
    }

    /**
     * 请求验证码
     * @param accountStr 用户账号（手机号或者邮箱）
     * @return 验证码是否请求成功
     */
    public void requestVerifyCode(String accountStr, Context context) {
        VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.CHINA)
                .build();
        agcRequestVerifyCode(accountStr, context, settings);
    }

    public boolean signInWithPassword(String accountStr, String password) {
        AGConnectAuthCredential credential = credentialWithPassword(accountStr, password);

        this.account = accountStr;
        this.password = password;

        Task<SignInResult> signInTask = AGConnectAuth.getInstance().signIn(credential);
        while (!signInTask.isComplete());

        if (!signInTask.isSuccessful()) {
            Exception e = signInTask.getException();
            if (e instanceof AGCAuthException) {
                AGCAuthException agcAuthException = (AGCAuthException) e;
                int errCode = agcAuthException.getCode();
                String message = agcAuthException.getMessage();
                Log.w("Authentication signInWithPassword", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return signInTask.isSuccessful();
    }

    public boolean signInWithVerifyCode(String accountStr, String verifyCode) {
        AGConnectAuthCredential credential = credentialWithVerifyCode(accountStr, verifyCode);

        this.account = accountStr;
        this.verifyCode = verifyCode;

        Task<SignInResult> signInTask = AGConnectAuth.getInstance().signIn(credential);
        while (!signInTask.isComplete());

        if (!signInTask.isSuccessful()) {
            Exception e = signInTask.getException();
            if (e instanceof AGCAuthException) {
                AGCAuthException agcAuthException = (AGCAuthException) e;
                int errCode = agcAuthException.getCode();
                String message = agcAuthException.getMessage();
                Log.w("Authentication signInWithPassword", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return signInTask.isSuccessful();
    }

    /**
     * 注销用户
     * @return 是否成功
     */
    public boolean deleteUser() {
        // 敏感操作需要首先进行重认证
        if (reAuthenticate() == false)
            return false;

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

    protected boolean reAuthenticate() {
        AGConnectUser user =AGConnectAuth.getInstance().getCurrentUser();
        if (user == null)
            return false;

        AGConnectAuthCredential credential = null;
        if (password != null) {
            credential = credentialWithPassword(account, password);
        }
        else if (verifyCode != null) {
            credential = credentialWithVerifyCode(account, verifyCode);
        }
        Task<SignInResult> signInTask = user.reauthenticate(credential);

        while (!signInTask.isComplete());

        if (!signInTask.isSuccessful()) {
            Exception e = signInTask.getException();
            if (e instanceof AGCAuthException) {
                AGCAuthException agcAuthException = (AGCAuthException) e;
                int errCode = agcAuthException.getCode();
                String message = agcAuthException.getMessage();
                Log.w("Authentication reAuthenticate", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return signInTask.isSuccessful();
    }

    protected abstract AGConnectAuthCredential credentialWithPassword(String accountStr, String passwordStr);
    protected abstract AGConnectAuthCredential credentialWithVerifyCode(String accountStr, String verifyCodeStr);

    protected abstract void agcRequestVerifyCode(String accountStr, Context context, VerifyCodeSettings settings);

    public abstract boolean createUser(String accountStr, String verifyCOdeStr);

    public abstract boolean createUser(String accountStr, String verifyCOdeStr, String passwordStr);


}
