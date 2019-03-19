package com.tzj.bd.map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.tzj.bd.map.entity.Point;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 62 ： 扫描整合定位依据失败。此时定位结果无效。\n
 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。\n
 65 ： 定位缓存的结果。\n
 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果\n
 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果\n
 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果\n
 161： 表示网络定位结果\n
 162~167： 服务端定位失败\n
 502：key参数错误\n
 505：key不存在或者非法\n
 601：key服务被开发者自己禁用\n
 602：key mcode不匹配\n
 501～700：key验证失败\n

 <uses-permission android:name="android.permission.WRITE_CONTACTS" />
 <uses-permission android:name="android.permission.WRITE_SETTINGS" />
 <uses-permission android:name="android.permission.CHANGE_CONFIGURATION" />
 *
 */

public class UtilGPS {
    private LocationClient mLocationClient;
    private BDLocationListener listener;
    private Map<String,LocationListener> mapListener = new HashMap<>();

    public UtilGPS(Context ctx) {
        mLocationClient = new LocationClient(ctx.getApplicationContext());
        mLocationClient.registerLocationListener(listener=new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation arg0) {
                Set<Map.Entry<String,LocationListener>> entrySet = mapListener.entrySet();
                for (Map.Entry<String,LocationListener> l:entrySet) {
                    l.getValue().onReceiveLocation(getHashMap(arg0));
                }
                if (isGetLocation) {
                    isGetLocation = false;
                    mapListener.remove("getLocation");
                    if (!isStart) {
                        HashMap hashMap = new HashMap(mapListener);
                        stop();
                        mapListener.putAll(hashMap);
                    }
                }
            }
        });
    }

    /**
     * 注册监听
     */
    public UtilGPS addLocationListener(String name,LocationListener bdLocationListener){
        mapListener.put(name, bdLocationListener);
        return this;
    }
    /**
     * 取消注册监听
     */
    public UtilGPS removeLocationListener(String name){
        mapListener.remove(name);
        return this;
    }


    boolean isStart = false;
    /**
     * 开始定位(多次)
     * @return: void
     */
    public void start(int time){
        isStart = true;
        InitLocation(time);
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.start();
//			Mlog.running("start");
        }
    }

    boolean isGetLocation = false;
    /**
     *请求定位(一次)
     *如里已开启定位将不作操作
     *好像会回调两次不知什么原因
     * @return: void
     */
    public void getLocation(LocationListener bdLocationListener){
        addLocationListener("getLocation", bdLocationListener);
        isGetLocation = true;
        InitLocation(5000);
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.start();
//			mLocationClient.requestLocation();
        }
    }


    /**
     * 停止定位
     * @return: void
     */
    public void stop(){
        isStart = false;
        mapListener.clear();
        if (mLocationClient !=null) {
            if(mLocationClient.isStarted()){
                mLocationClient.stop();
            }
        }
    }

    public void clear(){
        stop();
        mapListener = null;
        if (mLocationClient !=null) {
            mLocationClient.unRegisterLocationListener(listener);
            mLocationClient = null;
        }
    }

    /**
     * errCord	61--GPS,161--net
     * @param location
     * @return
     * @return: Map<String,Object>
     */
    public static Map<String, Object> getHashMap(BDLocation location){
        Map<String, Object> map = new HashMap<>();
        if (location == null) {
            return map;
        }
        map.put("time", location.getTime());
        map.put("errCord", location.getLocType());//61--GPS,161--net
        map.put("X", location.getLatitude());
        map.put("Y", location.getLongitude());
        map.put("R", location.getRadius());
        map.put("addrStr", location.getAddrStr());
        map.put("cityCode",location.getCityCode());
        map.put("city",location.getCity());
        return map;
    }

    public static double distance(Point p1,Point p2){
        return DistanceUtil.getDistance(p1.getLatLng(),p2.getLatLng());
    }
    public static String guide(Object cx,Object cy,Object x,Object y,String name){
        String appName = BaiduMapApplication.application.getResources().getString(BaiduMapApplication.application.getApplicationInfo().labelRes);
        StringBuffer mapUrl = new StringBuffer("http://api.map.baidu.com/direction?origin=");
        mapUrl.append("latlng:");
        mapUrl.append(cx+"");
        mapUrl.append(",");
        mapUrl.append(cy+"");
        mapUrl.append("|name:我");
        mapUrl.append("&destination=");
        mapUrl.append("latlng:");
        mapUrl.append(x);
        mapUrl.append(",");
        mapUrl.append(y);
        mapUrl.append("|name:");
        mapUrl.append(name);
        mapUrl.append("&mode=driving&region=南京&output=html&src=");//南京写死了
        mapUrl.append("健康无忧");
        mapUrl.append("|");
        mapUrl.append(appName);
        return mapUrl.toString();
    }
    //===========================================
    /**
     * 初始化参数
     * @param time 这个小于100将定位一次
     * @return: void
     */
    private void InitLocation(int time){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式//LocationMode.Hight_Accuracy;
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度还是?"gcj02";"bd09ll";"bd09";
        option.setScanSpan(time);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//要不要反地理
        option.setOpenGps(true);// 打开gps
        mLocationClient.setLocOption(option);
    }



    //===============================================================
	public static BitmapDescriptor mPointMarker;//定位图标


	/**
	 * 标记
	 */
	public static void addText(BaiduMap mMap, double x, double y, String text){
		if (mPointMarker == null) {
			mPointMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);// 修改为自定义marker
		}
		LatLng ll = new LatLng(x, y);

		OverlayOptions ooText = new TextOptions().bgColor(0x00000000).fontSize(26).fontColor(0x88ff0000)
				.text(text+"").rotate(0).position(ll).zIndex(1);
		mMap.addOverlay(ooText);
		OverlayOptions ooPoint = new MarkerOptions().position(ll).icon(mPointMarker).zIndex(2).draggable(true);
		mMap.addOverlay(ooPoint);
	}

	/**
	 * 清除标记
	 */
	public static void deleteDrawView(MapView mMapView){
		if (mMapView!=null) {
			mMapView.getMap().clear();
		}
	}

	//=======================================================

    /**
     *
     */
    public static String getMapUrl(String selfLatitude, String selfLongitude,
                                          String dirLatitude, String dirLongitude,
                                          String target) {
        String appName = getAppName(BaiduMapApplication.application);
        StringBuffer mapUrl = new StringBuffer();
        //http://api.map.baidu.com/direction?
        // origin=latlng:32.091050,118.789630|name:我
        // &destination=latlng:32.054219,118.775991|name:南京市胸科医院
        // &mode=driving&region=南京&output=html&src=健康无忧|健康南京

        mapUrl.append("http://api.map.baidu.com/direction?origin=");
        mapUrl.append("latlng:");
        mapUrl.append(selfLatitude);
        mapUrl.append(",");
        mapUrl.append(selfLongitude);
        mapUrl.append("|name:我");
        mapUrl.append("&destination=");
        mapUrl.append("latlng:");
        mapUrl.append(dirLatitude);
        mapUrl.append(",");
        mapUrl.append(dirLongitude);
        mapUrl.append("|name:");
        mapUrl.append(target);
        mapUrl.append("&mode=driving&region=南京&output=html&src=");
        mapUrl.append(appName);
        mapUrl.append("|");
        mapUrl.append(appName);

        return mapUrl.toString();
    }

    /**
     *
     */
    public static String getMapUrl(String latitude, String longitude, String target) {
        String appName = getAppName(BaiduMapApplication.application);
        StringBuffer mapUrl = new StringBuffer();
        mapUrl.append("http://api.map.baidu.com/marker?location=");
        mapUrl.append(latitude);
        mapUrl.append(",");
        mapUrl.append(longitude);
        mapUrl.append("&title=");
        mapUrl.append(target);
        mapUrl.append("&content=");
        mapUrl.append(target);
        // mapUrl.append("&output=html&src=src=yourComponyName|yourAppName");
        mapUrl.append("&output=html&src=src=");
        mapUrl.append(appName);
        mapUrl.append("|");
        mapUrl.append(appName);
        return mapUrl.toString();
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
