### 使用样例
CloudStorage storage = CloudStorage.getStorage();
List<StorageReference> referenceList = storage.getFileList("yyj/2022-09-22/");
for (StorageReference rf: referenceList) {
    String createFileName = System.currentTimeMillis() + ".jpg";
    storage.downloadUserFile(rf, new File(Environment.getExternalStorageDirectory(), "Pictures/" + createFileName));
}
