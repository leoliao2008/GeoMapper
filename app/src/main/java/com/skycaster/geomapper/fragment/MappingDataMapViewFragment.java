package com.skycaster.geomapper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.MappingCoordinateRecyclerAdapter;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.interfaces.CoordinateItemEditCallBack;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/14.
 */

public class MappingDataMapViewFragment extends BaseFragment {

    private TextureMapView mMapView;
    private NestedScrollView mScrollView;
    private RecyclerView mRecyclerView;
    private ArrayList<LatLng> mLatLngs;
    private static final String EXTRA_COORDINATES="coordinates";
    private MappingCoordinateRecyclerAdapter mAdapter;

    public MappingDataMapViewFragment(Context context, ArrayList<LatLng> latLngs) {
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(EXTRA_COORDINATES,latLngs);
        setArguments(bundle);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_mapping_data_map_view;
    }

    @Override
    protected void initView() {
        mMapView= (TextureMapView) findViewById(R.id.fragment_save_mapping_data_map_view);
        mScrollView= (NestedScrollView) findViewById(R.id.fragment_save_mapping_data_nested_scroll_view);
        mRecyclerView= (RecyclerView) findViewById(R.id.fragment_save_mapping_data_recycler_view);

    }

    @Override
    protected void initData(Bundle arguments) {
        ArrayList<LatLng> list = arguments.getParcelableArrayList(EXTRA_COORDINATES);
        if(list!=null&&list.size()>0){
            mLatLngs=list;
        }

        mAdapter=new MappingCoordinateRecyclerAdapter(mLatLngs, getContext(), new CoordinateItemEditCallBack() {
            @Override
            public void onInsertNewLatlng(int newPosition, LatLng newLatlng) {

            }

            @Override
            public void onDeleteLatlng(int position, LatLng latLng) {

            }

            @Override
            public void onSaveAs(int position, LatLng latLng) {

            }

            @Override
            public void onEdit(int position, LatLng latLng) {

            }

            @Override
            public void onItemSelected(int position, LatLng latLng) {

            }
        });
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mScrollView.setTop((int) (BaseApplication.getDisplayMetrics().heightPixels*0.1));

    }

    @Override
    protected void initListeners() {
        mMapView.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mMapView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mMapView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        mMapView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mMapView.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        mMapView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
