package com.example.guoca.can_app;

import android.app.Application;

import com.example.guoca.can_app.RequestHttp.HttpsUtil;
import com.mob.MobSDK;

/**
 * Created by Guoca on 2019/8/3.
 */

public class MyApplication extends Application {
    private static MyApplication mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        // 启动短信验证sdk
        MobSDK.init(this,"2b9fa6c550690","71b85991f8d7c8bf34352332d66826c5");
        HttpsUtil.initHttpsUrlConnection(this);
        mApplication=this;
    }
    public static MyApplication getInstance(){
        return mApplication;
    }
}
