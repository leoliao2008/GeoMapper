package com.skycaster.geomapper.interfaces;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;

/**
 * Created by 廖华凯 on 2017/6/13.
 */

public interface OffLineMapDownLoadListener {
    void onProgressUpgrade(MKOLUpdateElement updateElement);
}
