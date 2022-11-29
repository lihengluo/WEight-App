package com.example.myapplication.database;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.Goods;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.agconnect.exception.AGCException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class CloudDB {
    private static CloudDB database = null;

    private AGConnectCloudDB mCloudDB;
    private CloudDBZone mCloudDBZone;

    private CloudDB() {
        AGConnectInstance instance = AGConnectInstance.getInstance();
        mCloudDB = AGConnectCloudDB.getInstance(instance, AGConnectAuth.getInstance(instance));

        initializeTypeObject();
        openCloudDBZone();
    }

    private void initializeTypeObject() {
        // 初始化对象类型
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        } catch (AGConnectCloudDBException e) {
            Log.w("CloudDB getDatabase", "createObjectType: " + e.getMessage());;
        }
    }

    private void openCloudDBZone() {
        // 打开CloudDB zone
        CloudDBZoneConfig mConfig = new CloudDBZoneConfig("WEight",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);
        Task<CloudDBZone> openDBZoneTask = mCloudDB.openCloudDBZone2(mConfig, true);

        while (!openDBZoneTask.isComplete());

        if (!openDBZoneTask.isSuccessful()) {
            Exception e = openDBZoneTask.getException();
            if (e instanceof AGConnectCloudDBException) {
                AGConnectCloudDBException cloudDBException = (AGConnectCloudDBException) e;
                int errCode = cloudDBException.getCode();
                String message = cloudDBException.getMessage();
                Log.w("CloudDB openCloudZone", "errorCode: " + errCode + ", message: " + message);
            }
        }

        mCloudDBZone = openDBZoneTask.getResult();
//        openDBZoneTask.addOnSuccessListener(new OnSuccessListener<CloudDBZone>() {
//            @Override
//            public void onSuccess(CloudDBZone cloudDBZone) {
//                Log.i("CloudDB getDatabase", "open cloudDBZone success");
//                mCloudDBZone = cloudDBZone;
//                // Add subscription after opening cloudDBZone success
//                // addSubscription();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                Log.w("CloudDB getDatabase", "open cloudDBZone failed for " + e.getMessage());
//            }
//        });
    }

    /**
     * 查询数据库饮食记录
     * @param query 构造的查询对象
     * @return 查询结果：饮食记录列表
     */
    private List<DietRecordWithImage> queryDietRecord(CloudDBZoneQuery<DietRecordWithImage> query) {
        if (mCloudDBZone == null) {
            Log.w("CloudDB queryDietRecord", "CloudDBZone is null, try re-open it");
            return null;
        }
        Task<CloudDBZoneSnapshot<DietRecordWithImage>> queryTask = mCloudDBZone.executeQuery(query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_DEFAULT);
        while (!queryTask.isComplete());

        if (!queryTask.isSuccessful()) {
            Exception e = queryTask.getException();
            if (e instanceof AGConnectCloudDBException) {
                AGConnectCloudDBException cloudDBException = (AGConnectCloudDBException) e;
                int errCode = cloudDBException.getCode();
                String message = cloudDBException.getMessage();
                Log.e("CloudDB queryDietRecord", "errorCode: " + errCode + ", message: " + message);
            }
        }

        CloudDBZoneObjectList<DietRecordWithImage> dietRecordCursor = queryTask.getResult().getSnapshotObjects();
        List<DietRecordWithImage> dietRecordList = new ArrayList<>();
        try {
            while (dietRecordCursor.hasNext()) {
                DietRecordWithImage dietRecord = dietRecordCursor.next();
                dietRecordList.add(dietRecord);
            }
        } catch (AGConnectCloudDBException e) {
            Log.w("CloudDB queryDietRecord", "processQueryResult: " + e.getMessage());
        }

        return dietRecordList;
    }

    /**
     * 向云数据库写入一条饮食记录
     * @param dietRecord 饮食记录对象
     * @return 是否写入成功
     */
    private boolean upsertDietRecord(DietRecordWithImage dietRecord) {
        if (mCloudDBZone == null) {
            Log.w("CloudDB upsertDietRecord", "CloudDBZone is null, try re-open it");
            return false;
        }

        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(dietRecord);
        while (!upsertTask.isComplete());

        if (!upsertTask.isSuccessful()) {
            Exception e = upsertTask.getException();
            if (e instanceof AGConnectCloudDBException) {
                AGConnectCloudDBException cloudDBException = (AGConnectCloudDBException) e;
                int errCode = cloudDBException.getCode();
                String message = cloudDBException.getMessage();
                Log.e("CloudDB upsertTask", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return upsertTask.isSuccessful();
    }

    /**
     * 获取云数据库实例（调用方法：CloudDB.getDatabase()）
     * @param context 通过getApplicationContext()获取
     * @return CloudDB对象
     */
    public static synchronized CloudDB getDatabase(Context context) {
        if (database == null) {
            // 初始化CloudDB
            AGConnectCloudDB.initialize(context);
            database = new CloudDB();
        }
          return database;
    }

    public boolean upsertUserDietRecord(String uid, String date, String time, Goods foodInfo, byte[] image) {
        DietRecordWithImage dietRecord = new DietRecordWithImage();
        // 设置dietRecord对象值
        dietRecord.setUid(uid);
        dietRecord.setDate(date);
        dietRecord.setTime(time);
        dietRecord.setFoodname(foodInfo.getFoodName());
        dietRecord.setHeat(foodInfo.getHeats());
        dietRecord.setFat(foodInfo.getFat());
        dietRecord.setProtein(foodInfo.getProtein());
        dietRecord.setCarbohydrate(foodInfo.getCarbohydrates());
        dietRecord.setCa(foodInfo.getCa());
        dietRecord.setFe(foodInfo.getFe());

        dietRecord.setImage(image);

        return upsertDietRecord(dietRecord);
    }

    /**
     * 查询特定用户在特定日期的饮食记录
     * @param uid 用户id
     * @param date 日期
     * @return 饮食记录列表
     */
    public List<DietRecordWithImage> queryUserDietRecord(String uid, String date) {
        CloudDBZoneQuery<DietRecordWithImage> query = CloudDBZoneQuery.where(DietRecordWithImage.class).equalTo(
                "uid", uid).equalTo("date", date);
        return queryDietRecord(query);
    }
}
