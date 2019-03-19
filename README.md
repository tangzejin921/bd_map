# 百度

## 定位网址
>https://lbsyun.baidu.com/index.php?title=android-locsdk/guide/create-project/android-studio
## 地图网址
>https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-project/androidstudio
## 下载 SDK
## 导入SDK 
    implementation files('libs/BaiduLBS_Android.jar')
## 添加AK
    <meta-data
        android:name="com.baidu.lbsapi.API_KEY"
        android:value="开发者申请的AK" >
    </meta-data>
## 添加服务
    <service android:name="com.baidu.location.f" android:enabled="true" android:process=":remote"> </service>
## 权限
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <!-- 获取设备网络状态，禁用后无法获取网络状态-->
    <uses-permission android:name="android.permission.INTERNET"/>
    <!--//网络权限，当禁用后，无法进行检索等相关业务-->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <!--//读取设备硬件信息，统计数据-->
    <uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" />
    <!--//读取系统信息，包含系统版本等信息，用作统计-->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <!--//获取设备的网络状态，鉴权所需网络代理-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <!--//允许sd卡写权限，需写入地图数据，禁用后无法显示地图-->
    <uses-permission android:name="android.permission.WRITE_SETTINGS"
        tools:ignore="ProtectedPermissions" />
    <!--//获取统计数据-->
    <uses-permission android:name="android.permission.GET_TASKS" />
    <!--//鉴权所需该权限获取进程列表-->
    <uses-permission android:name="android.permission.CAMERA" />

    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
    <uses-permission android:name="android.permission.CHANGE_CONFIGURATION"
        tools:ignore="ProtectedPermissions" />
        
## 混淆
    -keep class com.baidu.** {*;}
    -keep class mapsdkvi.com.** {*;}    
    -dontwarn com.baidu.**