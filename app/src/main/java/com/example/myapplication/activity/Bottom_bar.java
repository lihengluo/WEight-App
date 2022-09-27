package com.example.myapplication.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.util.FunctionUtils;

import fragment.Fragment_History;
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
    private TextView bottom_bar_text_3;
    private ImageView bottom_bar_image_3;
    private RelativeLayout bottom_bar_1_btn;
    private RelativeLayout bottom_bar_2_btn;
    private RelativeLayout bottom_bar_3_btn;
    private Fragment_main fragment_main;
    private Fragment_me fragment_me;
    private RelativeLayout main_body;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_bar);

        Intent myintent = getIntent();
        String info = myintent.getStringExtra("登录信息"); //接受登录信息，默认值为-1；
        Log.v(TAG, "----login info："+info);
        initView();//初始化数据
        //对单选按钮进行监听，选中、未选中

        //找到控件
        //initView();
        fragment_main = new Fragment_main();
        fragment_me = new Fragment_me();
        setMain();
        setSelectStatus(0);
        final int[] index = {0};

        bottom_bar_1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    if (view.getId() == R.id.bottom_bar_1_btn) {
                        if (index[0] == 1 || index[0] == 2) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new Fragment_main()).commit();
                            index[0] = 0;
                        }
                        setSelectStatus(index[0]);
                    }
                }
            }
        });
        bottom_bar_2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    if (view.getId() == R.id.bottom_bar_2_btn) {
                        if (index[0] == 0 || index[0] == 2) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new Fragment_History()).commit();
                            index[0] = 1;
                        }
                        setSelectStatus(index[0]);
                    }
                }
            }
        });

        bottom_bar_3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    if (view.getId() == R.id.bottom_bar_3_btn) {
                        if (index[0] == 0 || index[0] == 1) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new Fragment_me()).commit();
                            index[0] = 2;
                        }
                        setSelectStatus(index[0]);
                    }
                }
            }
        });

        

    }

    private void initView() {
        bottom_bar_text_1 = findViewById(R.id.bottom_bar_text_1);
        bottom_bar_image_1 = findViewById(R.id.bottom_bar_image_1);
        bottom_bar_text_2 = findViewById(R.id.bottom_bar_text_2);
        bottom_bar_image_2 = findViewById(R.id.bottom_bar_image_2);
        bottom_bar_image_3 = findViewById(R.id.bottom_bar_image_3);
        bottom_bar_text_3 = findViewById(R.id.bottom_bar_text_3);
        bottom_bar_1_btn = findViewById(R.id.bottom_bar_1_btn);
        bottom_bar_2_btn = findViewById(R.id.bottom_bar_2_btn);
        bottom_bar_3_btn = findViewById(R.id.bottom_bar_3_btn);

        main_body = findViewById(R.id.main_body);

    }

    private void setSelectStatus(int index) {
        switch (index) {
            case 0:
                //图片点击选择变换图片，颜色的改变，其他变为原来的颜色，并保持原有的图片
                bottom_bar_image_1.setImageResource(R.drawable.main_normal);
                bottom_bar_text_1.setTextColor(Color.parseColor("#0097F7"));
                //其他的文本颜色不变
                bottom_bar_text_2.setTextColor(Color.parseColor("#666666"));
                //图片也不变
                bottom_bar_image_2.setImageResource(R.drawable.me_normal);
                bottom_bar_text_3.setTextColor(Color.parseColor("#666666"));
                //图片也不变
                bottom_bar_image_3.setImageResource(R.drawable.mee_normal);
                break;
            case 1://同理如上
                bottom_bar_image_1.setImageResource(R.drawable.main_select);
                bottom_bar_text_1.setTextColor(Color.parseColor("#666666"));
                bottom_bar_text_2.setTextColor(Color.parseColor("#0097F7"));
                bottom_bar_image_2.setImageResource(R.drawable.me_select);
                bottom_bar_text_3.setTextColor(Color.parseColor("#666666"));
                bottom_bar_image_3.setImageResource(R.drawable.mee_normal);
                break;
            case 2:
                bottom_bar_image_1.setImageResource(R.drawable.main_normal);
                bottom_bar_text_2.setTextColor(Color.parseColor("#666666"));
                //其他的文本颜色不变
                bottom_bar_text_1.setTextColor(Color.parseColor("#666666"));
                //图片也不变
                bottom_bar_image_2.setImageResource(R.drawable.me_normal);
                bottom_bar_text_3.setTextColor(Color.parseColor("#0097F7"));
                //图片也不变
                bottom_bar_image_3.setImageResource(R.drawable.mee_select);

                break;
        }
    }

    //用于打开初始页面
    private void setMain() {
        //getSupportFragmentManager() -> beginTransaction() -> add -> (R.id.main_boy,显示课程 new CourseFragment()
        this.getSupportFragmentManager().beginTransaction().add(R.id.main_body, fragment_main).commit();
    }
}
