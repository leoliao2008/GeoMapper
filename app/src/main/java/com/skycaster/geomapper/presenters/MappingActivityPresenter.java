package com.skycaster.geomapper.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
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
import com.skycaster.inertial_navi_lib.GPGGABean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;
import com.skycaster.inertial_navi_lib.NaviDataExtractorCallBack;
import com.skycaster.inertial_navi_lib.TbGNGGABean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.skycaster.geomapper.data.StaticData.REQUEST_CODE_SAVE_MAPPING_DATA;
import static com.skycaster.geomapper.util.ImageUtil.showLog;

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
    private BeidouPortDataReceiver mBeidouPortDataReceiver;
    private float mRoteDegree=0;
    private float mZoomLevel=18;
    private AtomicBoolean isFirstTimeFixLocation=new AtomicBoolean(false);
    private NaviDataExtractorCallBack mDataExtractorCallBack = new NaviDataExtractorCallBack() {
        @Override
        public void onGetTBGNGGABean(TbGNGGABean tbGNGGABean) {
            mTbGNGGABean=tbGNGGABean;
            BaseApplication.post(mRunnableOnGetTBGNGGA);
        }

        @Override
        public void onGetGPGGABean(final GPGGABean bean) {
            super.onGetGPGGABean(bean);
            BaseApplication.post(new Runnable() {
                @Override
                public void run() {
                    mActivity.getTxtSwitcher().setText(bean.getRawGpggaString());
                }
            });
        }
    };
    private Runnable mRunnableOnGetTBGNGGA =new Runnable() {
        @Override
        public void run() {
            LatLng latLng = new LatLng(mTbGNGGABean.getLocation().getLatitude(), mTbGNGGABean.getLocation().getLongitude());

            //更新小灯笼
            mActivity.getLanternView().updateLantern(mTbGNGGABean.getFixQuality());

            //在导航模式或者首次定位成功的情况下，更新当前位置并跳到该位置
            if(isFirstTimeFixLocation.compareAndSet(false,true)){
                BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(latLng);
                mBaiduMapModel.updateMyLocation(mMapView.getMap(),bdLocation);
                mBaiduMapModel.focusToLocation(mMapView.getMap(),bdLocation,mRoteDegree,mZoomLevel);
            }else if(mActivity.isInNaviMode()){
                BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(latLng);
                mBaiduMapModel.updateMyLocation(mMapView.getMap(),bdLocation);
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
                //如果跟上一次坐标不一样，才更新测绘页面。
                if(!checkIfMappingCoordTheSameAsLast(latLng)){
                    mMapppingCoords.add(latLng);
                    updateMappingOverlay(mMapppingCoords);
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
//            toggleMappingResultPanel(true);
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
                //undo 上一个坐标
                case R.id.action_mode_delete_last_index:
                    int size = mMapppingCoords.size();
                    if(size >1){
                        mMapppingCoords.remove(size -1);
                        updateMappingOverlay(mMapppingCoords);
                    }
                    break;
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
//            toggleMappingResultPanel(false);
            mMapppingCoords.clear();
            if(mMappingOverLays!=null){
                for(Overlay temp:mMappingOverLays){
                    temp.remove();
                }
            }
            mMappingResultPanel.restoreToDefault();
        }
    };
    private int mTextPadding;
    private ArrayList<LatLng> mMapppingCoords=new ArrayList<>();//测绘轨迹
    private ArrayList<Overlay> mMappingOverLays;//测绘时显示图层
    private MappingResultPanel mMappingResultPanel;





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
        mBaiduMapModel.setOnMapStatusChangeListener(mMapView, new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                mZoomLevel =mapStatus.zoom;
                mRoteDegree = mapStatus.rotate;
            }
        });
        mMappingResultPanel=mActivity.getMappingResultPanel();
        initActionBar();
        initTextSwitcher();
        activateCdRadio();
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
        mActivity.getTxtSwitcher().setInAnimation(mActivity, R.anim.anim_text_switcher_in);
        mActivity.getTxtSwitcher().setOutAnimation(mActivity,R.anim.anim_text_switcher_out);
        mActivity.getTxtSwitcher().setText("北斗模块正在启动，需要1-2分钟时间，请稍候...");

    }

    private void initActionBar() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null);
        }
    }

    public void onStart() {
        mBeidouPortDataReceiver=new BeidouPortDataReceiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_SEND_BEIDOU_SP_DATA);
        mActivity.registerReceiver(mBeidouPortDataReceiver,intentFilter);
    }

    public void onPause(){
        mMapView.onPause();
    }

    public void onResume(){
        mMapView.onResume();
    }

    public void onStop() {
        if(mBeidouPortDataReceiver!=null){
            mActivity.unregisterReceiver(mBeidouPortDataReceiver);
            mBeidouPortDataReceiver=null;
        }
        if(mActivity.isFinishing()){
            stopRecordingGNGGP();
            BaseApplication.removeCallBacks(mRunnableOnGetTBGNGGA);
            deactivateCdRadio();
        }

    }

    public void onDestroy(){
        mMapView.onDestroy();
    }

    /**
     * 启动CdRadio模块
     */
    private void activateCdRadio() {
        try {
            mGpioModel.connectCdRadioToCPU();
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    mActivity.getRequestManager().activate(true);
                }
            },1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接CdRadio和北斗模块，之后手持机无法跟CdRadio串口通讯。
     */
    public void connectCdRadioToBeidou() throws IOException {
        mGpioModel.connectCDRadioToBeidou();
    }


    /**
     * 停止CdRadio模块的所有业务
     */
    private void deactivateCdRadio() {
        try {
            mGpioModel.connectCdRadioToCPU();
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    mActivity.getRequestManager().activate(false);
                }
            },1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
//    private void toggleMappingResultPanel(boolean show){
//        int startValue;
//        int endValuel;
//        if(show){
//            startValue=-mMappingResultPanel.getMeasuredHeight();
//            endValuel= 0;
//        }else {
//            startValue= 0;
//            endValuel=-mMappingResultPanel.getMeasuredHeight();
//        }
//        ValueAnimator animator=ValueAnimator.ofInt(startValue,endValuel);
//        animator.setDuration(500).setInterpolator(new DecelerateInterpolator());
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (int) animation.getAnimatedValue();
//                mMappingResultPanel.layout(
//                        mMappingResultPanel.getLeft(),
//                        value,
//                        mMappingResultPanel.getRight(),
//                        mMappingResultPanel.getMeasuredHeight()+value);
//            }
//        });
//        animator.start();
//
//    }

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
        //先把上一次的图层清理掉
        if(mMappingOverLays!=null){
            for (Overlay temp:mMappingOverLays){
                temp.remove();
            }
        }
        //添加新的图层
        mMappingOverLays = mBaiduMapModel.updateMappingOverLays(mActivity.getMapView(), latLngs, false);
        //更新测量结果
        mMappingResultPanel.updateMappingResult(latLngs);
    }

    /**
     * 判断最新接收到的坐标是否和测绘轨迹中上一次的坐标一样。
     * @param latLng  最新接收到的坐标
     */
    private boolean checkIfMappingCoordTheSameAsLast(LatLng latLng) {
        int size = mMapppingCoords.size();
        if(size >0){
            LatLng temp = mMapppingCoords.get(size - 1);
            return temp.latitude==latLng.latitude&&temp.longitude==latLng.longitude;
        }
        return false;
    }

    /**
     * 保存当前测绘数据到数据库
     */
    private void saveMappingDate() {
        if(mMapppingCoords.size()>1){
            ArrayList<LatLng> clone = new ArrayList<>();
            Iterator<LatLng> iterator = mMapppingCoords.iterator();
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

    /**
     * 广播接收者，接收前台服务广播出去的北斗串口数据。
     */
    private class BeidouPortDataReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra(StaticData.EXTRA_BYTES_BEI_DOU_SERIAL_PORT_DATA);
            if(data!=null&&data.length>0){
                NaviDataExtractor.decipherData(data,data.length, mDataExtractorCallBack);
            }
        }
    }

}