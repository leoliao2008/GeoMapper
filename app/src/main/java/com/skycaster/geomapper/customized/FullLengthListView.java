package com.skycaster.geomapper.customized;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by 廖华凯 on 2017/6/27.
 */

public class FullLengthListView extends ListView {
    public FullLengthListView(Context context) {
        this(context,null);
    }

    public FullLengthListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FullLengthListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
