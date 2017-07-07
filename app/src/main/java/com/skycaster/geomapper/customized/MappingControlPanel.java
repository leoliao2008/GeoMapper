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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.util.LogUtil;

/**
 * Created by 廖华凯 on 2017/7/7.
 */

public class MappingControlPanel extends FrameLayout {
    private Context mContext;
    private LinearLayout rootView;
    private TextView tv_length;
    private TextView tv_acreage;
    private ImageView iv_save;
    private ImageView iv_deleteBack;
    private CheckBox cbx_pauseOrStart;
    private boolean isNaviMode;
    private LinearLayout.LayoutParams mParams;

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
        tv_length= (TextView) rootView.findViewById(R.id.activity_mapping_tv_distance_length);
        tv_acreage= (TextView) rootView.findViewById(R.id.activity_mapping_tv_acreage);
        iv_save= (ImageView) rootView.findViewById(R.id.activity_mapping_iv_save_mapping_data);
        iv_deleteBack= (ImageView) rootView.findViewById(R.id.activity_mapping_iv_back_to_previous_coordinate);
        cbx_pauseOrStart= (CheckBox) rootView.findViewById(R.id.activity_mapping_cbx_start_or_pause);
        initListeners();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mParams = (LinearLayout.LayoutParams) cbx_pauseOrStart.getLayoutParams();

            }
        });
    }

    public void setNaviMode(final boolean isNaviMode) {
        this.isNaviMode = isNaviMode;
        int start=0;
        int stop=0;
        if(isNaviMode){
            stop=1;
        }else {
            start=1;
        }
        showLog("start: "+start+" stop: "+stop);
        ValueAnimator animator=ValueAnimator.ofInt(start,stop);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(isNaviMode){
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

    private void initListeners() {
        iv_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_deleteBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cbx_pauseOrStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }
}
