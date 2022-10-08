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

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
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
        // action bar
        final ImageView back = (ImageView) getView().findViewById(R.id.back);

        intent1=new Intent(getActivity(), Albums.class);//创建跳转到Albums显示的窗口的Intent
        intent2=new Intent(getActivity(), Camera.class);//创建跳转到Camera显示的窗口的Intent



        CheckAndroidPermission();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        //从相册读取
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String foodname = myfoodname.getText().toString(); //存储食物名称
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    startActivity(intent1);//进入album的窗口界面
//                }
//                else {
//                    Log.v("tag", "----未授权");
//                }
                startActivity(intent1);//进入album的窗口界面
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
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    startActivity(intent2);//进入camera的窗口界面
//                }
//                else {
//                    Log.v("tag", "----未授权");
//                }
                startActivity(intent2);
            }
        });

        //跳转的设置

    }

    private void CheckAndroidPermission() {
        List<String> permissionLists = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionLists.add(Manifest.permission.CAMERA);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionLists.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionLists.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(!permissionLists.isEmpty()){//说明肯定有拒绝的权限
            ActivityCompat.requestPermissions(getActivity(), permissionLists.toArray(new String[permissionLists.size()]), 100);
        }else{

        }
    }

    public void changeImage() {
        int[] mArray = {
//                R.drawable.suggestion1,
//                R.drawable.suggestion2,
//                R.drawable.suggestion3,
//                R.drawable.suggestion4,
//                R.drawable.suggestion5,
//                R.drawable.suggestion6,
                  R.drawable.suggestion7,
                  R.drawable.suggestion8,
                  R.drawable.suggestion9,
                  R.drawable.suggestion10,
                  R.drawable.suggestion11,
                  R.drawable.suggestion12,
                R.drawable.suggestion13,
                R.drawable.suggestion14,
                R.drawable.suggestion15,
                R.drawable.suggestion16,
                R.drawable.suggestion17,
                R.drawable.suggestion18,
        };
        LinearLayout[] lArray = {
                (LinearLayout) getView().findViewById(R.id.layout1),
                (LinearLayout) getView().findViewById(R.id.layout2),
                (LinearLayout) getView().findViewById(R.id.layout3),
                (LinearLayout) getView().findViewById(R.id.layout4),
                (LinearLayout) getView().findViewById(R.id.layout5),
        };
        ImageView[] ivBg = {
                new ImageView(this.getContext()),
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
                new ImageView(this.getContext()),
        };
        Drawable[] drawable = new Drawable[5];

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
        int b = r1.nextInt(12);

        //创建一个包含5个元素的数组, 存放随机数
        int[] a = new int[5];

        //第一个随机数,不需要判断是否重复,直接放进数组
        a[0] = b;

        //外层,用来放剩余的四个元素,下标从1开始
        for (int i = 1; i < a.length; i++) {
            b = r1.nextInt(12);

            //将取到的随机数,与已经存在的元素进行比较，从下标=0开始比较
            for (int num = 0; num < i; num++) {
                //如果和已经存在元素相同,需要重新取随机数,并且新取到的随机数要重新与a[0]开始比较,知道取到的随机数不重复
                while (b == a[num]) {
                    b = r1.nextInt(12);
                    num = 0;
                }
            }

            a[i] = b;
        }
        return a;
    }

}