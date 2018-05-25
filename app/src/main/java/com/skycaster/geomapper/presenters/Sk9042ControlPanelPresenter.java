package com.skycaster.geomapper.presenters;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.SK9042SettingActivity;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.interfaces.AlertDialogUtilsCallBack;
import com.skycaster.geomapper.models.GPIOModel;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.sk9042_lib.ack.AckDecipher;
import com.skycaster.sk9042_lib.ack.RequestCallBack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import project.SerialPort.SerialPort;

import static com.skycaster.sk9042_lib.request.RequestManager.getInstance;

/**
 * Created by 廖华凯 on 2018/5/17.
 */

public class Sk9042ControlPanelPresenter {
    private SK9042SettingActivity mActivity;
    private ListView mListView;
    private AckDecipher mAckDecipher;
    private RequestCallBack  mCallBack = new RequestCallBack() {
        @Override
        protected void testConnection(boolean isConnected) {
            super.testConnection(isConnected);
            mActivity.updateDisplayConsole("Sk9042模块连接"+(isConnected?"正常":"不正常"));
        }

        @Override
        protected void reset(boolean isSuccess) {
            super.reset(isSuccess);
            mActivity.updateDisplayConsole("Sk9042模块算法复位"+(isSuccess?"成功":"失败"));
        }

        @Override
        protected void getSysTime(String time) {
            super.getSysTime(time);
        }

        @Override
        protected void setBaudRate(boolean isSuccess) {
            super.setBaudRate(isSuccess);
            mActivity.updateDisplayConsole("Sk9042模块波特率设置"+(isSuccess?"成功":"失败"));
        }

        @Override
        protected void setFreq(boolean isSuccess) {
            super.setFreq(isSuccess);
            mActivity.updateDisplayConsole("Sk9042模块设置频点"+(isSuccess?"成功":"失败"));
        }

        @Override
        protected void getFreq(boolean isAvailable, String freq) {
            super.getFreq(isAvailable, freq);
            mActivity.updateDisplayConsole("Sk9042模块频点状态为："+(isAvailable?"已经设置":"未设置")+"， 频点："+(TextUtils.isEmpty(freq)?"Null":freq));
        }

        @Override
        protected void setReceiveMode(boolean isSuccess) {
            super.setReceiveMode(isSuccess);
            mActivity.updateDisplayConsole("设置接收模式"+(isSuccess?"成功":"失败"));
        }

        @Override
        protected void getReceiveMode(String mode) {
            super.getReceiveMode(mode);
            mActivity.updateDisplayConsole("当前接受模式为："+mode);
        }

        @Override
        protected void toggleCKFO(boolean isSuccess) {
            super.toggleCKFO(isSuccess);
        }

        @Override
        protected void checkIsOpenCKFO(String isOpen) {
            super.checkIsOpenCKFO(isOpen);
        }

        @Override
        protected void toggle1PPS(boolean isSuccess) {
            super.toggle1PPS(isSuccess);
        }

        @Override
        protected void checkIsOpen1PPS(String isOpen) {
            super.checkIsOpen1PPS(isOpen);
        }

        @Override
        protected void getSysVersion(String version) {
            super.getSysVersion(version);
            mActivity.updateDisplayConsole("Sk9042模块当前系统版本号为"+version);
        }

        @Override
        protected void setChipId(boolean isSuccess) {
            super.setChipId(isSuccess);
        }

        @Override
        protected void getChipId(String id) {
            super.getChipId(id);
        }

        @Override
        protected void getSNR(String snr) {
            super.getSNR(snr);
            mActivity.updateDisplayConsole("Sk9042模块当前音噪率是"+snr);
        }

        @Override
        protected void getSysState(String state) {
            super.getSysState(state);
            String temp="null";
            switch (Integer.valueOf(state)){
                case 0:
                    temp="未开机";
                    break;
                case 1:
                    temp="就绪";
                    break;
                case 2:
                    temp="锁定";
                    break;
                case 3:
                    temp="停止工作";
                    break;
                default:
                    break;
            }
            mActivity.updateDisplayConsole("Sk9042系统当前状态为"+temp);
        }

        @Override
        protected void getSFO(String sfo) {
            super.getSFO(sfo);
            mActivity.updateDisplayConsole("Sk9042当前时偏为"+sfo);
        }

        @Override
        protected void getCFO(String cfo) {
            super.getCFO(cfo);
            mActivity.updateDisplayConsole("Sk9042当前频偏为"+cfo);
        }

        @Override
        protected void getTunerState(boolean isSet) {
            super.getTunerState(isSet);
        }

        @Override
        protected void getLDPC(String passCnt, String failCnt) {
            super.getLDPC(passCnt, failCnt);
            mActivity.updateDisplayConsole("Sk9042译码成功次数"+passCnt+", 失败次数"+failCnt);
        }

        @Override
        protected void setLogLevel(boolean isSuccess) {
            super.setLogLevel(isSuccess);
            mActivity.updateDisplayConsole("Sk9042 Log等级设置"+(isSuccess?"成功":"失败"));
        }

        @Override
        protected void stopSearchFreq() {
            super.stopSearchFreq();
        }

        @Override
        protected void startSearchFreq(boolean isFound, String result) {
            super.startSearchFreq(isFound, result);
        }

        @Override
        protected void verifyFreq(boolean hasSignal) {
            super.verifyFreq(hasSignal);
            mActivity.updateDisplayConsole("Sk9042在该频点下"+(hasSignal?"有信号。":"无信号。"));
        }

        @Override
        protected void onConfirmUpgradeStart() {
            super.onConfirmUpgradeStart();
            mActivity.updateDisplayConsole("升级校验成功，即将升级...");
        }

        @Override
        protected void onStartTransferringUpgradeFile() {
            super.onStartTransferringUpgradeFile();
            mActivity.updateDisplayConsole("开始传送升级包...");
        }

        @Override
        protected void onFinishTransferringUpgradeFile() {
            super.onFinishTransferringUpgradeFile();
            mActivity.updateDisplayConsole("升级包传送完毕。");
        }

        @Override
        protected void onUpgradeFinish(boolean isSuccess, String errorCode) {
            super.onUpgradeFinish(isSuccess, errorCode);
            mActivity.updateDisplayConsole("升级结束，结果："+isSuccess);
            if(!isSuccess){
                mActivity.updateDisplayConsole(errorCode);
            }

        }

        @Override
        public void getBaudRate(boolean isValid, String result) {
            super.getBaudRate(isValid, result);
            mActivity.updateDisplayConsole("波特率"+(isValid?"有效":"无效")+", 结果："+result);
        }
    };
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private SerialPort mSerialPort;
    private GPIOModel mGPIOModel;

    public Sk9042ControlPanelPresenter(SK9042SettingActivity activity) {
        mActivity = activity;
        mListView=mActivity.getControlPanel();
    }

    public void init(){
        //打开SK9042模块的电源
        mGPIOModel=new GPIOModel();
        try {
            mGPIOModel.toggleSk9042Pow(true);
        } catch (IOException e) {
            handleException(e);
        }
        //把SK9042的设置选项都列到listview中，并设定点击事件
        String[] array = mActivity.getResources().getStringArray(R.array.sk9042_functions);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                mActivity,
                R.layout.item_simple_list_view_item,
                array
        );
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mOutputStream==null){
                    ToastUtil.showToast(mActivity.getString(R.string.serial_port_invalid));
                    return;
                }
                try {
                    switch (position){
                        case 0:
                            //测试连接
                            getInstance().testConnection(mOutputStream);
                            break;
                        case 1:
                            //算法复位
                            getInstance().reset(mOutputStream);
                            break;
                        case 2:
                            //日期时间
//                            getInstance().getSysTime(mOutputStream);
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 3:
                            //设置波特率
                            AlertDialogUtil.showChooseSK9042BaudRate(
                                    mActivity,
                                    new AlertDialogUtilsCallBack(){
                                        @Override
                                        public void onGetData(String... args) {
                                            try {
                                                getInstance().setBaudRate(mOutputStream,args[0]);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 4:
                            //查询波特率
                            getInstance().getBaudRate(mOutputStream);
                            break;
                        case 5:
                            //设置频点
                            AlertDialogUtil.showSetSk9042Freq(
                                    mActivity,
                                    new AlertDialogUtilsCallBack(){
                                        @Override
                                        public void onGetData(String... args) {
                                            try {
                                                getInstance().setFreq(mOutputStream,args[0]);
                                            } catch (IOException e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 6:
                            //查询频点
                            getInstance().getFreq(mOutputStream);
                            break;
                        case 7:
                            //设置接收模式
                            AlertDialogUtil.showChooseSk9042RevMode(
                                    mActivity,
                                    new AlertDialogUtilsCallBack(){
                                        @Override
                                        public void onGetData(String... args) {
                                            try {
                                                getInstance().setReceiveMode(mOutputStream,args[0]);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 8:
                            //查询接收模式
                            getInstance().getReceiveMode(mOutputStream);
                            break;
                        case 9:
                            //设置运行模式
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 10:
                            //查询运行模式
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 11:
                            //设置数据输出校验功能
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 12:
                            //查询数据输出校验功能
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 13:
                            //设置1PPS开
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 14:
                            //设置1PPS关
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 15:
                            //查询1PPS开关
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 16:
                            //启动升级
                            AlertDialogUtil.showPickSK9042UpgradeFileWindow(
                                    mActivity,
                                    new AlertDialogUtilsCallBack(){
                                        @Override
                                        public void onGetFile(File file) {
                                            super.onGetFile(file);
                                            try {
                                                getInstance().startUpgrade(mOutputStream,file);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 17:
                            //启动升级数据传输
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 18:
                            //查询SK9042系统版本
                            getInstance().getSysVersion(mOutputStream);
                            break;
                        case 19:
                            //设置产品ID
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 20:
                            //查询产品ID
                            ToastUtil.showToast("此功能已经废弃。");
                            break;
                        case 21:
                            //查询信噪比
                            getInstance().getSNR(mOutputStream);
                            break;
                        case 22:
                            //查询接收状态
                            getInstance().getSysState(mOutputStream);
                            break;
                        case 23:
                            //查询时偏
                            getInstance().getSFO(mOutputStream);
                            break;
                        case 24:
                            //查询频偏
                            getInstance().getCFO(mOutputStream);
                            break;
                        case 25:
                            //查询Tuner状态
                            getInstance().getTunerState(mOutputStream);
                            break;
                        case 26:
                            //译码统计
                            getInstance().getLDPC(mOutputStream);
                            break;
                        case 27:
                            //Log等级设置
                            AlertDialogUtil.showSetSk9042LogLevel(
                                    mActivity,
                                    new AlertDialogUtilsCallBack(){
                                        @Override
                                        public void onGetData(String... args) {
                                            try {
                                                getInstance().setLogLevel(mOutputStream,args[0]);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 28:
                            //开始搜台
                            ToastUtil.showToast("此功能不开放。");
                            break;
                        case 29:
                            //停止搜台
                            ToastUtil.showToast("此功能不开放。");
                            break;
                        case 30:
                            //校验频率
                            AlertDialogUtil.showCheckSk9042Freq(
                                    mActivity,
                                    new AlertDialogUtilsCallBack(){
                                        @Override
                                        public void onGetData(String... args) {
                                            try {
                                                getInstance().verifyFreq(mOutputStream,args[0]);
                                            }catch (Exception e){
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 31:
                            //查询SK9042差分输出端波特率
                            getInstance().getBaudRate(mOutputStream);
                            break;
                        default:
                            break;
                    }
                }catch (Exception e){
                    handleException(e);
                }

            }
        });

        //打开SK9042串口
        openSK9042SerialPort();
        //解析串口数据，在回调中返回结果
        mAckDecipher=new AckDecipher(mCallBack);
        try {
            mAckDecipher.decipherByStream(mInputStream);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        String message = e.getMessage();
        if(TextUtils.isEmpty(message)){
            message="未知错误";
        }
        mActivity.updateDisplayConsole(message);
    }

    private void openSK9042SerialPort() {
        String path= StaticData.SK9042_MODULE_SP_PATH;
        int bdRate=StaticData.SK9042_MODULE_SP_BAUD_RATE;
        try {
            mSerialPort = new SerialPort(new File(path),bdRate,0);
            if(mSerialPort !=null){
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void onDestroy(){
        try {
            //停止解析串口数据
            mAckDecipher.stopDecipherByStream();
            //关闭SK9042模块的电源
            mGPIOModel.toggleSk9042Pow(false);
            //释放串口
            closeSK9042SerialPort();


        } catch (Exception e) {
            handleException(e);
        }
    }

    private void closeSK9042SerialPort() {
        if(mSerialPort!=null){
            mSerialPort.close();
            mSerialPort=null;
        }
    }
}
