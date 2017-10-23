package com.skycaster.geomapper.presenters;

import android.os.Handler;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.skycaster.geomapper.activity.CDRadioSetting;
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
    private ListView mListView;


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
        initListView();
    }

    private void initListView() {
        mListView = mActivity.getListView();
        mAdapter=new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                mList
        );
        mListView.setAdapter(mAdapter);
    }

    public void onStart()  {
        try {
            mGPIOModel.turnOnCdRadio();
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void onStop()  {
        try {
            mGPIOModel.turnOffCdRadio();
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void activateCdRadio(){
        mRequestManager.activate(true);
    }

    public void deactivateCdRadio(){
        mRequestManager.activate(false);
    }


    public void checkFrq() {
        if(mActivity.isCdRadioActivated()){
            mRequestManager.checkFreq();
        }else {
            mActivity.showHint("必须先启动CDRadio模组。");
        }

    }

    public void checkTunes(){
        if(mActivity.isCdRadioActivated()){
            mRequestManager.checkTunes();
        }else {
            mActivity.showHint("必须先启动CDRadio模组。");
        }

    }

    public void startService(){
        if(mActivity.isCdRadioActivated()){
            mRequestManager.startService(ServiceCode.RAW_DATA);
        }else {
            mActivity.showHint("必须先启动CDRadio模组。");
        }
    }

    public void stopService(){
        deactivateCdRadio();
    }

    public void submitConfigs(){
        String str_frq = mEdtFrq.getText().toString();
        if(TextUtils.isEmpty(str_frq)){
            mActivity.showHint("主频不可为空。");
            return;
        }
        final String str_leftTune = mEdtLeftTune.getText().toString();
        if(TextUtils.isEmpty(str_leftTune)){
            mActivity.showHint("左频不可为空。");
            return;
        }
        final String str_rightTune = mEdtRightTune.getText().toString();
        if(TextUtils.isEmpty(str_rightTune)){
            mActivity.showHint("右频不可为空。");
            return;
        }
        try {
            mRequestManager.setFreq(Integer.valueOf(str_frq.trim()));
        } catch (FreqOutOfRangeException e) {
            handleException(e);
            return;
        }
        new Handler().postDelayed(new Runnable() {
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


    private void handleException(Exception e){
        String message = e.getMessage();
        if(TextUtils.isEmpty(message)){
            message="Exception Unknown";
        }
        mActivity.showHint(message);
    }

    public void onGetRawData(byte[] bytes, int len) {
        final String s=new String(bytes,0,len);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mList.add(s);
                mAdapter.notifyDataSetChanged();
                mListView.smoothScrollByOffset(Integer.MAX_VALUE);
            }
        });
    }
}
