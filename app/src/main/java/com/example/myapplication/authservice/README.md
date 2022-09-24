### 使用样例
#### 初始化认证对象
```java
PhoneAuth phoneAuth = new PhoneAuth();
```
#### 注册流程（注册后自动登录成功）
```java
// 从UI组件中获取用户手机号或邮箱
/* ...
*/

// 发送验证码
phoneAuth.requestVerifyCode("182xxxx7778", getApplicationContext());

// 从UI组件中获取用户输入的验证码
/* ...
*/

// 新建用户账户（A为验证码），password为可选项
phoneAuth.createUser("182xxxx7778", A, "password");
phoneAuth.createUser("182xxxx7778", A);
```

#### 登录流程
##### 使用密码登录
```java
phoneAuth.signInWithPassword("182xxxx7778", "password");
```
##### 使用验证码登录
phoneAuth.signInWithVerifyWord("182xxxx7778", "verifyCode");

#### 登出流程
```java
phoneAuth.signOut();
```

#### 判断用户是否已经登录
```java
boolean userIn = phoneAuth.isUserSignIn();
```

#### 注销流程
```java
phoneAuth.deleteUser();
```