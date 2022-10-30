package com.example.myapplication.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Goods;
import com.example.myapplication.R;
import com.example.myapplication.upload.UploadEngine;
import com.example.myapplication.upload.UploadEnginePhaseOne;
import com.example.myapplication.upload.UploadEnginePhaseTwo;
import com.example.myapplication.util.FunctionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Albums extends BaseActivity {
    private  ImageView albumsPicture;
    public static final int CHOOSE_PHOTO = 2;
    private Button pestDection;
    private Intent intent3;

    // 食物分类top3的名称及置信度
    public Map<String, Double> top3;
    // 食物分类top3的id及名称
    public Map<String, String> top3Id = new HashMap<>();
    // 请求的临时文件tag
    public String tag;

//    private EditText A1;
//    private EditText B1;

    // Dialog2
    Button bOpenAlertDialog;
    TextView tvSelectedItemPreview;

    String imagePath;
    String focal = "27";
    // handler + thread 处理post请求
    private Handler mHandlerPhaseOne = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                JSONObject result = (JSONObject) msg.obj;
                int code = msg.arg1;
                if (code == -1) {
                    new SweetAlertDialog(albumsPicture.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("未检测到容器！")
                            .setContentText("请重新选取图片，建议使用盘子或碗！")
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    finish();
                                }
                            })
                            .show();
                } else if (code == -2){
                    new SweetAlertDialog(albumsPicture.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("未检测到食物！")
                            .setContentText("请重新选取图片，建议选取中国菜！")
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    finish();
                                }
                            })
                            .show();
                } else if (code == -3) {
                    new SweetAlertDialog(albumsPicture.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("无法连接服务器！")
                            .setContentText("请稍后再试！")
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                    if (top3Id != null)
                        top3Id.clear();
                    try {
                        int count = result.getInt("count");
                        for (int i = 1; i < count + 1; i++) {
                            top3Id.put(result.getString("top" + i + "_id"), result.getString("top" + i + "_name"));
                        }
                        tag = result.getString("tag");

                        Log.i("s", "-----------------: " + top3Id);
                        Log.i("s", "-----------------: " + tag);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
/*                    if (top3 != null)
                        top3.clear();
                    try {
                        for (int i = 0; i < 3; i++) {
                            top3.put(result.getString("top" + i + "_name"), result.getDouble("top" + i + "_confidence"));
                        }
                        tag = result.getString("tag");

                        Log.i("s", "-----------------: " + top3);
                        Log.i("s", "-----------------: " + tag);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
            }

        }
    };

    private Handler mHandlerPhaseTwo = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                Goods good = (Goods) msg.obj;
                int code = msg.arg1;
                if (code == -3) {
                    new SweetAlertDialog(albumsPicture.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("无法连接服务器！")
                            .setContentText("请稍后再试！")
                            .setConfirmText("确认")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                    //传输数据
                    intent3.putExtra("foodname", good.getFoodName());
                    intent3.putExtra("heats", good.getHeats());
                    intent3.putExtra("fat", good.getFat());
                    intent3.putExtra("protein", good.getProtein());
                    intent3.putExtra("Carbohydrates", good.getCarbohydrates());
                    intent3.putExtra("Ca", good.getCa());
                    intent3.putExtra("Fe", good.getFe());
                    intent3.putExtra("imgpath", imagePath);
                    startActivity(intent3);
                    finish();
                }
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.albums);
        //action bar
        //声明控件
        pestDection=super.findViewById(R.id.pestDetection);
        albumsPicture = super.findViewById(R.id.picture);

        final ImageView back = (ImageView) this.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        openAlbum();
        pestDection.setOnClickListener(new pestDectionFuntion());


    }

    private void openAlbum() {
        //获取本地图片
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册

    }


    //点击“图像检测”按钮
    private class pestDectionFuntion implements View.OnClickListener {
        public void onClick(View view){
            if (!FunctionUtils.isFastDoubleClick()) {
                tag = null;
                //分析接口
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UploadEnginePhaseOne uploadEnginePhaseOne = new UploadEnginePhaseOne(getApplicationContext());
                        uploadEnginePhaseOne.uploadToClass(imagePath);
                        do {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (!uploadEnginePhaseOne.flag);
                        Message message = new Message();
                        message.what = 0;
                        message.obj = uploadEnginePhaseOne.result;
                        message.arg1 = uploadEnginePhaseOne.code;
                        mHandlerPhaseOne.sendMessage(message);
                    }
                }).start();

                showDialog1();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (requestCode == CHOOSE_PHOTO && resultCode == RESULT_OK && null != data) {
                    if (Build.VERSION.SDK_INT >= 19) { //版本要求
                        handleImageOnKitkat(data); //满足要求的以此种形式处理照片
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                } else {
//                    intent2 = new Intent(getApplicationContext(), Bottom_bar.class);
//                    startActivity(intent2);
                    finish();
                }
            default: {

            }
            break;
        }
    }

    //对地址进行解析，根据三种不同的提供Uri方式采用不同的方法。
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

    // Phase1 输入弹窗
    private void showDialog1(){
        LayoutInflater inflater = getLayoutInflater();
        View view_par = inflater.inflate(R.layout.par_dialog,null,false);
        SweetAlertDialog dialog = new SweetAlertDialog(view_par.getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("请估算以下参数信息")
                .setConfirmText("确认")
                .setCustomView(view_par)
                .setCancelText("取消")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        if (!FunctionUtils.isFastDoubleClick()) {
                            sDialog.dismiss();
                            EditText A1 = view_par.findViewById(R.id.et_01);
                            EditText B1 = view_par.findViewById(R.id.et_02);
                            String A = A1.getText().toString();
                            String B = B1.getText().toString();
                            focal = "27";

                            if (A.isEmpty() || B.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "参数未输入完整", Toast.LENGTH_SHORT).show();
                                sDialog.show();
                            }
                            if (!A.isEmpty() && !B.isEmpty()) {
                                try {
                                    //读取图片EXIF信息焦距
                                    ExifInterface exifInterface = new ExifInterface(imagePath);
                                    focal = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);
                                    Log.i("s", "-----------------focal: " + focal);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (focal == null || Integer.parseInt(focal) == 0) {
                                    focal = "27";
                                }

                                // 展示选择食物的弹窗
                                showDialog2(A, B, focal);
                            }
                        }
                    }
                });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/6*5), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // Phase2选择食物弹窗
    private void showDialog2(String A, String B, String focal){
        // TODO 将替换为选择食物的弹窗
        //当用户输入完参数后，监听云端是否返回结果
        // register both UI elements with their appropriate IDs.
        tvSelectedItemPreview = findViewById(R.id.selectedItemPreview);

        // single item array instance to store which element is selected by user initially
        // it should be set to zero meaning none of the element is selected by default
        final int[] checkedItem = {-1};

        // AlertDialog builder instance to build the alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Albums.this);

        // set the custom icon to the alert dialog
        alertDialog.setIcon(R.mipmap.logo);

        // title of the alert dialog
        alertDialog.setTitle("请选择您所拍摄的食物名称");

        // list of the items to be displayed to the user in the
        // form of list so that user can select the item from
        // 设置食物名称
        final String[] listItems = new String[]{"麻婆豆腐", "牛肉面", "汉堡", "以上均不是，返回主界面重新选择图片"};

        // the function setSingleChoiceItems is the function which
        // builds the alert dialog with the single item selection
        alertDialog.setSingleChoiceItems(listItems, checkedItem[0], (dialog, which) -> {
            // update the selected item which is selected by the user so that it should be selected
            // when user opens the dialog next time and pass the instance to setSingleChoiceItems method

            //返回用户选择的item下标
            checkedItem[0] = which;
            // now also update the TextView which previews the selected item
            tvSelectedItemPreview.setText("Selected Item is : " + listItems[which]);

            // when selected an item the dialog should be closed with the dismiss method
            dialog.dismiss();
        });

        // set the negative button if the user is not interested to select or change already selected item
        alertDialog.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });

        alertDialog.setPositiveButton("确认", (dialog, which) -> {
            if (!FunctionUtils.isFastDoubleClick()) {
                dialog.dismiss();
                SweetAlertDialog pDialog = new SweetAlertDialog(view_par.getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("请稍后！");
                pDialog.setContentText("正在进行食物识别与营养估计！");
                pDialog.setCancelable(false);
                pDialog.show();

                intent3 = new Intent(getApplicationContext(), Analyze.class);

                //分析接口
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /* 等待Phase One完成 */
                        do {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (tag == null);

                        UploadEnginePhaseTwo uploadEnginePhaseTwo = new UploadEnginePhaseTwo(getApplicationContext());
                        uploadEnginePhaseTwo.uploadToAnalyze("315", tag, Double.parseDouble(focal), Double.parseDouble(A),
                                Double.parseDouble(B));
                        do {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (!uploadEnginePhaseTwo.flag);
                        Message message = new Message();
                        message.what = 0;
                        message.obj = uploadEnginePhaseTwo.Good;
                        message.arg1 = uploadEnginePhaseTwo.code;
                        mHandlerPhaseTwo.sendMessage(message);
                        pDialog.dismiss();
                    }
                }).start();
            }
        });

        // create and build the AlertDialog instance with the AlertDialog builder instance
        AlertDialog customAlertDialog = alertDialog.create();

        // show the alert dialog when the button is clicked
        customAlertDialog.show();
        customAlertDialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/6*5), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    CharSequence getSavedText(){
        return ((TextView)findViewById(R.id.et_1)).getText();
    }
    void setSavedText(CharSequence text){
        ((TextView)findViewById(R.id.et_1)).setText(text);
    }

}
