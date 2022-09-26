package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class BaseActivity extends AppCompatActivity {
    /**
     * 状态栏的颜色
     */
    public static final int THIS_STATES_BAR_COLOR = R.color.lightblue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_bar);
        // 设置状态栏背景
        setThisStatusBarColor(THIS_STATES_BAR_COLOR);
    }


    /**
     * 修改状态栏的背景
     * @param resId 颜色id
     */
    public  void setThisStatusBarColor(final int resId) {
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //利用反射机制修改状态栏背景
                int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
                View statusBarView = getWindow().findViewById(identifier);
                // 这里传入想设置的颜色
                statusBarView.setBackgroundResource(resId);
                getWindow().getDecorView().removeOnLayoutChangeListener(this);
            }
        });
    }
}

