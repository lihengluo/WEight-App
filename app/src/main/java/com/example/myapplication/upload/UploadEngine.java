package com.example.myapplication.upload;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

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

import java.io.Closeable;
import java.io.File;
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

    public UploadEngine(Context context) {
        super(context);
    }

    @Override
    void initManager() {
        GlobalRequestConfig commonConfig = UploadManager.newGlobalRequestConfigBuilder()
                .retryTimes(1)
                .build();
        upManager = (UploadManager) new UploadManager
                .Builder("upoloadManager")
                .commonConfig(commonConfig)
                .build(context);

        callback = new FileUploadCallback() {
            @Override
            public BodyRequest onStart(BodyRequest request) {
                Log.i(TAG, "onStart:" + request);
                listener.onEngineStart();
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
                listener.onSuccess("timeused:" + (System.currentTimeMillis() - startTime));
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
                String filePath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.png";
                String filePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/2.png";

                request = UploadManager.newPostRequestBuilder()
                        .url(normalUrl)
                        .fileParams("file1", new FileEntity(Uri.fromFile(new File(filePath1))))
                        .fileParams("file2", new FileEntity(Uri.fromFile(new File(filePath2))))
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
