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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.Goods;
import com.example.myapplication.R;
import com.example.myapplication.upload.UploadEngine;
import com.example.myapplication.upload.UploadEnginePhaseOne;
import com.example.myapplication.upload.UploadEnginePhaseTwo;
import com.example.myapplication.util.FunctionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

//camera界面的功能就是在MainActivity点击拍照按钮时，调用本地摄像机，将拍的照片显示到ImageView控件，
// 图片检测功能未添加，因为还没有载入模型，图片保存就是将ImageView控件中的图片保存到本地中。
public class Camera extends BaseActivity {

    //声明控件
    private ImageView cameraPicture;
    public static final int TAKE_PHOTO = 1;
    private Button pestDection;
    private Intent intent3, intent2;
    private Uri imageUri;
    private EditText A1;
    private EditText B1;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // 食物分类top3的名称及置信度
    public Map<String, Double> top3;
    // 食物分类top3的id及名称
    public Map<String, String> top3Id = new HashMap<>();
    // 请求的临时文件tag
    public String tag;

    public static Bitmap bitmap;
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
                    new SweetAlertDialog(cameraPicture.getContext(), SweetAlertDialog.WARNING_TYPE)
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
                    new SweetAlertDialog(cameraPicture.getContext(), SweetAlertDialog.WARNING_TYPE)
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
                    new SweetAlertDialog(cameraPicture.getContext(), SweetAlertDialog.ERROR_TYPE)
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
                }
            } else if (msg.what == 1){
                String[] p  = (String[]) msg.obj;
                showDialog2(p[0], p[1], p[2]);
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
                    new SweetAlertDialog(cameraPicture.getContext(), SweetAlertDialog.ERROR_TYPE)
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
                    intent3.putExtra("imgpath", getExternalCacheDir() + "/output_image.jpg");
                    startActivity(intent3);
                    finish();
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);


        pestDection=super.findViewById(R.id.pestDetection);
        //pictureSave=super.findViewById(R.id.pictureSave);
        cameraPicture = super.findViewById(R.id.picture);
        // action bar
        final ImageView back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 创建一个File对象，用于保存摄像头拍下的图片，这里把图片命名为output_image.jpg

         //并将它存放在手机SD卡的应用关联缓存目录下
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

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
            imageUri = FileProvider.getUriForFile(this, "cn.hb.weight.fileprovider", outputImage);
            Log.d("MainActivity", outputImage.toString() + "手机系统版本高于Android7.0");
        } else {
            // 将File对象转换为Uri对象，这个Uri标识着output_image.jpg这张图片的本地真实路径
            Log.d("MainActivity", outputImage.toString() + "手机系统版本低于Android7.0");
            imageUri = Uri.fromFile(outputImage);
        }


        startCamera();

        pestDection.setOnClickListener(new Camera.pestDectionFuntion());


    }
    private void startCamera() {
        Intent intent4 = new Intent("android.media.action.IMAGE_CAPTURE");
        // 指定图片的输出地址为imageUri
        intent4.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent4, TAKE_PHOTO);

    }

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
                        uploadEnginePhaseOne.uploadToClass(getExternalCacheDir() + "/output_image.jpg");
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

                countDownTime();

                showDialog1();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
                    try {
                        // 将图片解析成Bitmap对象
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        cameraPicture.setImageBitmap(bitmap);
                        BitmapDrawable bmpDrawable = (BitmapDrawable) cameraPicture.getDrawable();

                        Bitmap bitmap2 = bmpDrawable.getBitmap();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
//                    intent2 = new Intent(getApplicationContext(), Bottom_bar.class);
//                    startActivity(intent2);
                    finish();
                }
                break;
            default: {
                intent2 = new Intent(getApplicationContext(), Bottom_bar.class);
                startActivity(intent2);
                finish();
            }
            break;
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
                                    ExifInterface exifInterface = new ExifInterface(getExternalCacheDir() + "/output_image.jpg");
                                    focal = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);
                                    Log.i("s", "-----------------focal: " + focal);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (focal == null || Integer.parseInt(focal) == 0) {
                                    focal = "27";
                                }
                                SweetAlertDialog pDialog_p1 = new SweetAlertDialog(view_par.getContext(), SweetAlertDialog.PROGRESS_TYPE);
                                pDialog_p1.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog_p1.setTitleText("请稍后！");
                                pDialog_p1.setContentText("正在进行食物识别！");
                                pDialog_p1.setCancelable(false);
                                pDialog_p1.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        do {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } while (tag == null);
                                        Message message = new Message();
                                        message.what = 1;
                                        message.obj = new String[]{A,B,focal};
                                        mHandlerPhaseOne.sendMessage(message);
                                        pDialog_p1.dismiss();
                                    }
                                }).start();
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
        // tvSelectedItemPreview = findViewById(R.id.selectedItemPreview);

        // single item array instance to store which element is selected by user initially
        // it should be set to zero meaning none of the element is selected by default
        final int[] checkedItem = {-1};

        // AlertDialog builder instance to build the alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Camera.this);

        // set the custom icon to the alert dialog
        // alertDialog.setIcon(R.mipmap.logo);

        // title of the alert dialog
        alertDialog.setTitle("请选择您所拍摄的食物名称：");

        // list of the items to be displayed to the user in the
        // form of list so that user can select the item from
        String[] listItems = new String[top3Id.size()+1];

        String[] listId = new String[top3Id.size()+1];

        String[] food_id = {""};

        int i = 0;
        for (Map.Entry<String, String> entry : top3Id.entrySet()) {
            listId[i] = entry.getKey();
            listItems[i] = entry.getValue();
            i++;
        }
        listItems[i] = "以上均不是";

        // the function setSingleChoiceItems is the function which
        // builds the alert dialog with the single item selection
        alertDialog.setSingleChoiceItems(listItems, checkedItem[0], (dialog, which) -> {
            // update the selected item which is selected by the user so that it should be selected
            // when user opens the dialog next time and pass the instance to setSingleChoiceItems method

            //返回用户选择的item下标
            checkedItem[0] = which;
            food_id[0] = listId[which];
//            // now also update the TextView which previews the selected item
//            tvSelectedItemPreview.setText("Selected Item is : " + listItems[which]);
        });

        // set the negative button if the user is not interested to select or change already selected item
        alertDialog.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });

        alertDialog.setPositiveButton("确认", (dialog, which) -> {
            if (!FunctionUtils.isFastDoubleClick()) {
                if (checkedItem[0] == top3Id.size()) {
                    Toast.makeText(getApplicationContext(), "请重新选择一张照片", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    finish();
                } else {
                    dialog.dismiss();
                    LayoutInflater inflater = getLayoutInflater();
                    View view_par = inflater.inflate(R.layout.par_dialog, null, false);
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

                            UploadEnginePhaseTwo uploadEnginePhaseTwo = new UploadEnginePhaseTwo(getApplicationContext());
                            uploadEnginePhaseTwo.uploadToAnalyze(food_id[0], tag, Double.parseDouble(focal), Double.parseDouble(A),
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
            }
        });

        // create and build the AlertDialog instance with the AlertDialog builder instance
        AlertDialog customAlertDialog = alertDialog.create();
        customAlertDialog.getWindow().setBackgroundDrawableResource(R.drawable.drawable_round_edge);

        TextView title = new TextView(this);
        title.setText("请选择您所拍摄的食物名称：");
        title.setPadding(10, 30, 10, 10);
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        customAlertDialog.setCustomTitle(title);
        customAlertDialog.setCancelable(false);
        customAlertDialog.show();
        // 获取positive按钮
        Button pos_button = (Button)customAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // 获取negative按钮
        Button neg_button = (Button)customAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pos_button.getLayoutParams();
        layoutParams.weight = 10;
        layoutParams.setMargins(30, 0, 30, 0);
        pos_button.setLayoutParams(layoutParams);
        neg_button.setLayoutParams(layoutParams);
        pos_button.setBackground(ContextCompat.getDrawable(this, R.drawable.drawable_round_edgebutton));
        pos_button.setTextColor(ContextCompat.getColor(this,R.color.white));
        neg_button.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_round_edgebutton2));
        neg_button.setTextColor(ContextCompat.getColor(this,R.color.white));
        pos_button.setTextSize(20);
        neg_button.setTextSize(20);

        // show the alert dialog when the button is clicked

        customAlertDialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/6*5), LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    CharSequence getSavedText(){
        return ((TextView)findViewById(R.id.et_1)).getText();
    }
    void setSavedText(CharSequence text){
        ((TextView)findViewById(R.id.et_1)).setText(text);
    }

    private void countDownTime() {
        //用安卓自带的CountDownTimer实现
        CountDownTimer mTimer = new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                pestDection.setText(millisUntilFinished / 1000 + 1 + "秒后可再次点击");
            }

            @Override
            public void onFinish() {
                pestDection.setClickable(true);
                pestDection.setText("开  始  分  析");
                cancel();
            }
        };
        mTimer.start();
        pestDection.setClickable(false);
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
