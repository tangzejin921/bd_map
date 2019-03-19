package com.tzj.bd.map;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class BaiduMapApplication extends Application{
    public static Application application;
    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
    }
    public static void init(Application application){
        BaiduMapApplication.application = application;
        SDKInitializer.initialize(application.getApplicationContext());
    }

}
