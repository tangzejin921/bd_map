package com.tzj.bd.map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.location.LocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.Map;

public class MapActivity extends Activity implements BaiduMap.OnMapClickListener,OnGetRoutePlanResultListener {

    private TextureMapView mMapView;
    private BaiduMap mBaidumap;


    // 搜索相关
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用


    private UtilGPS utilGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        utilGPS = new UtilGPS(this);

        mMapView = findViewById(R.id.map);
        mBaidumap = mMapView.getMap();

        //=================================
        mMapView.showZoomControls(true);
        mMapView.setZoomControlsPosition(null);
        mBaidumap.setOnMapClickListener(this);
        mBaidumap.setMyLocationEnabled(true);
        /**
         * 对定位的图标进行配置，需要MyLocationConfiguration实例，这个类是用设置定位图标的显示方式的
         */
        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        //=================================
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);


        utilGPS.addLocationListener(getClass().getSimpleName(), new LocationListener() {
            @Override
            public void onReceiveLocation(Map<String, Object> map) {
                getView(map);
            }
        }).start(5000);
    }


    private void getView(Map<String, Object> map){
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(30)
                .direction(50)// 此处设置开发者获取到的方向信息，顺时针0-360
                .latitude((double)map.get("X"))
                .longitude((double)map.get("Y"))
                .build();
        mBaidumap.setMyLocationData(locData);
        LatLng curLng = new LatLng((double)map.get("X"),(double)map.get("Y"));
        mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(curLng).build()));

        PlanNode stNode = PlanNode.withLocation(curLng);

        double latitudeDouble = (double)map.get("X")+0.01;//32.047982;
        double longitudeDouble = (double)map.get("Y")+0.01;//118.790649;
        PlanNode enNode = null;
        if (latitudeDouble != -1){
            LatLng latLng = new LatLng(latitudeDouble, longitudeDouble);
            enNode = PlanNode.withLocation(latLng);
        }else{
            enNode = PlanNode.withCityNameAndPlaceName("3201","南京");
        }
        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }
    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
    }
    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
    }
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
    }
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (isFinishing()){
            return;
        }
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapActivity.this, "抱歉，检索失败", Toast.LENGTH_SHORT).show();
        }else if (result.error == SearchResult.ERRORNO.NO_ERROR && result.getRouteLines().size() > 0) {
            try {
                if (mBaidumap!=null){
                    // 直接显示
                    RouteLine route = result.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    mBaidumap.clear();
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
    }
    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        utilGPS.stop();
        utilGPS.clear();
    }
}
