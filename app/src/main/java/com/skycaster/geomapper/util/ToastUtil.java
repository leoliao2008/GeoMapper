package com.skycaster.geomapper.util;

import android.widget.Toast;

import com.skycaster.geomapper.base.BaseApplication;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class ToastUtil {
    private static Toast mToast;

    public static synchronized void showToast(String msg){
        if(mToast==null){
            mToast=Toast.makeText(BaseApplication.getContext(),msg,Toast.LENGTH_SHORT);
        }else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
