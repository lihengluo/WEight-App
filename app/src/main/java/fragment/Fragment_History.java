package fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

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
        Button User_advice = (Button) getView().findViewById(R.id.user_advice);

        User_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(view.getContext())
                        .setTitleText("Here's a message!")
                        .setContentText("hello!")
                        .setConfirmText("确认")
                        .show();
            }
        });

        User_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定退出当前登陆吗？")
                        .setConfirmText("确认")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.setTitleText("退出登陆成功!")
                                        .setContentText("即将返回登陆界面!")
                                        .setConfirmText("确认")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
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
        });

        User_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View view_text = inflater.inflate(R.layout.fragment_me_userdel,null,false);
                EditText A1 = view.findViewById(R.id.et_01);
                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定注销当前账户吗？")
                        .setContentText("此操作不可逆!")
                        .setConfirmText("确认")
                        .setCustomView(view_text)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.setTitleText("注销成功!")
                                        .setContentText("您的账户已注销!")
                                        .setConfirmText("确认")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                //String passwd = A1.getText().toString();
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
        });

        User_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(view.getContext())
                        .setTitleText("Here's a message!")
                        .setContentText("hello!")
                        .setConfirmText("确认")
                        .show();
            }
        });

        User_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}