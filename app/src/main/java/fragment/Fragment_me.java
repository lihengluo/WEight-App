package fragment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.myapplication.R;
import com.example.myapplication.authservice.PhoneAuth;
import com.example.myapplication.database.CloudDB;
import com.example.myapplication.database.DietRecordWithImage;
import com.example.myapplication.upload.UploadEngine;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fragment.bean.MainBean;
import fragment.bean.MenuBean;
import fragment.util.DateUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import androidx.fragment.app.Fragment;

import static android.content.ContentValues.TAG;

public class Fragment_me extends Fragment {
    private RecyclerView re_can,re_lsit;
    private MenuAdapter adapter;
    private AdapterMain adapterMain;
    private ConstraintLayout con;
    private List<MenuBean> menuList;
    private List<MainBean> mList;
    private LinearLayout lin_date;
    private TextView tv_Date;
    private int c=0;

    private PhoneAuth phoneAuth;
//    private CloudStorage storage;
    private CloudDB database;
    private int flagsear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }
    private void initView() {
        menuList = new ArrayList<>();
        mList = new ArrayList<>();
        tv_Date = getView().findViewById(R.id.tv_Date);
        re_can = getView().findViewById(R.id.re_can);
        re_lsit = getView().findViewById(R.id.re_list);
        lin_date = getView().findViewById(R.id.lin_date);
        con = getView().findViewById(R.id.con);
        con.setBackgroundResource(R.mipmap.bg);
        adapter = new MenuAdapter(R.layout.adapter_menu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        re_can.setLayoutManager(linearLayoutManager);
        re_can.setAdapter(adapter);
        initDataNew(0,0,0);
        adapter.setNewData(menuList);
        re_can.scrollToPosition(c-1);
        // handler + thread 处理post请求
        Handler mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (flagsear) {
                    case -1:
                        Toast toast1 = Toast.makeText(getContext(), "请登录后查询！", Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.CENTER, 0, 0);
                        toast1.show();
                        break;
                    case -2:
                        Toast toast2 = Toast.makeText(getContext(), "查询失败，请检查网络链接后重试！", Toast.LENGTH_SHORT);
                        toast2.setGravity(Gravity.CENTER, 0, 0);
                        toast2.show();
                        break;
                    case -3:
                        Toast toast3 = Toast.makeText(getContext(), "未查询到该日记录", Toast.LENGTH_SHORT);
                        toast3.setGravity(Gravity.CENTER, 0, 0);
                        toast3.show();
                        break;
                    case 0:
                        Toast toast4 = Toast.makeText(getContext(), "查询成功！", Toast.LENGTH_SHORT);
                        toast4.setGravity(Gravity.CENTER, 0, 0);
                        toast4.show();
                        adapterMain.setNewData( (List<MainBean>) msg.obj);
                        break;
                }

            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SweetAlertDialog pDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("请稍后！");
                pDialog.setContentText("正在查询记录！");
                pDialog.setCancelable(false);
                pDialog.show();
                for (MenuBean m : menuList){
                    m.setCheck(false);
                }
                adapter.notifyDataSetChanged();
                ((MenuBean)adapter.getData().get(position)).setCheck(true);
                adapter.notifyItemChanged(position);
                int dayOfMonth = ((MenuBean)adapter.getData().get(position)).getDay();
                int year = ((MenuBean)adapter.getData().get(position)).getYear();
                int monthOfYear = ((MenuBean)adapter.getData().get(position)).getMonth();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 0;
                        message.obj = downloadData(String.valueOf(year), String.valueOf(monthOfYear), String.valueOf(dayOfMonth));
                        mHandler.sendMessage(message);
                        pDialog.dismiss();
                    }
                }).start();
                // 将year，monthOfYear和dayOfMonth发送至云数据库进行查询
                re_lsit.scrollToPosition(0);
            }
        });

        adapterMain = new AdapterMain(R.layout.adapter_main);
        LinearLayoutManager lm = new LinearLayoutManager(this.getContext());
        re_lsit.setLayoutManager(lm);
        re_lsit.setAdapter(adapterMain);
        // 将当天的year，monthOfYear和dayOfMonth发送至云数据库进行查询
//        downloadData();

        adapterMain.setNewData(mList);

        lin_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // 弹出对话框
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(Fragment_me.this.getContext(),
                        null,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                // 确认按钮
                datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                    SweetAlertDialog pDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("请稍后！");
                    pDialog.setContentText("正在查询记录！");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    // 确认年月日
                    int year = datePickerDialog.getDatePicker().getYear();
                    int monthOfYear = datePickerDialog.getDatePicker().getMonth() + 1;
                    int dayOfMonth = datePickerDialog.getDatePicker().getDayOfMonth();
                    tv_Date.setText(year+"年"+monthOfYear+"月");
                    menuList.clear();
                    adapter.notifyDataSetChanged();
                    initDataNew(year,monthOfYear,dayOfMonth);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 0;
                            message.obj = downloadData(String.valueOf(year), String.valueOf(monthOfYear), String.valueOf(dayOfMonth));
                            mHandler.sendMessage(message);
                            pDialog.dismiss();
                        }
                    }).start();

                    re_lsit.scrollToPosition(0);
                    re_can.scrollToPosition(c-1);
//                    toast(formatDate(year, monthOfYear, dayOfMonth));

                    // 关闭dialog
                    datePickerDialog.dismiss();
                });

            }
        });
        final ImageView back = (ImageView) getView().findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }

//    private void randomData(){
//        mList.clear();
//        for (int k=0;k<10;k++){
//            mList.add(new MainBean("","猪蹄饭","测试食物"+new Random().nextInt(10),new Random().nextInt(10)*2+"大卡",new Random().nextInt(10)*5+"%",new Random().nextInt(10)*3+"克"));
//        }
//        adapterMain.setNewData(mList);
//    }

    /* 保留两位小数 */
    private float decimalTwo(float f) {
        return Math.round(f * 100) / 100f;
    }

    private List<MainBean> downloadData(String year, String month, String day){
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
//        }
        mList.clear();
        month = month.length()<2 ? "0"+month : month;
        day = day.length()<2 ? "0"+day : day;
        String date = year + "-" + month + "-" + day;
        phoneAuth = new PhoneAuth();
        database = CloudDB.getDatabase(getContext());
        if (!phoneAuth.isUserSignIn()) {
            flagsear = -1;//未登录
            return null;
        }
        String uid = phoneAuth.getCurrentUserUid();
        List<DietRecordWithImage> dietRecordList = database.queryUserDietRecord(uid, date);

        if (dietRecordList == null) {
            flagsear = -2;//网络问题
            return null;
        }
        if (dietRecordList.size()==0) {
            flagsear = -3;//当天没有记录
            return null;

        }


        for (int k = 0; k < dietRecordList.size(); k++){
            DietRecordWithImage dietRecord = dietRecordList.get(k);

            String createFileName = System.currentTimeMillis() + ".jpg";
            File file = new File(getActivity().getExternalCacheDir(), createFileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(dietRecord.getImage(), 0, dietRecord.getImage().length);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }  catch (IOException e) {
                e.printStackTrace();
            }

            mList.add(new MainBean(getActivity().getExternalCacheDir() + "/" + createFileName,
                    dietRecord.getFoodname(), String.valueOf(decimalTwo(dietRecord.getHeat())), String.valueOf(decimalTwo(dietRecord.getCarbohydrate())),
                    String.valueOf(decimalTwo(dietRecord.getProtein())), String.valueOf(decimalTwo(dietRecord.getFat())),
                    String.valueOf(decimalTwo(dietRecord.getCa())), String.valueOf(decimalTwo(dietRecord.getFe()))));
        }
        flagsear = 0;
        return mList;
    }

    private void initDataNew(int cyear,int cmonth,int cday) {
        int s = 0,e=0,year=0,month=0;
        Calendar cal = Calendar.getInstance();
        c = cal.get(Calendar.DAY_OF_MONTH);
        if (cyear==0){
            cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,0);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH)+1;
        }else {
            cal.set(cyear,cmonth-1,0);
            c = cday;
            year = cyear;
            month = cmonth;
        }
        tv_Date.setText(year+"年"+month+"月");
        cal.set(Calendar.DAY_OF_MONTH, 1);
        s = cal.get(Calendar.DAY_OF_MONTH);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        e = cal.get(Calendar.DAY_OF_MONTH);
        for (int i=s;i<=e;i++){
            if (c==i){
                menuList.add(new MenuBean(DateUtil.dateToWeek(year+"-"+month+"-"+i), year, i, month, true));
            }else {
                menuList.add(new MenuBean(DateUtil.dateToWeek(year+"-"+month+"-"+i), year, i, month, false));
            }
        }

    }

    private void initData() {
        int s = 0,e=0,year=0,month=0;
        Calendar cal = Calendar.getInstance();
        c = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        s = cal.get(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        Log.e("======",cal.get(Calendar.DAY_OF_MONTH)+"");
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        e = cal.get(Calendar.DAY_OF_MONTH);
        Log.e("======",cal.get(Calendar.DAY_OF_MONTH)+"");
        for (int i=s;i<=e;i++){
            if (c==i){
                menuList.add(new MenuBean(DateUtil.dateToWeek(year+"-"+month+"-"+i), year, c, month, true));
            }else {
                menuList.add(new MenuBean(DateUtil.dateToWeek(year+"-"+month+"-"+i), year, c, month, false));
            }
        }

    }
}