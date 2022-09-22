package com.example.myapplication.function;

import android.util.Log;

import com.huawei.agconnect.function.AGCFunctionException;
import com.huawei.agconnect.function.AGConnectFunction;
import com.huawei.agconnect.function.FunctionResult;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class CloudFunction {
    private static CloudFunction function = null;

    private final AGConnectFunction mAGConnectFunction = AGConnectFunction.getInstance();

    private CloudFunction(){}

    /**
     * 获取云函数实例（调用方法：CloudFunction.getFunction()）
     * @return
     */
    public static synchronized CloudFunction getFunction() {
        if (function == null) {
            function = new CloudFunction();
        }
        return function;
    }

    /**
     * 获取当前日期和时间
     * 日期格式：yyyy-MM-dd
     * 时间格式：HH-mm-ss
     * @return 长度为2的字符串数组，0号元素表示日期，1号元素表示时间
     */
    public String[] getTime() {
        final String[] time = new String[2];

        Task<FunctionResult> task = mAGConnectFunction.wrap("get-now-time-$latest").call();
        // 完成调用前循环等待
        while (!task.isComplete())
            ;
        if (task.isSuccessful()) {
            String value = task.getResult().getValue();
            try {
                JSONObject object = new JSONObject(value);
                time[0] = (String)object.get("Date");
                time[1] = (String)object.get("Time");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Exception e = task.getException();
            if (e instanceof AGCFunctionException) {
                AGCFunctionException functionException = (AGCFunctionException) e;
                int errCode = functionException.getCode();
                String message = functionException.getMessage();
                Log.e("CloudFunction getTime", "errorCode: " + errCode + ", message: " + message);
            }
        }

        return time;
    }
}
