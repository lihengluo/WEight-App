package com.example.myapplication.upload;

import android.content.Context;

import com.huawei.hms.network.file.api.Result;

/**
 * Base class for implementing upload or download operations.
 *
 * @since: 2021/01/21
 */
public abstract class AUpDownloadEngine {
    long startTime = 0;
    Context context;
    EventListener listener;

    /**
     * Event listener. Events of download or upload tasks are called to the activity for display on the UI.
     */
    public interface EventListener {
        /**
         * Callback to be called when the task starts.
         */
        void onEngineStart();

        /**
         * Callback to be called during task execution.
         *
         * @param onProgress Task execution progress.
         */
        void onProgress(int onProgress);

        /**
         * Callback to be called if an error occurs during task execution.
         *
         * @param message Error message.
         */
        void onException(String message);

        /**
         * Callback to be called when the task is executed successfully.
         *
         * @param message Message indicating that the task is executed successfully.
         */
        void onSuccess(String message);
    }

    public AUpDownloadEngine(Context context) {
        this.context = context;
        //this.listener = listener;
        initManager();
    }

    /**
     * Check the result, and call the exception callback if an error occurs.
     *
     * @param result Result to be checked.
     */
    void checkResult(Result result) {
        if (result.getCode() != Result.SUCCESS) {
            listener.onException("action failed:" + result);
        }
    }

    /**
     * Initialize the task manager.
     */
    abstract void initManager();

    /**
     * Perform download.
     */
    abstract void download();

    /**
     * Pause download.
     */
    abstract void pause();

    /**
     * Resume download.
     */
    abstract void resume();

    /**
     * Cancel download.
     */
    abstract void cancel();

    /**
     * Perform put upload.
     */
    abstract void uploadForPut();

    /**
     * Perform post upload.
     */
    public abstract void uploadForPost();

}
