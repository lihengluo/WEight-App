package com.example.myapplication.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Goods;
import com.example.myapplication.R;
import com.example.myapplication.upload.UploadEngine;
import com.example.myapplication.util.FunctionUtils;

import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;

public class Albums extends BaseActivity {
    private  ImageView albumsPicture;
    public static final int CHOOSE_PHOTO = 2;
    private Button pestDection=null;
    //private Button pictureSave=null;
    private Intent intent2;
    private Intent intent3;
    private EditText A1;
    private EditText B1;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String imagePath;


    protected void onCreate(Bundle savedInstanceState) {
        //Bundle类型的数据与Map类型的数据相似，都是以key-value的形式存储数据的。
        super.onCreate(savedInstanceState);  //调用父类的onCreate构造函数

        //运用albums的布局
        setContentView(R.layout.albums);

        //声明控件
        pestDection=super.findViewById(R.id.pestDetection);
        //pictureSave=super.findViewById(R.id.pictureSave);
        albumsPicture = super.findViewById(R.id.picture);

        //获取权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
        }

        openAlbum();
        pestDection.setOnClickListener(new pestDectionFuntion());
        //pictureSave.setOnClickListener(new pictureSaveFunction());

    }

    private void openAlbum() {
        //获取本地图片
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册

    }

    //利用外部类实现点击事件

    //点击“图像检测”按钮
    private class pestDectionFuntion implements View.OnClickListener {
        public void onClick(View view){
            if (!FunctionUtils.isFastDoubleClick()) {
                showDialog();
            }
        }
    }

    @Override  //@Override是伪代码,表示重写 下边的方法是继承父类的方法，对其覆盖
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                //相册照片

                //进行照片的处理
                if (requestCode == CHOOSE_PHOTO && resultCode == RESULT_OK && null != data) {
                    if (Build.VERSION.SDK_INT >= 19) { //版本要求
                        handleImageOnKitkat(data); //满足要求的以此种形式处理照片
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                } else {
                    intent2 = new Intent(getApplicationContext(), Bottom_bar.class);
                    startActivity(intent2);
                }
            default: {

            }
            break;
        }
    }

    //对地址进行解析，根据三种不同的提供Uri方式采用不同的方法。
    //document 类型的 Uri
    //content 类型的 uri
    //file 类型的 Uri
    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是File类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //根据图片路径显示图片
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }
    //获取图片路径
    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        Log.v(TAG, "----imagepath:"+path);
        return path;
    }
    //展示图片
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            albumsPicture.setImageBitmap(bitmap);//将图片放置在控件上
        }else {
            Toast.makeText(this,"得到图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog(){


        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_edit_dialog,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();



        Button btn_cancel_high_opion = view.findViewById(R.id.no);
        Button btn_agree_high_opion = view.findViewById(R.id.yes);

        dialog.setCancelable(true);//设置对话框不能按返回键取消


        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        btn_agree_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FunctionUtils.isFastDoubleClick()) {
                    EditText A1 = view.findViewById(R.id.et_01);
                    EditText B1 = view.findViewById(R.id.et_02);
                    String A = A1.getText().toString();
                    String B = B1.getText().toString();
                    String focal = "27";

                    intent3 = new Intent(getApplicationContext(), Analyze.class);

                    if (A.isEmpty() || B.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "您还未输入", Toast.LENGTH_SHORT).show();
                        dialog.show();
                    }
                    if (!A.isEmpty() && !B.isEmpty()) {
                        try {
                            //读取图片EXIF信息焦距
                            ExifInterface exifInterface = new ExifInterface(getExternalCacheDir() + "/output_image.jpg");
                            focal = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);
                            Log.i("s", "-----------------focal: " + focal);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //分析接口
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
                            }
                        }
                        if (focal == null || Integer.parseInt(focal) == 0) {
                            focal = "27";
                        }
                        UploadEngine uploadEngine = new UploadEngine(getApplicationContext());

                        uploadEngine.uploadToDetect(imagePath, Double.parseDouble(focal), Double.parseDouble(A),
                                Double.parseDouble(B));
                        //while (!uploadEngine.flag);
                        do {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (!uploadEngine.flag);

                        if (uploadEngine.Good == null) {
                            Toast toast = Toast.makeText(getApplicationContext(), "未识别到食物！请重新选取图片！3秒后跳转~", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            dialog.dismiss();
                            Intent intent6 = new Intent(getApplicationContext(), Bottom_bar.class);
                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    startActivity(intent6); //执行
                                }
                            };
                            timer.schedule(task, 1000 * 3); //5秒后
                        } else {
                            //传输数据
                            Goods good = uploadEngine.Good;
                            intent3.putExtra("foodname", good.getFoodName());
                            intent3.putExtra("heats", good.getHeats());
                            intent3.putExtra("fat", good.getFat());
                            intent3.putExtra("protein", good.getProtein());
                            intent3.putExtra("Carbohydrates", good.getCarbohydrates());
                            intent3.putExtra("Ca", good.getCa());
                            intent3.putExtra("Fe", good.getFe());
                            intent3.putExtra("imgpath", imagePath);
                            startActivity(intent3);
                        }
                    }
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    CharSequence getSavedText(){
        return ((TextView)findViewById(R.id.et_1)).getText();
    }
    void setSavedText(CharSequence text){
        ((TextView)findViewById(R.id.et_1)).setText(text);
    }

}
