### 使用样例
#### 获取云数据库实例
CloudDB database = CloudDB.getDatabase(getApplicationContext());
#### 添加一条饮食记录
Goods good = new Goods("0", "hhc", 0, 1, 2, 3, 4, 5);
database.upsertUserDietRecord("yyj", "2022-09-22", "12-05-30", good);
#### 查询id为yyj的用户在2022-09-22日的饮食记录
List<DietRecord> dietRecordList = database.queryUserDietRecord("yyj", "2022-09-22");