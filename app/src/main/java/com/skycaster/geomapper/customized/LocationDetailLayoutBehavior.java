package com.skycaster.geomapper.customized;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.skycaster.geomapper.util.LogUtil;

/**
 * Created by 廖华凯 on 2017/7/3.
 */

public class LocationDetailLayoutBehavior extends CoordinatorLayout.Behavior<View> {
    private int childHeight;
    private int maxScrollRange;
    private CoordinatorLayout.LayoutParams mViewParam;
    private CoordinatorLayout.LayoutParams mDpParams;

    public LocationDetailLayoutBehavior() {
    }

    public LocationDetailLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if(mViewParam==null&&mDpParams==null){
            mViewParam = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int height = mViewParam.height;
            childHeight = height;
            maxScrollRange=height;
            boolean b = dependency instanceof NestedScrollView;
            if(b){
                mDpParams = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
            }
        }
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL)>0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        showLog("dy= "+dy);
        childHeight =childHeight-dy;
//        if(childHeight<0){
//            childHeight=0;
//        }else if(childHeight>=maxScrollRange){
//            childHeight=maxScrollRange;
//        }
        showLog("child height before = "+childHeight);
//        childHeight=Math.max(0,Math.min(childHeight,maxScrollRange));
        showLog("maxScrollRange="+maxScrollRange);
        childHeight=Math.max(0,Math.min(childHeight,maxScrollRange));
        showLog("child height after = "+childHeight);
        mViewParam.height= childHeight;
        child.setLayoutParams(mViewParam);
        mDpParams.topMargin=maxScrollRange-childHeight;
        target.setLayoutParams(mDpParams);

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }
}
