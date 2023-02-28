package com.example.myapplication.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static Toast myToast;
    public static void showMessage(Context context, String message){
        if(myToast == null){
            myToast = Toast.makeText(context,message, Toast.LENGTH_SHORT); //新建一个toast
        }
        else{
            myToast.setText(message);
        }
        myToast.show();
    }
}
