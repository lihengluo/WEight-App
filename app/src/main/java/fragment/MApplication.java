package fragment;


import android.app.Application;
import android.content.Context;


/**
 * @Author : qiangyu
 * @Date : on 2022-11-09 14:28.
 * @Description :描述
 */
public class MApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
