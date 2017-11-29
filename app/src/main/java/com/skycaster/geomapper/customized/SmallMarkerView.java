package com.skycaster.geomapper.customized;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.skycaster.geomapper.R;

/**
 * Created by 廖华凯 on 2017/7/8.
 */

public class SmallMarkerView extends FrameLayout{
    private FrameLayout rootView;
    private TextView tv_number;

    public SmallMarkerView(Context context, String text) {
        this(context,text,null);
    }

    public SmallMarkerView(Context context, String text, @Nullable AttributeSet attrs) {
        this(context,text, attrs,0);
    }

    public SmallMarkerView(Context context, String text, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        rootView= (FrameLayout) View.inflate(context, R.layout.widget_small_marker,null);
        tv_number= (TextView) rootView.findViewById(R.id.widget_small_marker_tv_number);
        addView(rootView);
        tv_number.setText(text);
    }


}
