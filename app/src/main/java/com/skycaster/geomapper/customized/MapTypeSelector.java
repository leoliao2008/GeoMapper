package com.skycaster.geomapper.customized;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.MapType;
import com.skycaster.geomapper.data.Constants;

import java.util.ArrayList;


/**
 * Created by 廖华凯 on 2017/8/7.
 */

public class MapTypeSelector extends FrameLayout {
    private ViewGroup rootView;
    private ImageView iv_icon;
    private TextView tv_title;
    private MapType mMapType;
    private PopupWindow mPopWindow;
    private SharedPreferences mSharedPreferences;
    private int mMapTypeCode;
    private TextureMapView mMapView;


    public MapTypeSelector(@NonNull Context context) {
        this(context,null);
    }

    public MapTypeSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MapTypeSelector(@NonNull final Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView= (ViewGroup) ViewGroup.inflate(context, R.layout.item_map_type_selector,null);
        iv_icon= (ImageView) rootView.findViewById(R.id.spinner_item_iv_map_type_icon);
        tv_title= (TextView) rootView.findViewById(R.id.spinner_item_tv_map_type_title);
        addView(rootView);

        mSharedPreferences = BaseApplication.getSharedPreferences();
        mMapTypeCode = mSharedPreferences.getInt(Constants.MAP_TYPE_CODE, BaiduMap.MAP_TYPE_NORMAL);


        iv_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPopWindow ==null){

                    ListView listView= new ListView(context);
                    listView.setVerticalScrollBarEnabled(false);
                    listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    final ArrayList<MapType> mapTypes=new ArrayList<>();
//                    mapTypes.add(new MapType(BaiduMap.MAP_TYPE_NONE));
                    mapTypes.add(new MapType(BaiduMap.MAP_TYPE_NORMAL));
                    mapTypes.add(new MapType(BaiduMap.MAP_TYPE_SATELLITE));
                    mapTypes.remove(getMapType());
                    MapTypeSelectorAdapter adapter = new MapTypeSelectorAdapter(mapTypes, context);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MapType temp = mapTypes.get(position);
                            if(temp.getMapTypeCode()==getMapType().getMapTypeCode()){
                                mPopWindow.dismiss();
                            }else {
                                setMapType(temp);
                                mSharedPreferences.edit().putInt(Constants.MAP_TYPE_CODE, temp.getMapTypeCode()).apply();
                                mPopWindow.dismiss();
                                mPopWindow =null;
                            }
                        }
                    });

                    mPopWindow = new PopupWindow(getMeasuredWidth(),getMeasuredHeight()*mapTypes.size());
                    mPopWindow.setAnimationStyle(R.style.PopWinAnimationStyle);
                    mPopWindow.setContentView(listView);
                    mPopWindow.setBackgroundDrawable(new ColorDrawable());
                    mPopWindow.setFocusable(true);
                    mPopWindow.setOutsideTouchable(true);
                    mPopWindow.showAsDropDown(MapTypeSelector.this);
                }else {
                    if(mPopWindow.isShowing()){
                        mPopWindow.dismiss();
                    }else {
                        mPopWindow.showAsDropDown(MapTypeSelector.this);
                    }

                }
            }
        });
    }

    public void setMapType(MapType mapType){
        iv_icon.setImageResource(mapType.getDrawableSrc());
        tv_title.setText(mapType.getTitle());
        if(mMapView!=null){
            mMapView.getMap().setMapType(mapType.getMapTypeCode());
        }
        mMapType=mapType;
    }

    public MapType getMapType() {
        return mMapType;
    }

    public void attachToMapView(TextureMapView mapView){
        mMapView=mapView;
        setMapType(new MapType(mMapTypeCode));
    }


    /**
     * 设配器
     */
    private class MapTypeSelectorAdapter extends BaseAdapter {
        private ArrayList<MapType> mList;
        private Context mContext;

        public MapTypeSelectorAdapter(ArrayList<MapType> list, Context context) {
            mList = list;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView==null){
                convertView=View.inflate(mContext,R.layout.item_map_type_selector,null);
                vh=new ViewHolder(convertView);
                convertView.setTag(vh);
            }else {
                vh= (ViewHolder) convertView.getTag();
            }
            MapType item = mList.get(position);
            vh.tv_title.setText(item.getTitle());
            vh.iv_icon.setImageResource(item.getDrawableSrc());
            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_icon;
            private TextView tv_title;

            public ViewHolder(View contentView) {
                iv_icon= (ImageView) contentView.findViewById(R.id.spinner_item_iv_map_type_icon);
                tv_title= (TextView) contentView.findViewById(R.id.spinner_item_tv_map_type_title);
            }
        }
    }
}
