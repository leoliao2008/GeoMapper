package com.skycaster.geomapper.models;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 廖华凯 on 2017/8/21.
 */

public class NetWorkStateModel {

    private ConnectivityManager mConnectivityManager;
    private NetWorkStateModel.Callback mCallback;
    private NetworkStateChangeReceiver mNetworkStateChangeReceiver;

    public NetWorkStateModel(Context context) {
        mConnectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean checkIfNetworkAvailable(){
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){
            return false;
        }
        return networkInfo.isAvailable();
    }

    public NetWorkType getMostFavorableNetworkType(){
        if(!checkIfNetworkAvailable()){
            return NetWorkType.NET_WORK_TYPE_NO_NETWORK;
        }
        NetworkInfo wifiNetwork = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetwork!=null&&wifiNetwork.isAvailable()){
            return NetWorkType.NET_WORK_TYPE_WIFI;
        }

        NetworkInfo mobileNetWork = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(mobileNetWork!=null&&mobileNetWork.isAvailable()){
            return NetWorkType.NET_WORK_TYPE_MOBILE;
        }
        return NetWorkType.NET_WORK_TYPE_NO_NETWORK;
    }

    public void configWifiSetting(Context context){
        Intent intent = null;
        /**
         * 判断手机系统的版本！如果API大于10 就是3.0+
         * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
         */
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName(
                    "com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }

    public enum NetWorkType{
        NET_WORK_TYPE_WIFI,NET_WORK_TYPE_MOBILE,NET_WORK_TYPE_NO_NETWORK
    }

    public void registerNetworkStateReceiver(Context context,Callback callback){
        mCallback=callback;
        mNetworkStateChangeReceiver=new NetworkStateChangeReceiver();
        IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetworkStateChangeReceiver,intentFilter);
    }

    public void unRegisterNetworkStateReceiver(Context context){
        if(mNetworkStateChangeReceiver!=null){
            context.unregisterReceiver(mNetworkStateChangeReceiver);
            mNetworkStateChangeReceiver=null;
            mCallback=null;
        }
    }

    public class NetworkStateChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(mConnectivityManager!=null&&mCallback!=null){
                NetWorkType netWorkType = getMostFavorableNetworkType();
                if(netWorkType==NetWorkType.NET_WORK_TYPE_NO_NETWORK){
                    mCallback.onNetworkDisconnected();
                }else {
                    mCallback.onNetworkConnected();
                }
            }
        }
    }

    public interface Callback{
        void onNetworkDisconnected();
        void onNetworkConnected();
    }


}
