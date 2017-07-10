package com.skycaster.geomapper.customized;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.MapActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.MappingMode;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.MapUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/7.
 */

public class MappingControlPanel extends FrameLayout {
    private Context mContext;
    private LinearLayout rootView;
    private TextView tv_perimeter;
    private TextView tv_acreage;
    private ImageView iv_save;
    private ImageView iv_deleteBack;
    private CheckBox cbx_pauseOrStart;
    private LinearLayout.LayoutParams mParams;
    private MappingMode mMappingMode;
    private ArrayList<LatLng> mLocations;
    private boolean isNaviMappingStart;
    private MapActivity mActivity;
    private TextView tv_pathLength;

    public MappingControlPanel(@NonNull Context context) {
        this(context,null);
    }

    public MappingControlPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MappingControlPanel(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        rootView= (LinearLayout) LayoutInflater.from(context).inflate(R.layout.widget_mapping_control_panel,null);
        addView(rootView);
        tv_perimeter = (TextView) rootView.findViewById(R.id.activity_mapping_tv_perimeter);
        tv_acreage= (TextView) rootView.findViewById(R.id.activity_mapping_tv_acreage);
        iv_save= (ImageView) rootView.findViewById(R.id.activity_mapping_iv_save_mapping_data);
        iv_deleteBack= (ImageView) rootView.findViewById(R.id.activity_mapping_iv_back_to_previous_coordinate);
        cbx_pauseOrStart= (CheckBox) rootView.findViewById(R.id.activity_mapping_cbx_start_or_pause);
        tv_pathLength= (TextView) rootView.findViewById(R.id.activity_mapping_tv_path_length);
        initListeners();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mParams = (LinearLayout.LayoutParams) cbx_pauseOrStart.getLayoutParams();

            }
        });
    }

    public MappingMode getMappingMode() {
        return mMappingMode;
    }

    public void setMappingMode(final MappingMode mode) {
        mMappingMode=mode;
        if(mMappingMode==MappingMode.MAPPING_MODE_NAVI&&mParams.weight!=1
                ||mMappingMode==MappingMode.MAPPING_MODE_USER&&mParams.weight!=0){
            int start=0;
            int stop=0;
            if(mode==MappingMode.MAPPING_MODE_NAVI){
                stop=1;
            }else {
                start=1;
            }
            ValueAnimator animator=ValueAnimator.ofInt(start, stop);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(mode==MappingMode.MAPPING_MODE_NAVI){
                        mParams.weight= animation.getAnimatedFraction();
                    }else {
                        mParams.weight= 1-animation.getAnimatedFraction();
                    }
                    cbx_pauseOrStart.setLayoutParams(mParams);
                    cbx_pauseOrStart.requestLayout();
                }
            });
            animator.start();
        }

    }

    private void initListeners() {
        iv_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_deleteBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocations!=null){
                    int size = mLocations.size();
                    if(size >0){
                        mLocations.remove(mLocations.get(size-1));
                        mActivity.updateMappingOverLays();
                    }
                }


            }
        });


        cbx_pauseOrStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isNaviMappingStart=cbx_pauseOrStart.isChecked();
            }
        });
    }

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }

    public void attachToMappingActivity(ArrayList<LatLng> list, MapActivity activity){
        mActivity=activity;
        mLocations=list;

    }

    public boolean isNaviMappingStart() {
        return isNaviMappingStart;
    }

    public void setNaviMappingStart(boolean naviMappingStart) {
        isNaviMappingStart = naviMappingStart;
        cbx_pauseOrStart.setChecked(isNaviMappingStart);
    }

    public void updateLengthAndAcreage(){
        BaseApplication.post(new Runnable() {
            @Override
            public void run() {
                double distance=0;
                int size = mLocations.size();
                if(size>1){
                    for(int i = 1; i<size; i++){
                        distance+= DistanceUtil.getDistance(mLocations.get(i-1),mLocations.get(i));
                        showLog("path length: "+distance);

                    }
                }
                tv_pathLength.setText(String.format("%.02f",distance));
                if(size>2){
                    distance+=DistanceUtil.getDistance(mLocations.get(size-1),mLocations.get(0));
                    showLog("perimeter: "+distance);
                    tv_perimeter.setText(String.format("%.02f",distance));
                }else {
                    tv_perimeter.setText(String.format("%.02f",0.f));
                }
                double area = MapUtil.getPolygonArea(mLocations);
                tv_acreage.setText(String.format("%.02f",area));
            }
        });
    }
}
