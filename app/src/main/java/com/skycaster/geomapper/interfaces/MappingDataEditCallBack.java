package com.skycaster.geomapper.interfaces;

import com.skycaster.geomapper.bean.MappingData;

/**
 * Created by 廖华凯 on 2017/7/12.
 */

public interface MappingDataEditCallBack {
    void onDelete(MappingData data,int groupIndex,int childIndex);
    void onEdit(MappingData data,int groupIndex,int childIndex);
    void onViewDetails(MappingData data,int groupIndex,int childIndex);
}
