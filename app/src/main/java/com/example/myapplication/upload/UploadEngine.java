package com.example.myapplication.upload;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.myapplication.Goods;
import com.huawei.hms.network.file.api.GlobalRequestConfig;
import com.huawei.hms.network.file.api.Progress;
import com.huawei.hms.network.file.api.Response;
import com.huawei.hms.network.file.api.Result;
import com.huawei.hms.network.file.api.exception.InterruptedException;
import com.huawei.hms.network.file.api.exception.NetworkException;
import com.huawei.hms.network.file.upload.api.BodyRequest;
import com.huawei.hms.network.file.upload.api.FileEntity;
import com.huawei.hms.network.file.upload.api.FileUploadCallback;
import com.huawei.hms.network.file.upload.api.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation class for upload-related operations.
 *
 * @since: 2021/01/21
 */
public class UploadEngine extends AUpDownloadEngine {
    private static final String TAG = "UploadEngine";
    UploadManager upManager;
    BodyRequest request;
    FileUploadCallback callback;
    public boolean flag;
    public Goods Good;

    public UploadEngine(Context context) {
        super(context);
    }

    @Override
    void initManager() {
        GlobalRequestConfig commonConfig = UploadManager.newGlobalRequestConfigBuilder()
                .callTimeoutMillis(9999999999999L)
                .connectTimeoutMillis(9999999999L)
                .pingIntervalMillis(9999999999999L)
                .readTimeoutMillis(9999999999999L)
                .writeTimeoutMillis(9999999999999L)
                .retryTimes(1)
                .build();
        upManager = (UploadManager) new UploadManager
                .Builder("upoloadManager")
                .commonConfig(commonConfig)
                .build(context);

        callback = new FileUploadCallback() {
            @Override
            public BodyRequest onStart(BodyRequest request) {
                flag = false;
                Log.i(TAG, "onStart:" + request);
                //listener.onEngineStart();
                startTime = System.currentTimeMillis();
                return request;
            }

            @Override
            public void onProgress(BodyRequest request, Progress progress) {
                Log.i(TAG, "onProgress:" + progress);
                listener.onProgress(progress.getProgress());
            }

            @Override
            public void onSuccess(Response<BodyRequest, String, Closeable> response) {
                Log.i(TAG, "onSuccess:" + response.getContent());
                //listener.onSuccess("timeused:" + (System.currentTimeMillis() - startTime));
                try {
                    if ( Integer.valueOf(response.getContent()) == -1) {
                        Log.i(TAG, "The object is null!!!!");
                        Good = null;
                    }
                    else {
                        JSONObject result_json = new JSONObject(response.getContent());
                        Good = new Goods(result_json.getString("food_id"), result_json.getString("food_label"),
                                (float) result_json.getDouble("energy"), (float) result_json.getDouble("fat"),
                                (float) result_json.getDouble("protein"), (float) result_json.getDouble("carbohydrates"),
                                (float) result_json.getDouble("ca"), (float) result_json.getDouble("fe"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                flag = true;
            }

            @Override
            public void onException(BodyRequest bodyRequest, NetworkException e, Response<BodyRequest, String, Closeable> response) {
                if (e instanceof InterruptedException) {
                    String errorMsg = "upload onException for canceled";
                    Log.w(TAG, errorMsg);
                    listener.onException(errorMsg);
                } else {
                    String errorMsg = "upload exception for request:" + bodyRequest.getId() +
                            "\n\ndetail : " + e.getMessage();
                    if (e.getCause() != null) {
                        errorMsg += " , cause : " +
                                e.getCause().getMessage();
                    }
                    Log.e(TAG, errorMsg);
                    listener.onException(errorMsg);
                }
            }
        };
    }

    @Override
    void download() {
    }

    @Override
    void pause() {
    }

    @Override
    void resume() {
    }

    @Override
    void cancel() {
        if (upManager == null) {
            Log.e(TAG, "nothing to cancel");
            return;
        }
        if (request == null) {
            if (listener != null) {
                listener.onException("request is null!");
            }
            return;
        }
        Result result = upManager.cancelRequest(request.getId());
        checkResult(result);
    }

    public void uploadToDetect(String img_path, double focal_d, double plate_d, double obj2cam_d){
            try {
                Map<String, String> httpHeader = new HashMap<>();
                httpHeader.put("header1", "value1");
                httpHeader.put("header2", "value2");

                Map<String, String> httpParams = new HashMap<>();
                httpParams.put("param1", "value1");
                httpParams.put("param2", "value2");

                // replace the url for upload
                final String normalUrl = "http://192.168.1.29:5000/upload";
                // upload file for http post
                // replace the file path
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fd", focal_d);
                jsonObject.put("pd", plate_d);
                jsonObject.put("od", obj2cam_d);
                String jsonString = jsonObject.toString();
                String tmp_path =  context.getExternalCacheDir() + "/tmp.txt";
                File f = new File(tmp_path);
                FileWriter fw=new FileWriter(f);
                fw.write(jsonString);
                fw.close();

                request = UploadManager.newPostRequestBuilder()
                        .url(normalUrl)
                        .fileParams("file1", new FileEntity(Uri.fromFile(new File(img_path))))
                        .fileParams("file2", new FileEntity(Uri.fromFile(new File(tmp_path))))
                        .params(httpParams)
                        .headers(httpHeader)
                        .build();

                if (upManager == null) {
                    Log.e(TAG, "nothing to cancel");
                    return;
                }

                Result result = upManager.start(request, callback);
                checkResult(result);
                Log.i(TAG, "Result:" + result.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "exception:" + e.getMessage());
            }
    }

    @Override
    void uploadForPut() {
        testUpload(true);
    }

    @Override
    public void uploadForPost() {
        testUpload(false);
    }

    public void testUpload(boolean usePut) {
        try {
            Map<String, String> httpHeader = new HashMap<>();
            httpHeader.put("header1", "value1");
            httpHeader.put("header2", "value2");

            Map<String, String> httpParams = new HashMap<>();
            httpParams.put("param1", "value1");
            httpParams.put("param2", "value2");

            // replace the url for upload
            final String normalUrl = "http://192.168.1.29:5000/upload";
            if (usePut) {
                // upload file for http put
                List<FileEntity> fileList = new ArrayList<>();
                // replace the file path
                String filePath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.png";
                fileList.add(new FileEntity(Uri.fromFile(new File(filePath1))));

                request = UploadManager.newPutRequestBuilder()
                        .url(normalUrl)
                        .fileParams(fileList)
                        .params(httpParams)
                        .headers(httpHeader)
                        .build();
            } else {
                // upload file for http post
                // replace the file path
                String filePath1 = Environment.getExternalStorageDirectory() + "/Pictures/3.jpg";
                String filePath2 = Environment.getExternalStorageDirectory() + "/2.png";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fd", 24);
                jsonObject.put("pd", 0.32);
                jsonObject.put("od", 0.6);
                String jsonString = jsonObject.toString();
                File f = new File(Environment.getExternalStorageDirectory() + "/tmp.txt");
                FileWriter fw=new FileWriter(f);
                fw.write(jsonString);
                fw.close();

                request = UploadManager.newPostRequestBuilder()
                        .url(normalUrl)
                        .fileParams("file1", new FileEntity(Uri.fromFile(new File(filePath1))))
                        .fileParams("file2", new FileEntity(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/tmp.txt"))))
                        .params(httpParams)
                        .headers(httpHeader)
                        .build();
            }

            if (upManager == null) {
                Log.e(TAG, "nothing to cancel");
                return;
            }

            Result result = upManager.start(request, callback);
            checkResult(result);
        } catch (Exception e) {
            Log.e(TAG, "exception:" + e.getMessage());
        }
    }
}
