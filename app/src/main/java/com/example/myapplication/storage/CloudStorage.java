package com.example.myapplication.storage;

import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.DownloadTask;
import com.huawei.agconnect.cloud.storage.core.ListResult;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.Tasks;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CloudStorage {
    private static CloudStorage storage = null;

    private final AGCStorageManagement mAGCStorageManagement = AGCStorageManagement.getInstance();

    private CloudStorage(){}

    private boolean uploadFile(StorageReference reference, File file) {
        UploadTask task = reference.putFile(file);
        return task.isSuccessful();
    }

    private boolean downloadFile(StorageReference reference, File file) {
        DownloadTask task = reference.getFile(file);
        return task.isSuccessful();
    }

    private StorageReference getFileReference(String path) {
        return mAGCStorageManagement.getStorageReference(path);
    }

    /**
     * 获取云存储实例（调用方法：CloudStorage.getStorage()）
     * @return 一个CloudStorage对象
     */
    public static synchronized CloudStorage getStorage() {
        if (storage == null) {
            storage = new CloudStorage();
        }
        return storage;
    }

    /**
     * 将本地文件file上传到云端cloudPath处
     * @param cloudPath 文件上传存储路径(建议为：$(uid)/$(date)/$(time).jpg)
     * @param file 本地文件
     * @return 文件是否上传成功，true：成功；false：失败
     */
    public boolean uploadUserFile(String cloudPath, File file) {
        StorageReference reference = getFileReference(cloudPath);
        return uploadFile(reference, file);
    }

    /**
     * 将云端cloudPath处的文件下载到file
     * @param cloudPath 云端文件存储路径
     * @param file 待写入的本地文件
     * @return 文件是否下载成功，true：成功；false：失败
     */
    public boolean downloadUserFile(String cloudPath, File file) {
        StorageReference reference = getFileReference(cloudPath);
        return downloadFile(reference, file);
    }

    /**
     * 将云端文件下载到本地
     * @param reference 云端文件引用
     * @param file 待写入的本地文件
     * @return 文件是否下载成功，true：成功；false：失败
     */
    public boolean downloadUserFile(StorageReference reference, File file) {
        return downloadFile(reference, file);
    }

    /**
     * 获取指定存储目录下的所有文件
     * @param cloudDir 云端存储目录
     * @return 文件引用列表
     */
    public List<StorageReference> getFileList(String cloudDir) {
        StorageReference dirReference  = getFileReference(cloudDir);
        Task<ListResult> listTask = dirReference.listAll();
        while (!listTask.isComplete());
        ListResult listResult = listTask.getResult();

        return listResult.getFileList();
    }
}