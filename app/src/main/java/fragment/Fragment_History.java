package fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activity.Albums;
import com.example.myapplication.activity.Bottom_bar;
import com.example.myapplication.activity.Main;
import com.example.myapplication.authservice.HWidAuth;
import com.example.myapplication.authservice.PhoneAuth;
import com.huawei.agconnect.auth.AGCAuthException;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Fragment_History extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        Button User_info = (Button) getView().findViewById(R.id.user_info);
        Button User_logout = (Button) getView().findViewById(R.id.user_logout);
        Button User_delete = (Button) getView().findViewById(R.id.user_delete);
        Button User_aboutus = (Button) getView().findViewById((R.id.user_aboutus));
        //Button User_advice = (Button) getView().findViewById(R.id.user_advice);
        // action bar
        final ImageView back = (ImageView) getView().findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        User_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuth phoneAuth = new PhoneAuth();
                if (phoneAuth.isUserSignIn()) {
                    String Uid = phoneAuth.getCurrentUserUid();
                    String phoneNum = phoneAuth.getCurrentUserAccount();
                    new SweetAlertDialog(view.getContext())
                            .setTitleText("欢迎使用WEight!")
                            .setContentText("用户ID：" + Uid + "<br/>" + "手机号：" + phoneNum)
                            .setConfirmText("确认")
                            .show();
                } else {
                    new SweetAlertDialog(view.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("发生错误！")
                            .setContentText("用户未登录！")
                            .setConfirmText("确认")
                            .show();
                }
            }
        });

        User_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuth phoneAuth = new PhoneAuth();
                if (phoneAuth.isUserSignIn()) {
                    new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("确定退出当前登录吗？")
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    phoneAuth.signOut();
                                    sDialog.setTitleText("退出登录成功!")
                                            .setContentText("即将返回登录界面!")
                                            .setConfirmText("确认")
                                            .setConfirmClickListener(null)
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    Intent intent_logout = new Intent(view.getContext(), Main.class);
                                    Timer timer = new Timer();
                                    TimerTask task = new TimerTask() {
                                        @Override
                                        public void run() {
                                            startActivity(intent_logout); //执行
                                            getActivity().finish();
                                        }
                                    };
                                    timer.schedule(task, 1000 * 2); //2秒后
                                }
                            })
                            .setCancelText("取消")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(view.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("发生错误！")
                            .setContentText("用户未登录！")
                            .setConfirmText("确认")
                            .show();
                }
            }
        });

        User_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuth phoneAuth = new PhoneAuth();
                if (phoneAuth.isUserSignIn()) {
                    if (phoneAuth.getCurrentUserAccount() != null) {
                        LayoutInflater inflater = getLayoutInflater();
                        View view_text = inflater.inflate(R.layout.fragment_me_userdel, null, false);
                        new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("确定注销当前账户吗？")
                                .setContentText("此操作不可逆!")
                                .setConfirmText("确认")
                                .setCustomView(view_text)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        EditText A1 = view_text.findViewById(R.id.et_01);
                                        String passwd = A1.getText().toString();
                                        boolean del_flag = phoneAuth.deleteUser(passwd);
                                        if (del_flag) {
                                            sDialog.setTitleText("账户已成功注销!")
                                                    .setContentText("即将返回登录界面!")
                                                    .setConfirmText("确认")
                                                    .setConfirmClickListener(null)
                                                    .showCancelButton(false)
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            Intent intent_del = new Intent(view.getContext(), Main.class);
                                            Timer timer = new Timer();
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    startActivity(intent_del); //执行
                                                    getActivity().finish();
                                                }
                                            };
                                            timer.schedule(task, 1000 * 2); //2秒后
                                        } else {
                                            sDialog.setTitleText("注销失败!")
                                                    .setContentText("请检查密码是否正确!")
                                                    .setConfirmText("确认")
                                                    .setConfirmClickListener(null)
                                                    .showCancelButton(false)
                                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        }
                                    }
                                })
                                .setCancelText("取消")
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    else {
                        new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("确定注销当前账号吗？")
                            .setContentText("此操作不可逆！")
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    HWidAuth hWidAuth = new HWidAuth();

                                    AGConnectAuth.getInstance().signIn(getActivity(), AGConnectAuthCredential.HMS_Provider).addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                                        @Override
                                        public void onSuccess(SignInResult signInResult) {
                                            boolean del_flag = hWidAuth.deleteUser();
                                            if (del_flag) {
                                                sDialog.setTitleText("账户已成功注销!")
                                                        .setContentText("即将返回登录界面!")
                                                        .setContentText("下次关联账号登录会为您生成新账户")
                                                        .setConfirmText("确认")
                                                        .setConfirmClickListener(null)
                                                        .showCancelButton(false)
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                Intent intent_del = new Intent(view.getContext(), Main.class);
                                                Timer timer = new Timer();
                                                TimerTask task = new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(intent_del); //执行
                                                        getActivity().finish();
                                                    }
                                                };
                                                timer.schedule(task, 1000 * 2); //2秒后
                                            } else {
                                                sDialog.setTitleText("注销失败!")
                                                        .setContentText("请重试!")
                                                        .setConfirmText("确认")
                                                        .setConfirmClickListener(null)
                                                        .showCancelButton(false)
                                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            }
                                        }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                sDialog.setTitleText("注销失败!")
                                                        .setContentText("请重试!")
                                                        .setConfirmText("确认")
                                                        .setConfirmClickListener(null)
                                                        .showCancelButton(false)
                                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            }
                                        });
                                    }
                                })
                                .setCancelText("取消")
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                } else {
                    new SweetAlertDialog(view.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("发生错误！")
                            .setContentText("用户未登录！")
                            .setConfirmText("确认")
                            .show();
                }
            }
        });

        User_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(view.getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("WEight——健康生活，从了解食物开始")
                        .setContentText("Version 1.1")
                        .setCustomImage(R.mipmap.ic_launcher_foreground)
                        .setConfirmText("确认")
                        .show();
            }
        });

//        User_advice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
}