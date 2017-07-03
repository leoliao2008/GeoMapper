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
    private int mViewHeight;
    private int maxScrollRange;
    private CoordinatorLayout.LayoutParams mViewParam;
    private CoordinatorLayout.LayoutParams mDpdParams;

    public LocationDetailLayoutBehavior() {
    }

    public LocationDetailLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if(mViewParam==null && mDpdParams == null){
            mViewParam = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            maxScrollRange=mViewHeight = mViewParam.height;
        }
        boolean b = dependency instanceof NestedScrollView;
        if(mDpdParams==null&&b){
            mDpdParams= (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
        }
        return b;
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
        handleScroll(child,target,dy);
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        if(target instanceof NestedScrollView){
            handleScroll(child, target, (int) velocityY);
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    private void handleScroll(View child,View target,int dy){
        mViewHeight= (int) (mViewHeight - dy);
        mViewHeight =Math.max(0, Math.min(mViewHeight, maxScrollRange));
        mViewParam.height= mViewHeight;
        child.setLayoutParams(mViewParam);

        mDpdParams.topMargin= mViewHeight;
        target.setLayoutParams(mDpdParams);

    }

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }
}
