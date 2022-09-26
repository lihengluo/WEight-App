package fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.myapplication.R;
import com.example.myapplication.activity.Albums;
import com.example.myapplication.activity.Camera;
import com.example.myapplication.activity.Bottom_bar;

import java.util.Random;


public class Fragment_main extends Fragment {

    private ImageView picture;
    Intent intent1, intent2;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //changeImage();
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeImage();
        Button chooseFromAlbum = (Button) getView().findViewById(R.id.choose_from_album);


        //从相册读取
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String foodname = myfoodname.getText().toString(); //存储食物名称
                //查看相册权限，如果没权限就给权限
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没权限，给权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
                    openAlbum();//打开album的界面
                } else {
                    //有权限 打开相册
                    openAlbum();//打开album的界面
                }
            }
        });

        //进行拍照
        Button takePhoto = (Button) getView().findViewById(R.id.take_photo);
        picture = (ImageView) getView().findViewById(R.id.picture);

        //同上，对相机权限进行检查
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String foodname = myfoodname.getText().toString();
                // 动态申请权限
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
                    startCamera();
                } else {
                    // 启动相机程序
                    startCamera();
                }
            }
        });

        //跳转的设置
        intent1=new Intent(getActivity(), Albums.class);//创建跳转到Albums显示的窗口的Intent
        intent2=new Intent(getActivity(), Camera.class);//创建跳转到Camera显示的窗口的Intent
    }

    public void openAlbum() {
        startActivity(intent1);//进入album的窗口界面

    }
    public void startCamera() {
        startActivity(intent2);//进入camera的窗口界面

    }
    public void changeImage() {
        int[] mArray = {
                R.drawable.suggestion1,
                R.drawable.suggestion2,
                R.drawable.suggestion3,
                R.drawable.suggestion4,
                R.drawable.suggestion5,
                R.drawable.suggestion6,
        };
        LinearLayout[] lArray = {
                (LinearLayout) getView().findViewById(R.id.layout1),
                (LinearLayout) getView().findViewById(R.id.layout2),
                (LinearLayout) getView().findViewById(R.id.layout3),
                (LinearLayout) getView().findViewById(R.id.layout4),
        };
        ImageView[] ivBg = {
                new ImageView(this.getContext()),
                new ImageView(this.getContext()),
                new ImageView(this.getContext()),
                new ImageView(this.getContext()),
        };
        ImageView[] ivBar = {
                new ImageView(this.getContext()),
                new ImageView(this.getContext()),
                new ImageView(this.getContext()),
                new ImageView(this.getContext()),
        };
        Drawable[] drawable = new Drawable[4];

        //把图片资源文件变成数组，注意R文件中数据对应的都是int类型
        int[] ar = getArray();
        //设置图片
        for (int m = 0; m < ar.length; m++) {
            drawable[m] = getContext().getResources().getDrawable(mArray[ar[m]]);
            ivBg[m].setImageDrawable(drawable[m]);
            ivBg[m].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            lArray[m].addView(ivBg[m]);
//            ivBar[m].setImageDrawable(getContext().getResources().getDrawable(R.drawable.bar));
//            lArray[m].addView(ivBar[m],1);
        }
    }

    public int[] getArray() {
        Random r1 = new Random();
        int b = r1.nextInt(6);

        //创建一个包含5个元素的数组, 存放随机数
        int[] a = new int[4];

        //第一个随机数,不需要判断是否重复,直接放进数组
        a[0] = b;

        //外层,用来放剩余的四个元素,下标从1开始
        for (int i = 1; i < a.length; i++) {
            b = r1.nextInt(6);

            //将取到的随机数,与已经存在的元素进行比较，从下标=0开始比较
            for (int num = 0; num < i; num++) {
                //如果和已经存在元素相同,需要重新取随机数,并且新取到的随机数要重新与a[0]开始比较,知道取到的随机数不重复
                while (b == a[num]) {
                    b = r1.nextInt(6);
                    num = 0;
                }
            }

            a[i] = b;
        }
        return a;
    }

}