package fragment;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.myapplication.database.DietRecord;
import com.example.myapplication.storage.CloudStorage;
import com.huawei.agconnect.cloud.storage.core.StorageReference;

import fragment.bean.MainBean;
import fragment.bean.MenuBean;
import fragment.util.DateUtil;

import java.io.File;
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
    private CloudStorage storage;
    private CloudDB database;

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
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (MenuBean m : menuList){
                    m.setCheck(false);
                }
                adapter.notifyDataSetChanged();
                ((MenuBean)adapter.getData().get(position)).setCheck(true);
                adapter.notifyItemChanged(position);
                int dayOfMonth = ((MenuBean)adapter.getData().get(position)).getDay();
                int year = ((MenuBean)adapter.getData().get(position)).getYear();
                int monthOfYear = ((MenuBean)adapter.getData().get(position)).getMonth();

                downloadData(year + "-" + monthOfYear + "-" + dayOfMonth);
                // 将year，monthOfYear和dayOfMonth发送至云数据库进行查询
                re_lsit.scrollToPosition(0);
            }
        });

        adapterMain = new AdapterMain(R.layout.adapter_main);
        LinearLayoutManager lm = new LinearLayoutManager(this.getContext());
        re_lsit.setLayoutManager(lm);
        re_lsit.setAdapter(adapterMain);
        //setData();
        // 将当天的year，monthOfYear和dayOfMonth发送至云数据库进行查询

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
                    // 确认年月日
                    int year = datePickerDialog.getDatePicker().getYear();
                    int monthOfYear = datePickerDialog.getDatePicker().getMonth() + 1;
                    int dayOfMonth = datePickerDialog.getDatePicker().getDayOfMonth();
                    tv_Date.setText(year+"年"+monthOfYear+"月");
                    menuList.clear();
                    adapter.notifyDataSetChanged();
                    initDataNew(year,monthOfYear,dayOfMonth);
                    downloadData(year + "-" + monthOfYear + "-" + dayOfMonth);

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

    private void downloadData(String date){
        phoneAuth = new PhoneAuth();
        database = CloudDB.getDatabase(getContext());
        storage = CloudStorage.getStorage();
        if (!phoneAuth.isUserSignIn()) {
            Toast toast = Toast.makeText(getContext(), "请登录后查询！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        String uid = phoneAuth.getCurrentUserUid();
        List<StorageReference> referenceList = storage.getFileList(uid + "/" + date);
        List<DietRecord> dietRecordList = database.queryUserDietRecord(uid, date);

        if (dietRecordList == null || referenceList == null) {
            Toast toast = Toast.makeText(getContext(), "查询失败，请重试！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        assert (referenceList.size() == dietRecordList.size());

        mList.clear();
        for (int k = 0; k < referenceList.size(); k++){
            DietRecord dietRecord = dietRecordList.get(k);
            StorageReference reference = referenceList.get(k);
            String createFileName = System.currentTimeMillis() + ".jpg";
            storage.downloadUserFile(reference, new File(Environment.getDownloadCacheDirectory(), createFileName));
            mList.add(new MainBean(Environment.getDownloadCacheDirectory() + "/" + createFileName,
                    dietRecord.getFoodname(), dietRecord.getHeat()+"大卡", dietRecord.getCarbohydrate()+"克",
                    dietRecord.getProtein()+"克", dietRecord.getFat()+"克", dietRecord.getCa()+"毫克", dietRecord.getFe()+"毫克"));
        }
        adapterMain.setNewData(mList);
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
        tv_Date.setText(year+"年"+year+"月");
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