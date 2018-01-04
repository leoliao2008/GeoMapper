package com.skycaster.geomapper.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.CDRadioSetting;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.models.GPIOModel;
import com.skycaster.skycaster21489.data.ServiceCode;
import com.skycaster.skycaster21489.excpt.FreqOutOfRangeException;
import com.skycaster.skycaster21489.excpt.TunerSettingException;
import com.skycaster.skycaster21489.utils.AdspRequestManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/10/23.
 */

public class CDRadioSettingPresenter {
    private CDRadioSetting mActivity;
    private GPIOModel mGPIOModel;
    private AdspRequestManager mRequestManager;
    private EditText mEdtFrq;
    private EditText mEdtLeftTune;
    private EditText mEdtRightTune;
    private ArrayList<String> mList=new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private ListView mRecyclerView;
    private float mTextSize;


    public CDRadioSettingPresenter(CDRadioSetting activity) {
        mActivity = activity;
    }

    public void init(){
        mGPIOModel=new GPIOModel();
        mRequestManager =mActivity.getRequestManager();
        mActivity.getTvPortPath().setText(mActivity.getSerialPortPath());
        mActivity.getTvPortRate().setText(String.valueOf(mActivity.getBaudRate()));
        mEdtFrq=mActivity.getEdtFrq();
        mEdtLeftTune=mActivity.getEdtLeftTune();
        mEdtRightTune=mActivity.getEdtRightTune();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = mActivity.getRecyclerView();
        DisplayMetrics metrics=new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mTextSize = mActivity.getResources().getDimension(R.dimen.sp_24)/metrics.scaledDensity;
        mAdapter=new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                mList
        ){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(mTextSize);
                return textView;
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onStart()  {
//        try {
//            mGPIOModel.turnOnCdRadio();
//        } catch (IOException e) {
//            handleException(e);
//        }
        mRequestManager.activate(false);//在启动的时候先把之前地图上的业务数据传输停止掉（如果还没有来得及停止）。


    }

    public void onStop()  {
//        try {
//            mGPIOModel.turnOffCdRadio();
//        } catch (IOException e) {
//            handleException(e);
//        }
        if(mActivity.isFinishing()){
            mRequestManager.activate(false);
        }
    }

    public void activateCdRadio(){
        try {
            mGPIOModel.connectCdRadioToCPU();
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    mRequestManager.activate(true);
                }
            },1000);
        } catch (IOException e) {
            handleException(e);
        }

    }

    public void deactivateCdRadio(){
        try {
            mGPIOModel.connectCdRadioToCPU();
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    mRequestManager.activate(false);
                }
            },1000);
        } catch (IOException e) {
            handleException(e);
        }

    }

    public void connectCdRadioToBeidou(){
        try {
            mGPIOModel.connectCDRadioToBeidou();
        } catch (IOException e) {
            handleException(e);
        }
    }


    public void checkFrq() {

        if(mActivity.isCdRadioActivated()){
            try {
                mGPIOModel.connectCdRadioToCPU();
                BaseApplication.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        mRequestManager.checkFreq();
                    }
                },1000);
            } catch (IOException e) {
                handleException(e);
            }

        }else {
            mActivity.showToast("必须先启动CDRadio模组。");
        }

    }

    public void checkTunes(){
        if(mActivity.isCdRadioActivated()){
            try {
                mGPIOModel.connectCdRadioToCPU();
                BaseApplication.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        mRequestManager.checkTunes();
                    }
                },1000);
            } catch (IOException e) {
                handleException(e);
            }
        }else {
            mActivity.showToast("必须先启动CDRadio模组。");
        }

    }

    public void startService(){
        if(mActivity.isCdRadioActivated()){
            try {
                mGPIOModel.connectCdRadioToCPU();
                BaseApplication.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        mRequestManager.startService(ServiceCode.RAW_DATA);
                    }
                },1000);
            } catch (IOException e) {
                handleException(e);
            }

        }else {
            mActivity.showToast("必须先启动CDRadio模组。");
        }
    }

    public void stopService(){
        deactivateCdRadio();
    }

    public void submitConfigs(){
        if (mActivity.isTransmittingRawData()){
            mActivity.showToast("请先停止业务数据传输！");
            return;
        }
        final String str_frq = mEdtFrq.getText().toString();
        if(TextUtils.isEmpty(str_frq)){
            mActivity.showToast("主频不可为空。");
            return;
        }
        final String str_leftTune = mEdtLeftTune.getText().toString();
        if(TextUtils.isEmpty(str_leftTune)){
            mActivity.showToast("左频不可为空。");
            return;
        }
        final String str_rightTune = mEdtRightTune.getText().toString();
        if(TextUtils.isEmpty(str_rightTune)){
            mActivity.showToast("右频不可为空。");
            return;
        }
        try {
            mGPIOModel.connectCdRadioToCPU();
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        mRequestManager.setFreq(Integer.valueOf(str_frq.trim()));
                    } catch (FreqOutOfRangeException e) {
                        handleException(e);
                    }
                    BaseApplication.postDelay(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mRequestManager.setTunes(Integer.valueOf(str_leftTune.trim()),Integer.valueOf(str_rightTune.trim()));
                            } catch (TunerSettingException e) {
                                handleException(e);
                            }
                        }
                    },1000);
                }
            },1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void handleException(Exception e){
        String message = e.getMessage();
        if(TextUtils.isEmpty(message)){
            message="Exception Unknown";
        }
        mActivity.showToast(message);
    }

    public void onGetRawData(byte[] bytes, int len) {
        if(bytes==null){
            return;
        }
        final String s=new String(bytes,0,len);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mList.add(s);
                if(mList.size()>15){
                    mList.remove(0);
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });
    }
}
