package com.skycaster.geomapper.customized;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.bean.MapType;


/**
 * Created by 廖华凯 on 2017/8/7.
 */

public class MapTypeSelector extends FrameLayout {
    private ViewGroup rootView;
    private ImageView iv_icon;
    private TextView tv_title;
    private MapType mMapType;
    public MapTypeSelector(@NonNull Context context) {
        this(context,null);
    }

    public MapTypeSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MapTypeSelector(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView= (ViewGroup) ViewGroup.inflate(context, R.layout.item_map_type_selector,null);
        iv_icon= (ImageView) rootView.findViewById(R.id.spinner_item_iv_map_type_icon);
        tv_title= (TextView) rootView.findViewById(R.id.spinner_item_tv_map_type_title);
        addView(rootView);
    }

    public void setMapType(MapType mapType){
        iv_icon.setImageResource(mapType.getDrawableSrc());
        tv_title.setText(mapType.getTitle());
        mMapType=mapType;
    }

    public MapType getMapType() {
        return mMapType;
    }
}
