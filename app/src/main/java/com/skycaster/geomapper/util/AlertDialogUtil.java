package com.skycaster.geomapper.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.data.RouteIndexOpenHelper;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    public static void saveRoute(final Activity context, final ArrayList<LatLng>list){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        //init view
        View rootView=View.inflate(context,R.layout.dialog_save_route_points,null);
        final EditText edt_input= (EditText) rootView.findViewById(R.id.activity_save_route_edt_input_name);
        Button btn_save= (Button) rootView.findViewById(R.id.activity_save_route_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.activity_save_route_btn_cancel);
        //init data
        SimpleDateFormat format=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
        String s = "DB"+format.format(new Date());
        edt_input.setText(s);
        edt_input.setSelection(s.length());
        final RouteIndexOpenHelper helper=new RouteIndexOpenHelper(context);
        final ArrayList<String> index = helper.getRouteIndex();
        //init listener
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = edt_input.getText().toString().trim();
                if(!TextUtils.isEmpty(s1)){
                    if(index.contains(s1)){
                        ToastUtil.showToast(context.getString(R.string.duplicate_data));
                    }else {
                        if(helper.addRoute(s1,list)){
                            ToastUtil.showToast(context.getString(R.string.save_success));
                        }else {
                            ToastUtil.showToast(context.getString(R.string.save_fails));
                        }
                        helper.close();
                        mAlertDialog.dismiss();
                    }
                }else {
                    ToastUtil.showToast(context.getString(R.string.warning_invalid_input));
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.close();
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog=builder.setView(rootView).setCancelable(false).create();
        mAlertDialog.show();
    }

    public static void showRouteRecords(Activity context, final RouteRecordSelectedListener listener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        final RouteIndexOpenHelper helper=new RouteIndexOpenHelper(context);
        ArrayList<String> routeIndex = helper.getRouteIndex();
        final String[] strings=new String[routeIndex.size()];
        routeIndex.toArray(strings);
        builder.setTitle(context.getString(R.string.route_records))
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.close();
                        mAlertDialog.dismiss();
                    }
                });
        if(strings.length>0){
            builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onRouteRecordSelected(strings[which]);
                    helper.close();
                    mAlertDialog.dismiss();
                }
            });
        }else {
            builder.setMessage(context.getString(R.string.no_route_record));
        }
        mAlertDialog=builder.create();
        mAlertDialog.show();
    }
}
