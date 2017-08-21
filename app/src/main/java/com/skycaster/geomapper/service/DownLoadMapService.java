package com.skycaster.geomapper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.skycaster.geomapper.receivers.MapDownLoadProgressReceiver;

/**
 * Created by 廖华凯 on 2017/6/12.
 */

public class DownLoadMapService extends Service {

    public static final String CITY_ID="city_id";
    private MKOfflineMap mOfflineMap;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        mOfflineMap = new MKOfflineMap();
        mOfflineMap.init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int i, int i1) {
                switch (i){
                    case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                        MKOLUpdateElement city = mOfflineMap.getUpdateInfo(i1);
                        Intent it=new Intent();
                        it.putExtra(MapDownLoadProgressReceiver.CITY_NAME,city.cityName);
                        it.putExtra(MapDownLoadProgressReceiver.CITY_ID,city.cityID);
                        it.putExtra(MapDownLoadProgressReceiver.RATIO,city.ratio);
                        it.putExtra(MapDownLoadProgressReceiver.STATUS,city.status);
                        sendBroadcast(it,MapDownLoadProgressReceiver.ACTION);
                        if(city.status==MKOLUpdateElement.FINISHED||city.status==MKOLUpdateElement.SUSPENDED){
                            stopSelf();
                        }
                        break;
                    case MKOfflineMap.TYPE_NEW_OFFLINE:
                        break;
                    case MKOfflineMap.TYPE_VER_UPDATE:
                        break;
                    case MKOfflineMap.TYPE_NETWORK_ERROR:
                        stopSelf();
                        break;
                }

            }
        });
        int cityId=intent.getIntExtra(CITY_ID,0);
        mOfflineMap.start(cityId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOfflineMap.destroy();
    }
}
