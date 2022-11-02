package com.example.myapplication.upload;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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
public class UploadEnginePhaseOne extends AUpDownloadEngine {
    private static final String TAG = "UploadEngine";
    UploadManager upManager;
    BodyRequest request;
    FileUploadCallback callback;
    public boolean flag;
    public int code;

    public JSONObject result;

    public UploadEnginePhaseOne(Context context) {
        super(context);
    }

    @Override
    void initManager() {
        GlobalRequestConfig commonConfig = UploadManager.newGlobalRequestConfigBuilder()
                .callTimeoutMillis(0)
                .connectTimeoutMillis(15000L)
                .pingIntervalMillis(0)
                .readTimeoutMillis(15000L)
                .writeTimeoutMillis(15000L)
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
                    JSONObject result_json = new JSONObject(response.getContent());

                    if (result_json.getInt("isfood") == -1) {
                        code = -1;
                    }
                    else if (result_json.getInt("isfood") == -2) {
                        code = -2;
                    }
                    else {
                        code = 0;
                        result = result_json;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                flag = true;
            }

            @Override
            public void onException(BodyRequest bodyRequest, NetworkException e, Response<BodyRequest, String, Closeable> response) {
                code = -3;
                flag = true;
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

    @Override
    void uploadForPut() {

    }

    @Override
    public void uploadForPost() {

    }

    public void uploadToClass(String img_path){
            try {
                Map<String, String> httpHeader = new HashMap<>();
                httpHeader.put("header1", "value1");
                httpHeader.put("header2", "value2");

                Map<String, String> httpParams = new HashMap<>();
                httpParams.put("param1", "value1");
                httpParams.put("param2", "value2");

                // replace the url for upload
                // final String normalUrl = "http://192.168.1.29:5000/uploadone";
                // final String normalUrl = "http://124.71.153.95:5000/upload";
                final String normalUrl = "http://weight.hb.cn:5000/uploadone";

                request = UploadManager.newPostRequestBuilder()
                        .url(normalUrl)
                        .fileParams("file1", new FileEntity(Uri.fromFile(new File(img_path))))
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
}
