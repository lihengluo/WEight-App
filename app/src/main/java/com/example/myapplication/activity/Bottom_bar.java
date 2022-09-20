package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import fragment.Fragment_main;
import fragment.Fragment_me;

public class Bottom_bar extends AppCompatActivity {

    //声明控件
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    //private EditText myfoodname;
    private Intent intent1, intent2;

    private TextView bottom_bar_text_1;
    private ImageView bottom_bar_image_1;
    private TextView bottom_bar_text_2;
    private ImageView bottom_bar_image_2;
    private RelativeLayout bottom_bar_1_btn;
    private RelativeLayout bottom_bar_2_btn;
    private Fragment_main fragment_main;
    private RelativeLayout main_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_bar);

        //getSupportFragmentManager().beginTransaction().replace(R.id.main_body,new Fragment_main()).commit();
        initView();//初始化数据
        //对单选按钮进行监听，选中、未选中

        //找到控件
        //initView();
        fragment_main = new Fragment_main();
        setMain();


        bottom_bar_1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.bottom_bar_1_btn) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new Fragment_main()).commit();
                    setSelectStatus(0);
                }
            }
        });
        bottom_bar_2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new Fragment_me()).commit();
                if (view.getId() == R.id.bottom_bar_2_btn) setSelectStatus(1);
            }
        });

    }

    private void initView() {
        bottom_bar_text_1 = findViewById(R.id.bottom_bar_text_1);
        bottom_bar_image_1 = findViewById(R.id.bottom_bar_image_1);
        bottom_bar_text_2 = findViewById(R.id.bottom_bar_text_2);
        bottom_bar_image_2 = findViewById(R.id.bottom_bar_image_2);
        bottom_bar_1_btn = findViewById(R.id.bottom_bar_1_btn);
        bottom_bar_2_btn = findViewById(R.id.bottom_bar_2_btn);

        main_body = findViewById(R.id.main_body);


    }

    private void setSelectStatus(int index) {
        switch (index) {
            case 0:
                //图片点击选择变换图片，颜色的改变，其他变为原来的颜色，并保持原有的图片
                bottom_bar_image_1.setImageResource(R.drawable.main_select);
                bottom_bar_text_1.setTextColor(Color.parseColor("#0097F7"));
                //其他的文本颜色不变
                bottom_bar_text_2.setTextColor(Color.parseColor("#666666"));
                //图片也不变
                bottom_bar_image_2.setImageResource(R.drawable.me_normal);
                break;
            case 1://同理如上
                bottom_bar_image_1.setImageResource(R.drawable.main_normal);
                bottom_bar_text_2.setTextColor(Color.parseColor("#0097F7"));
                //其他的文本颜色不变
                bottom_bar_text_1.setTextColor(Color.parseColor("#666666"));
                //图片也不变
                bottom_bar_image_2.setImageResource(R.drawable.me_select);
                break;
        }
    }

    //用于打开初始页面
    private void setMain() {
        //getSupportFragmentManager() -> beginTransaction() -> add -> (R.id.main_boy,显示课程 new CourseFragment()
        this.getSupportFragmentManager().beginTransaction().add(R.id.main_body, fragment_main).commit();
    }
}
