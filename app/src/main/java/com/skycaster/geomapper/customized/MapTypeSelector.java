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
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.models.NetWorkStateModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by 廖华凯 on 2017/8/7. 中文翻译为“地图类型选择器”，自定义控件，类似百度地图右上角点击切换地图类型的控件。
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
    private AtomicBoolean isNetWorkAvailable =new AtomicBoolean(false);
    private NetWorkStateModel mNetWorkStateModel;


    public MapTypeSelector(@NonNull Context context) {
        this(context,null);
    }

    public MapTypeSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MapTypeSelector(@NonNull final Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //判断当前网络状态，如果断网的情况，是不能切换到卫星图的。
        mNetWorkStateModel=new NetWorkStateModel(context);
        isNetWorkAvailable.compareAndSet(false,mNetWorkStateModel.checkIfNetworkAvailable());

        rootView= (ViewGroup) ViewGroup.inflate(context, R.layout.item_map_type_selector,null);
        iv_icon= (ImageView) rootView.findViewById(R.id.spinner_item_iv_map_type_icon);
        tv_title= (TextView) rootView.findViewById(R.id.spinner_item_tv_map_type_title);
        addView(rootView);

        //读取上一次地图类型，默认是矢量图类型。
        mSharedPreferences = BaseApplication.getSharedPreferences();
        mMapTypeCode = mSharedPreferences.getInt(StaticData.MAP_TYPE_CODE, BaiduMap.MAP_TYPE_NORMAL);


        //点击图片切换地图类型，通过pop window控件来展示地图类型选项
        iv_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPopWindow ==null){
                    ListView listView= new ListView(context);
                    listView.setVerticalScrollBarEnabled(false);
                    listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    //初始化地图类型的集合
                    final ArrayList<MapType> mapTypes=new ArrayList<>();
//                    mapTypes.add(new MapType(BaiduMap.MAP_TYPE_NONE));
                    mapTypes.add(new MapType(BaiduMap.MAP_TYPE_NORMAL));
                    mapTypes.add(new MapType(BaiduMap.MAP_TYPE_SATELLITE));
                    //去掉当前的地图类型，这样展示出来的就是其他可选类型了
                    mapTypes.remove(getMapType());
                    MapTypeSelectorAdapter adapter = new MapTypeSelectorAdapter(mapTypes, context);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MapType temp = mapTypes.get(position);
                            if(temp.getMapTypeCode()==getMapType().getMapTypeCode()){
                                //如果选择的地图类型和当前一样，啥也不用干，退出。
                                mPopWindow.dismiss();
                            }else {
                                if(temp.getMapTypeCode()==BaiduMap.MAP_TYPE_SATELLITE){
                                    if(!mNetWorkStateModel.checkIfNetworkAvailable()){
                                        BaseApplication.showToast("当前无网络，无法转到卫星图。");
                                        return;
                                    }
                                }
                                setMapType(temp);
                                mSharedPreferences.edit().putInt(StaticData.MAP_TYPE_CODE, temp.getMapTypeCode()).apply();
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNetWorkStateModel.registerNetworkStateReceiver(getContext(), new NetWorkStateModel.Callback() {
            @Override
            public void onNetworkDisconnected() {
                isNetWorkAvailable.compareAndSet(true,false);
            }

            @Override
            public void onNetworkConnected() {
                isNetWorkAvailable.compareAndSet(false,true);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mNetWorkStateModel.unRegisterNetworkStateReceiver(getContext());
    }

    /**
     * 设置百度地图的地图类型并刷新地图
     * @param mapType 地图类型
     */
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

    /**
     * 把这个自定义控件和百度地图的map view连接起来，实行联动。获得地图实例后要使用这个方法初始化地图类型，否则此控件无法生效。
     * @param mapView
     */
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
