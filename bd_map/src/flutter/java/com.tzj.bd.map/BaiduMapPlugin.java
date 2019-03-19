package com.tzj.bd.map;

import android.app.Activity;

import com.baidu.location.LocationListener;
import com.tzj.bd.map.util.UtilGPS;

import java.lang.ref.WeakReference;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AliPayPlugin */
public class BaiduMapPlugin implements MethodCallHandler {
  private final WeakReference<Activity> mActivityWeak;
  private final UtilGPS utilGPS;
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), BaiduMapPlugin.class.getSimpleName());
    channel.setMethodCallHandler(new BaiduMapPlugin(registrar.activity()));
  }

  public BaiduMapPlugin(Activity mActivity) {
    this.mActivityWeak = new WeakReference<Activity>(mActivity);
    utilGPS = new UtilGPS(mActivity);
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    if(call.method.equals("gpsGetOnce")){
      utilGPS.getLocation(new LocationListener() {
        @Override
        public void onReceiveLocation(Map<String, Object> map) {
          if (61==map.get("errCord")||161==map.get("errCord")){
            result.success(map);
          }else{
            result.error(map.get("errCord")+"","定位失败",map);
          }
        }
      });
    }else if(call.method.equals("gpsStart")){
      int time = call.arguments();
      utilGPS.addLocationListener("gps", new LocationListener() {
        @Override
        public void onReceiveLocation(Map<String, Object> map) {
          if (61==map.get("errCord")||161==map.get("errCord")){
            result.success(map);
          }else{
            result.error(map.get("errCord")+"","定位失败",map);
          }
        }
      });
      utilGPS.start(time);
    }else if(call.method.equals("gpsStop")){
      utilGPS.stop();
      result.success(null);
    }else if(call.method.equals("clear")){
      utilGPS.clear();
      result.success(null);
    } else {
      result.notImplemented();
    }
  }
}
