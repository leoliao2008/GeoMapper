package com.skycaster.geomapper.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.skycaster.geomapper.R;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class AlertDialogUtil {

    private static AlertDialog mAlertDialog;

    public static void showHint(Context context,String msg){
        showHint(context,msg,null);

    }

    public static void showHint(Context context,String msg,@Nullable Runnable positive){
        showHint(context,msg,positive,null);

    }

    public static void showHint(Context context, String msg, @Nullable final Runnable positive, @Nullable final Runnable negative){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.notice).setCancelable(true).setMessage(msg);
        DialogInterface.OnClickListener temp;
        if(positive!=null){
            temp=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positive.run();
                    mAlertDialog.dismiss();
                }
            };
        }else {
            temp=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAlertDialog.dismiss();
                }
            };
        }
        builder.setPositiveButton(R.string.confirm, temp);
        if(negative!=null){
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    negative.run();
                    mAlertDialog.dismiss();
                }
            });
        }
        mAlertDialog=builder.create();
        mAlertDialog.show();
    }
}
