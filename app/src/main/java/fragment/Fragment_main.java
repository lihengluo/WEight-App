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
                R.drawable.suggestion4};

        //生成随机数，设置为4，是0,1,2,3,4 五个数
        Random random = new Random();
        int index = random.nextInt(4);

        //ImageView对应的id
        LinearLayout linear = (LinearLayout) getView().findViewById(R.id.layout1);
        ImageView ivBg = new ImageView(this.getContext());
        //把图片资源文件变成数组，注意R文件中数据对应的都是int类型

        Drawable drawable =getContext().getResources().getDrawable(mArray[index]);

        //设置图片
        ivBg.setImageDrawable(drawable);
        ivBg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        linear.addView(ivBg);

    }

}