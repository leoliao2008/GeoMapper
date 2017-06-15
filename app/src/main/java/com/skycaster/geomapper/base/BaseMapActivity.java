package com.skycaster.geomapper.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 创建者     $Author$
 * 创建时间   2017/5/16 20:14
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public abstract class BaseMapActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        //百度地图启动前需初始化
//        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
    }


}
