package com.example.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//camera界面的功能就是在MainActivity点击拍照按钮时，调用本地摄像机，将拍的照片显示到ImageView控件，
// 图片检测功能未添加，因为还没有载入模型，图片保存就是将ImageView控件中的图片保存到本地中。
public class Camera extends Activity {

    //声明控件
    private ImageView cameraPicture;
    public static final int TAKE_PHOTO = 1;
    private Button pestDection=null;
    //private Button pictureSave=null;
    private Intent intent3, intent2;
    private Uri imageUri;
    private EditText A1;
    private EditText B1;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static Bitmap bitmap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);
        pestDection=super.findViewById(R.id.pestDetection);
        //pictureSave=super.findViewById(R.id.pictureSave);
        cameraPicture = super.findViewById(R.id.picture);

        // 创建一个File对象，用于保存摄像头拍下的图片，这里把图片命名为output_image.jpg

         //并将它存放在手机SD卡的应用关联缓存目录下
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        // 对照片的更换设置,每次只能存一张图片
        try {
            // 如果上一次的照片存在，就删除
            if (outputImage.exists()) {
                outputImage.delete();
            }
            // 创建一个新的文件
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果Android版本大于等于7.0
        if (Build.VERSION.SDK_INT >= 24) {
            // 将File对象转换成一个封装过的Uri对象
            imageUri = FileProvider.getUriForFile(this, "cn.edu.hust.weight.fileprovider", outputImage);
            Log.d("MainActivity", outputImage.toString() + "手机系统版本高于Android7.0");
        } else {
            // 将File对象转换为Uri对象，这个Uri标识着output_image.jpg这张图片的本地真实路径
            Log.d("MainActivity", outputImage.toString() + "手机系统版本低于Android7.0");
            imageUri = Uri.fromFile(outputImage);
        }
        // 动态申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
        } else {
            // 启动相机程序
            startCamera();

        }


        pestDection.setOnClickListener(new Camera.pestDectionFuntion());

        //pictureSave.setOnClickListener(new Camera.pictureSaveFunction());

    }
    private void startCamera() {
        Intent intent4 = new Intent("android.media.action.IMAGE_CAPTURE");
        // 指定图片的输出地址为imageUri
        intent4.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent4, TAKE_PHOTO);

    }
    //当我们在第一个Activity打开第二个Activity时，第二个Activity关闭并想返回数据给第一个Activity时，
    // 我们就要重写onActivityResult(int requestCode, int resultCode, Intent data)
    private class pestDectionFuntion implements View.OnClickListener {

        public void onClick(View view){
            showDialog();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK ) {
                    try {
                        // 将图片解析成Bitmap对象
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        cameraPicture.setImageBitmap(bitmap);
                        BitmapDrawable bmpDrawable = (BitmapDrawable) cameraPicture.getDrawable();

                        Bitmap bitmap2 = bmpDrawable.getBitmap();
                        saveToSystemGallery(bitmap2);//将图片保存到本地
                        Toast.makeText(getApplicationContext(),"图片已保存至本地相册！",Toast.LENGTH_SHORT).show();
                        //startActivity(intent3);//窗口切换
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    intent2 = new Intent(getApplicationContext(), Main.class);
                    startActivity(intent2);
                }
                break;
            default:
            {
                intent2 = new Intent(getApplicationContext(), Main.class);
                startActivity(intent2);
            }
                break;
        }
    }
    public void saveToSystemGallery(Bitmap bmp) {
        // 首先保存图片

        File appDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);// 发送广播，通知图库更新


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

                EditText A1 = view.findViewById(R.id.et_01);
                EditText B1 = view.findViewById(R.id.et_02);
                String A = A1.getText().toString();
                String B = B1.getText().toString();


                intent3 = new Intent(getApplicationContext(), Analyze.class);
//                            System.out.println(A.toString());
//                            System.out.println(B.toString());
                if (A.isEmpty() || B.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"您还未输入",Toast.LENGTH_SHORT).show();
                    dialog.show();
                }
                if(!A.isEmpty() && !B.isEmpty()){
                    try {
                        //读取图片EXIF信息焦距
                        ExifInterface exifInterface=new ExifInterface(getExternalCacheDir()+"/output_image.jpg");
                        String focal = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);
                        Log.i("s", "-----------------focal: "+ focal);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    

                    startActivity(intent3);
                }
                dialog.dismiss();
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

class ScreenUtils {

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

}
