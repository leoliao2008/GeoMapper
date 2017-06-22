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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapterr.RouteAdminAdapter;
import com.skycaster.geomapper.data.RouteIndexOpenHelper;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;
import com.skycaster.geomapper.interfaces.SQLiteExecuteResultCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class AlertDialogUtil {

    private static AlertDialog mAlertDialog;
    private static View fragmentAdminView;

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
        final RelativeLayout rl_loadingView= (RelativeLayout) rootView.findViewById(R.id.activity_save_route_rl_loading_view);
        //init data
        SimpleDateFormat format=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
        String s = "DB"+format.format(new Date());
        edt_input.setText(s);
        edt_input.setSelection(s.length());
        final RouteIndexOpenHelper helper=new RouteIndexOpenHelper(context);
        final ArrayList<String> index = helper.getRouteIndex();
        final ArrayList<LatLng> mList=new ArrayList<>();
        int size = list.size();
        for(int i=0;i<size;i++){
            try {
                LatLng temp = list.get(i);
                if(temp!=null){
                    mList.add(temp);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        //init listener
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = edt_input.getText().toString().trim();
                if(!TextUtils.isEmpty(s1)){
                    if(index.contains(s1)){
                        ToastUtil.showToast(context.getString(R.string.duplicate_data));
                    }else {

                        if(mList.size()>1){
                            rl_loadingView.setVisibility(View.VISIBLE);
                            helper.addRoute(s1, mList, new SQLiteExecuteResultCallBack() {
                                @Override
                                public void onResult(boolean isSuccess) {
                                    if(isSuccess){
                                        ToastUtil.showToast(context.getString(R.string.save_success));
                                    }else {
                                        ToastUtil.showToast(context.getString(R.string.save_fails));
                                    }
                                    rl_loadingView.setVisibility(View.GONE);
                                    helper.close();
                                    mAlertDialog.dismiss();
                                }
                            });
                        }else {
                            ToastUtil.showToast(context.getString(R.string.not_enough_loc_points));
                        }
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
        //view
        View rootView=View.inflate(context,R.layout.dialog_admin_route_record,null);
        ListView listView= (ListView) rootView.findViewById(R.id.dialog_route_admin_lst_view);
        Button btn_exit= (Button) rootView.findViewById(R.id.dialog_route_admin_btn_cancel);
        final LinearLayout ll_noDate= (LinearLayout) rootView.findViewById(R.id.dialog_route_admin_ll_no_data);
        //date
        final RouteIndexOpenHelper helper=new RouteIndexOpenHelper(context);
        final ArrayList<String> list = helper.getRouteIndex();
        if(list.size()>0){
            RouteAdminAdapter adapter=new RouteAdminAdapter(list, context, helper, new RouteRecordSelectedListener() {
                @Override
                public void onRouteRecordSelected(String recordName) {
                    listener.onRouteRecordSelected(recordName);
                    mAlertDialog.dismiss();
                    helper.close();
                }

                @Override
                public void onRouteRecordEmpty() {
                    ll_noDate.setVisibility(View.VISIBLE);

                }
            });
            listView.setAdapter(adapter);
        }else {
            ll_noDate.setVisibility(View.VISIBLE);
        }
        //listener
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                helper.close();
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog=builder.setView(rootView).setCancelable(false).create();
        mAlertDialog.show();
    }
}
