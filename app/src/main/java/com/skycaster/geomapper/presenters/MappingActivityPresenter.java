package com.skycaster.geomapper.presenters;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.MappingActivity;
import com.skycaster.geomapper.activity.SaveLocationActivity;
import com.skycaster.geomapper.activity.SaveMappingDataActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.MappingResultPanel;
import com.skycaster.geomapper.data.RouteRecordOpenHelper;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.interfaces.GetGeoInfoListener;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;
import com.skycaster.geomapper.models.BaiduMapModel;
import com.skycaster.geomapper.models.GPIOModel;
import com.skycaster.geomapper.models.GnggaRecordModel;
import com.skycaster.geomapper.models.LocalStorageModel;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.gps_decipher_lib.GPGGA.GPGGABean;
import com.skycaster.gps_decipher_lib.GPGGA.TbGNGGABean;
import com.skycaster.gps_decipher_lib.GPSDataExtractor;
import com.skycaster.gps_decipher_lib.GPSDataExtractorCallBack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.skycaster.geomapper.data.StaticData.REQUEST_CODE_SAVE_MAPPING_DATA;

/**
 * Created by 廖华凯 on 2017/11/1.
 */

public class MappingActivityPresenter {

    private MappingActivity mActivity;
    private BaiduMapModel mBaiduMapModel;
    private TextureMapView mMapView;
    private float mTextSize;
    private Overlay mOverlay;
    private TbGNGGABean mTbGNGGABean;
    private GPSPortDataReceiver mGPSPortDataReceiver;
    private float mRoteDegree=0;
    private float mZoomLevel=18;
    private AtomicBoolean isFirstTimeFixLocation=new AtomicBoolean(false);
    private GPSDataExtractorCallBack mDataExtractorCallBack = new GPSDataExtractorCallBack() {
        @Override
        public void onGetTBGNGGABean(TbGNGGABean tbGNGGABean) {
            mTbGNGGABean=tbGNGGABean;
            mHandler.post(mRunnableOnGetTBGNGGA);
        }

        @Override
        public void onGetGPGGABean(final GPGGABean bean) {
            super.onGetGPGGABean(bean);
            //2018/1/4 接收到GPGGA数据或GNGGA数据都更新地图
            mTbGNGGABean=new TbGNGGABean(bean.getRawString());
            mHandler.post(mRunnableOnGetTBGNGGA);
        }
    };

    private Runnable mRunnableOnGetTBGNGGA =new Runnable() {
        @Override
        public void run() {
            LatLng latLng = new LatLng(mTbGNGGABean.getLocation().getLatitude(), mTbGNGGABean.getLocation().getLongitude());
            BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(latLng);
            LatLng dummyLatlng =new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());

            //更新小灯笼
            mActivity.getLanternView().updateLantern(mTbGNGGABean.getFixQuality());

            //在地图上标出当前位置
            mBaiduMapModel.updateMyLocation(mMapView.getMap(),bdLocation);

            //在导航模式或者首次定位成功的情况下，跳到当前位置
            if(isFirstTimeFixLocation.compareAndSet(false,true)){
                mBaiduMapModel.focusToLocation(mMapView.getMap(),bdLocation,mRoteDegree,mZoomLevel);
            }else if(mActivity.isInNaviMode()){
                mBaiduMapModel.focusToLocation(mMapView.getMap(),bdLocation);
            }

            //更新底部的txt switcher
            mActivity.getTxtSwitcher().setText(mTbGNGGABean.getRawString());

            //保存GNGGA数据
            if(mGnggaRecordModel!=null&&mActivity.isSaveGpggaData()){
                try {
                    mGnggaRecordModel.write(mTbGNGGABean.getRawString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //测绘模式下，显示路径及显示测绘数据
            if(mIsMapping.get()){
                //如果真实坐标跟上一次坐标不一样，才把坐标添加到真实坐标集合中。
                if(!checkIfMappingCoordTheSameAsLast(latLng,mRealCoords)){
                    mRealCoords.add(latLng);
                }
                //如果百度坐标跟上一次坐标不一样，才更新测绘页面。
                if(!checkIfMappingCoordTheSameAsLast(dummyLatlng,mDummyCoords)){
                    mDummyCoords.add(dummyLatlng);
//                    updateMappingOverlay(mDummyCoords);

                    //先把上一次的图层清理掉
                    if(mMappingOverLays!=null){
                        for (Overlay temp:mMappingOverLays){
                            temp.remove();
                        }
                    }
//                    mMapView.getMap().clear();
                    //添加新的图层
                    mMappingOverLays = mBaiduMapModel.updateMappingOverLays(mActivity,mMapView.getMap(), mDummyCoords, true,true);
                    //更新测量结果
                    mMappingResultPanel.updateMappingResult(mDummyCoords);
                }
            }
            //// TODO: 2017/11/2 其他后续操作

        }
    };

    private GPIOModel mGpioModel;
    private LocalStorageModel mLocalStorageModel;
    private GnggaRecordModel mGnggaRecordModel;
    private Overlay mHistoryRouteOverlay;
    private AtomicBoolean mIsMapping =new AtomicBoolean(false);
    private ActionMode.Callback mActionModeCallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActivity.getMenuInflater().inflate(R.menu.menu_action_mode_mapping,menu);
            mode.setTitle("测绘模式");
            toggleMappingResultPanel(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem item = menu.findItem(R.id.action_mode_pause_or_start_mapping);
            if(mIsMapping.get()){
                item.setIcon(R.drawable.ic_pause_white_24dp);
            }else {
                item.setIcon(R.drawable.ic_play_arrow_white_24dp);
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                //开始测绘
                case R.id.action_mode_pause_or_start_mapping:
                    mIsMapping.set(!mIsMapping.get());
                    mode.invalidate();
                    break;
                //undo 上一个坐标 //这个功能也不要了，逻辑容易和测绘冲突。
//                case R.id.action_mode_delete_last_index:
//                    int size = mDummyCoords.size();
//                    if(size >1){
//                        mDummyCoords.remove(size -1);
//                        mRealCoords.remove(size-1);
//                        updateMappingOverlay(mDummyCoords);
//                    }
//                    break;
                //保存测绘数据
                case R.id.action_mode_save:
                    saveMappingDate();
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mIsMapping.set(false);
            toggleMappingResultPanel(false);
            mDummyCoords.clear();
            mMapView.getMap().clear();
            mMappingResultPanel.restoreToDefault();
        }
    };
    private int mTextPadding;
    private ArrayList<LatLng> mDummyCoords =new ArrayList<>();//转化成百度坐标的测绘轨迹
    private ArrayList<LatLng> mRealCoords=new ArrayList<>();//GPS真实坐标的测绘轨迹
    private ArrayList<Overlay> mMappingOverLays;//测绘时显示图层
    private MappingResultPanel mMappingResultPanel;
    private ViewGroup.MarginLayoutParams mLayoutParams;
    private Handler mHandler;


    /******BUG退散*******BUG退散**********BUG退散*************以上都是变量，以下才是函数*********BUG退散*******BUG退散**********BUG退散***********BUG退散**/


    public MappingActivityPresenter(MappingActivity activity) {
        mActivity = activity;
    }

    public void init(){
        mGpioModel=new GPIOModel();
        mLocalStorageModel=new LocalStorageModel();
        mBaiduMapModel=new BaiduMapModel();
        mMapView=mActivity.getMapView();
        mActivity.getMapTypeSelector().attachToMapView(mMapView);
        mBaiduMapModel.initBaiduMap(mMapView);
        mMappingResultPanel=mActivity.getMappingResultPanel();
        mHandler=new Handler();
        initActionBar();
        initTextSwitcher();
        try {
            mGpioModel.turnOnAllModulesPow();
        } catch (Exception e) {
            ToastUtil.showToast(e.getMessage());
        }

        mActivity.getWindow().setFormat(PixelFormat.TRANSLUCENT);

    }



    private void initTextSwitcher() {
        mTextSize = mActivity.getResources().getDimensionPixelSize(R.dimen.text_size_type_7)/ BaseApplication.getDisplayMetrics().scaledDensity;
        mTextPadding = (int) (mActivity.getResources().getDimensionPixelSize(R.dimen.padding_size_type_2)/ BaseApplication.getDisplayMetrics().scaledDensity+0.5f);
        mActivity.getTxtSwitcher().setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView=new TextView(mActivity);
                FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity=Gravity.CENTER;
                textView.setLayoutParams(params);
                textView.setTextSize(mTextSize);
                textView.setPadding(mTextPadding,mTextPadding,mTextPadding,mTextPadding);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.drawable.shape_black_transparent_round);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        mActivity.getTxtSwitcher().setText("GPS模块正在启动，需要1-2分钟时间，请稍候...");

    }

    private void initActionBar() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null);
        }
    }

    public void onStart() {
        mGPSPortDataReceiver =new GPSPortDataReceiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_GPS_SERIAL_PORT_DATA);
        mActivity.registerReceiver(mGPSPortDataReceiver,intentFilter);
    }

    public void onPause(){
        mMapView.onPause();
    }

    public void onResume(){
        mMapView.onResume();
    }

    public void onStop() {
        if(mGPSPortDataReceiver !=null){
            mActivity.unregisterReceiver(mGPSPortDataReceiver);
            mGPSPortDataReceiver =null;
        }
        if(mActivity.isFinishing()){
            stopRecordingGNGGP();
//            BaseApplication.removeCallBacks(mRunnableOnGetTBGNGGA);
            mHandler.removeCallbacks(mRunnableOnGetTBGNGGA);
            try {
                mGpioModel.turnOffAllModulesPow();
            } catch (Exception e) {
                ToastUtil.showToast(e.getMessage());
            }
        }

    }

    public void onDestroy(){
        mMapView.onDestroy();
    }



    /**
     * 保存最新坐标
     */
    public void saveCurrentLocation(){
        if(mTbGNGGABean==null){
            mActivity.showToast("当前无定位数据，请稍后尝试！");
        }else {
            saveLocation(mTbGNGGABean.getLocation().getLatitude(),mTbGNGGABean.getLocation().getLongitude());
        }
    }

    /**
     * 根据当前国际坐标获得地址信息，并保存到本地。
     * @param lat 经度
     * @param lng 纬度
     */
    private void saveLocation(final double lat, final double lng) {
        //百度地图不支持国际坐标，必须先把国际坐标转成百度坐标
        final BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(new LatLng(lat, lng));
        //跳到目标位置
        mBaiduMapModel.focusToLocation(mMapView.getMap(),bdLocation.getLatitude(),bdLocation.getLongitude());
        //在目标位置加上一个红色标记
        mOverlay = mBaiduMapModel.addLocationMarkAt(mMapView, bdLocation.getLatitude(), bdLocation.getLongitude());
        //显示对话框，是否保存该位置的信息
        AlertDialogUtil.showStandardDialog(
                mActivity,
                //对话框提示的坐标为国际坐标
                composeDialogMsgSaveLocation(lat,lng),
                new Runnable() {
                    @Override
                    public void run() {
                        //调用百度api时用百度坐标
                        mBaiduMapModel.getAdjacentInfoByLatlng(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()), new GetGeoInfoListener() {
                            @Override
                            public void onGetResult(ReverseGeoCodeResult result) {
                                //保存坐标数据时，要用国际坐标
                                goToSaveLocationActivity(lat,lng,result);
                                //完成操作后要把红色标记去掉。
                                mOverlay.remove();
                            }

                            @Override
                            public void onNoResult() {
                                goToSaveLocationActivity(lat,lng,null);
                                mOverlay.remove();
                            }
                        });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        mOverlay.remove();
                    }
                }

        );
    }

    /**
     * 跳到地址信息保存的页面去
     * @param lat 国际坐标经度
     * @param lng 国际坐标纬度
     * @param result 百度定义的一个类，封装了该地址的一些附带信息，可空。
     */
    private void goToSaveLocationActivity(double lat, double lng, @Nullable ReverseGeoCodeResult result) {
        com.skycaster.geomapper.bean.Location location=new com.skycaster.geomapper.bean.Location();
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setBaiduCoordinateSystem(false);
        if(result!=null){
            location.setTitle(result.getAddress());
            location.setComments(result.getBusinessCircle()+result.getSematicDescription());
        }
        SaveLocationActivity.start(mActivity, location);

    }

    /**
     * 自动生成保存位置对话框中的文字内容
     * @param lat 经度
     * @param lng 纬度
     * @return 返回对话内容的字符串
     */
    private String composeDialogMsgSaveLocation(double lat, double lng) {
        StringBuffer sb=new StringBuffer();
        sb.append(mActivity.getString(R.string.latitude))
                .append(lat)
                .append('\r').append('\n')
                .append(mActivity.getString(R.string.longitude))
                .append(lng)
                .append('\r').append('\n')
                .append(mActivity.getString(R.string.confirm_if_to_add_location_record));
        return sb.toString();
    }

    /**
     * 跳出一个测量记录的清单，点击可显示测量轨迹
     */
    public void displayHistoryRoutes() {
        AlertDialogUtil.showRouteRecords(mActivity, new RouteRecordSelectedListener() {
            @Override
            public void onRouteRecordSelected(String recordName) {
                RouteRecordOpenHelper helper=new RouteRecordOpenHelper(mActivity, recordName);
                ArrayList<LatLng> routePoints = helper.getRoutePoints();
                if(mHistoryRouteOverlay!=null){
                    mHistoryRouteOverlay.remove();
                }
                mHistoryRouteOverlay = mBaiduMapModel.addHistoryRouteOverlay(mMapView.getMap(), routePoints,false);
            }

            @Override
            public void onRouteRecordEmpty() {
                mActivity.showToast("当前无测量记录。");
            }
        });
    }

    /**
     * 进入测绘模式
     */
    public void activateMappingMode() {
        mActivity.startActionMode(mActionModeCallback);
    }

    /**
     * 切换测绘数据面板的展示或隐藏
     * @param show true为展示,false为隐藏
     */
    private void toggleMappingResultPanel(boolean show){
        int startValue;
        int endValue;
        mLayoutParams = (ViewGroup.MarginLayoutParams) mMappingResultPanel.getLayoutParams();
        if(show){
            startValue= (int) mActivity.getResources().getDimension(R.dimen.dp_minu_150);
            endValue= 0;
        }else {
            startValue= 0;
            endValue=(int) mActivity.getResources().getDimension(R.dimen.dp_minu_150);
        }
        ValueAnimator animator=ValueAnimator.ofInt(startValue,endValue);
        animator.setDuration(500).setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLayoutParams.topMargin=(int) animation.getAnimatedValue();
                mMappingResultPanel.setLayoutParams(mLayoutParams);
            }
        });
        animator.start();

    }

    /**
     * 开始记录GNGGP数据，保存到本地文件夹中
     */
    public void startRecordingGNGGP(){
        if(mLocalStorageModel.isSdCardAvailable()){
            try {
                mGnggaRecordModel=new GnggaRecordModel();
                mGnggaRecordModel.prepareDestFile(mActivity);
                mActivity.setSaveGpggaData(true);
                mActivity.supportInvalidateOptionsMenu();
            } catch (Exception e) {
                showLog("startRecordingGNGGP error");
            }
        }else {
            mActivity.showToast("侦测不到SDCard，请确认SDCard正常插入。");
        }
    }

    /**
     * 停止记录GNGGP数据
     */
    public void stopRecordingGNGGP(){
        try {
            mActivity.setSaveGpggaData(false);
            mActivity.supportInvalidateOptionsMenu();
            if(mGnggaRecordModel!=null){
                mGnggaRecordModel.stopRecording();
            }
        } catch (Exception e) {
            showLog("stopRecordingGNGGP error");
        }finally {
            mGnggaRecordModel=null;
        }
    }

    /**
     * 更新绘测界面的路径显示及测量数据
     */
    private void updateMappingOverlay(ArrayList<LatLng> latLngs) {
        showLog("begin to clear overlays");
        //先把上一次的图层清理掉
        if(mMappingOverLays!=null){
            for (Overlay temp:mMappingOverLays){
                temp.remove();
            }
        }
        showLog("clear overlays completes.");
        showLog("begin to update mapping overlays");
        //添加新的图层
        mMappingOverLays = mBaiduMapModel.updateMappingOverLays(mActivity,mActivity.getMapView().getMap(), latLngs, true,true);
        showLog("mapping overlays completes");
        //更新测量结果
        showLog("begin to update result penal");
        mMappingResultPanel.updateMappingResult(latLngs);
        showLog("result penal finished.");
    }


    /**
     * 判断最新接收到的坐标是否和现存集合中上一次的坐标一样。
     * @param latLng  最新接收到的坐标
     * @param arr 现存坐标集合
     */
    private boolean checkIfMappingCoordTheSameAsLast(LatLng latLng,ArrayList<LatLng> arr) {
        int size = arr.size();
        if(size >0){
            LatLng temp = arr.get(size - 1);
            return temp.latitude==latLng.latitude&&temp.longitude==latLng.longitude;
        }
        return false;
    }

    /**
     * 保存当前测绘数据到数据库
     */
    private void saveMappingDate() {
        if(mRealCoords.size()>1){
            ArrayList<LatLng> clone = new ArrayList<>();
            Iterator<LatLng> iterator = mRealCoords.iterator();
            while (iterator.hasNext()){
                clone.add(iterator.next());
            }
            SaveMappingDataActivity.startForResult(mActivity, REQUEST_CODE_SAVE_MAPPING_DATA,clone);
        }else {
            mActivity.showToast(mActivity.getString(R.string.not_enough_loc_points));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_SAVE_MAPPING_DATA){
            String string=null;
            if(resultCode==RESULT_OK){
                string=mActivity.getString(R.string.save_success);
            }else if(resultCode==RESULT_CANCELED){
                string=mActivity.getString(R.string.save_cancel);
            }
            if(!TextUtils.isEmpty(string)){
                mActivity.showToast(string);
            }
        }
    }

    //测试专用
    private int index=0;
    private int len=StaticData.TEST_LINES.length;
    private AtomicBoolean mIsTestMode=new AtomicBoolean(false);
    private Runnable mRunnableTest=new Runnable() {
        @Override
        public void run() {
            mDataExtractorCallBack.onGetTBGNGGABean(new TbGNGGABean(StaticData.TEST_LINES[index%len]));
            index++;
            BaseApplication.postDelay(this,1000);
        }
    };
    public void startTest() {
        mIsTestMode.set(true);
        BaseApplication.post(mRunnableTest);
    }
    public void stopTest(){
        mIsTestMode.set(false);
        BaseApplication.removeCallBacks(mRunnableTest);
    }

    /**
     * 广播接收者，接收前台服务广播出去的GPS串口数据。
     */
    private class GPSPortDataReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //避免与测试模式相冲突
            if(mIsTestMode.get()){
                return;
            }
            byte[] data = intent.getByteArrayExtra(StaticData.EXTRA_BYTES_GPS_MODULE_SERIAL_PORT_DATA);
            if(data!=null&&data.length>0){
                GPSDataExtractor.decipherData(data,data.length, mDataExtractorCallBack);
            }
        }
    }

    private void showLog(String msg){
//        Log.e(getClass().getSimpleName(),msg);
    }

}
