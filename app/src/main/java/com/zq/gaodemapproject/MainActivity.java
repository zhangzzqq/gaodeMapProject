package com.zq.gaodemapproject;

import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;


/**
 * 在项目中使用地图的时候需要注意，需要合理的管理地图生命周期，这非常的重要。
 *
 */

public class MainActivity extends BaseActivity implements  LocationSource, AMapLocationListener {


    private AMapNaviView mAMapNaviView;
    private AMapNavi mAMapNavi;
    private MapView mMapView;

    //定位
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mlocationClient;
    AMapLocationClientOption mLocationOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //显示地图
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写,创建地图
        //初始化地图控制器对象
        AMap aMap = mMapView.getMap();

        aMap.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE卫星地图模式,MAP_TYPE_NIGHT晚上
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 普通地图模式

        //定位 初始化 aMap 对象

        // 设置定位监听
        aMap.setLocationSource(this);
// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
// 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);




/**
 * 高德地图导航
 *
 */
//        //获取AMapNaviView实例，并设置监听
//        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
//        mAMapNaviView.onCreate(savedInstanceState);
//        mAMapNaviView.setAMapNaviViewListener(this);
//
//
//        //获取AMapNavi实例，并设置监听
//        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
//        mAMapNavi.addAMapNaviListener(this);

    }


    //定位

    /**
     * 激活定位
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    /**
     * 在定位回调中设置显示定位小蓝点
     * @param amapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (mListener != null&&amapLocation != null) {
            if (amapLocation != null
                    &&amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }

    }

    /**
     * 地图生命周期的管理
     *
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图,销毁定位对象
        mMapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }




    /**
     * 高德地图导航
     *
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mAMapNaviView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mAMapNaviView.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mAMapNaviView.onDestroy();
//        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
//        mAMapNavi.stopNavi();
//        mAMapNavi.destroy();
//    }
//
//
//    @Override
//    public void onInitNaviSuccess() {
//        /**
//         * 方法:
//         *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
//         * 参数:
//         * @congestion 躲避拥堵
//         * @avoidhightspeed 不走高速
//         * @cost 避免收费
//         * @hightspeed 高速优先
//         * @multipleroute 多路径
//         *
//         * 说明:
//         *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
//         * 注意:
//         *      不走高速与高速优先不能同时为true
//         *      高速优先与避免收费不能同时为true
//         */
//        int strategy = 0;
//        try {
//            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //驾车路径计算
////        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
//    }
//
//
//    @Override
//    public void onCalculateRouteSuccess() {
//        //开始模拟导航
//        mAMapNavi.startNavi(NaviType.EMULATOR);
//    }


}
