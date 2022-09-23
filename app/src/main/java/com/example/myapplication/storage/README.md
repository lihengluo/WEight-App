### 使用样例
```java
// 获取云存储实例
CloudStorage storage = CloudStorage.getStorage();
/* 上传一张图片 */
// 将用户yyj于202-09-22日12-05-12分拍摄的图片上传到云上（云端路径为yyj/2022-09-22/12-05-12.jpg，图片的本地路径为xxx.jpg）
// 日期和时间可以通过云函数getTime获取
storage.uploadUserFile("yyj/2022-09-22/12-05-12.jpg", new File("xxx.jpg"));
/* 获取用户yyj在2022-09-22日上传的所有图片 */
// 查询用户yyj在2022-09-22日上传的文件列表
List<StorageReference> referenceList = storage.getFileList("yyj/2022-09-22/");
// 使用文件引用，将文件下载到本地
for (StorageReference rf: referenceList) {
    String createFileName = System.currentTimeMillis() + ".jpg";
    storage.downloadUserFile(rf, new File(Environment.getExternalStorageDirectory(), "Pictures/" + createFileName));
}
```