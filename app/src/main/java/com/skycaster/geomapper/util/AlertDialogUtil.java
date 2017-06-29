package com.skycaster.geomapper.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.RouteAdminAdapter;
import com.skycaster.geomapper.bean.LocationTag;
import com.skycaster.geomapper.data.LocTagListOpenHelper;
import com.skycaster.geomapper.data.RouteIndexOpenHelper;
import com.skycaster.geomapper.interfaces.RequestTakingPhotoCallback;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;
import com.skycaster.geomapper.interfaces.SQLiteExecuteResultCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class AlertDialogUtil {

    private static final int REQUEST_TAKE_PHOTO = 3005;
    private static final int GET_IMAGE_FROM_ALBUM = 3154;
    private static AlertDialog mAlertDialog;
    private static File photoFile;

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


    public static void showAddLocTagDialog(final Context context, final LocTagListOpenHelper helper, final Runnable onConfirm){
        View rootView=View.inflate(context,R.layout.dialog_add_new_loc_tag,null);
        final EditText edt_inputName= (EditText) rootView.findViewById(R.id.dialog_add_loc_tag_edt_input_name);
        final EditText edt_inputId= (EditText) rootView.findViewById(R.id.dialog_add_loc_tag_edt_input_id);
        Button btn_confirm= (Button) rootView.findViewById(R.id.dialog_add_loc_tag_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_add_loc_tag_btn_cancel);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_inputName.getText().toString().trim();
                String id = edt_inputId.getText().toString().trim();
                if(!TextUtils.isEmpty(name)){
                    if(name.length()>15){
                        ToastUtil.showToast(context.getString(R.string.warning_exceed_len_limit));
                    }else {
                        if(TextUtils.isEmpty(id)){
                            if(helper.add(name)){
                                ToastUtil.showToast(context.getString(R.string.add_loc_tag_success));
                            }else {
                                ToastUtil.showToast(context.getString(R.string.add_loc_tag_fail));
                            }
                            mAlertDialog.dismiss();
                            onConfirm.run();
                        }else {
                            Integer tagId = Integer.valueOf(id);
                            if(tagId>99||tagId<1){
                                ToastUtil.showToast(context.getString(R.string.warning_exceed_id_limit));
                            }else {
                                if(helper.add(name, tagId)){
                                    ToastUtil.showToast(context.getString(R.string.add_loc_tag_success));
                                }else {
                                    ToastUtil.showToast(context.getString(R.string.add_loc_tag_fail));
                                }
                                mAlertDialog.dismiss();
                                onConfirm.run();
                            }
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
                mAlertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog=builder.setView(rootView).setCancelable(true).create();
        mAlertDialog.show();
    }

    public static void showEditLocTagDialog(final Context context, final LocTagListOpenHelper helper, final LocationTag tag, final Runnable onConfirm){
        View rootView=View.inflate(context,R.layout.dialog_add_new_loc_tag,null);
        TextView tv_title= (TextView) rootView.findViewById(R.id.dialog_add_loc_tag_tv_title);
        final EditText edt_inputName= (EditText) rootView.findViewById(R.id.dialog_add_loc_tag_edt_input_name);
        final EditText edt_inputId= (EditText) rootView.findViewById(R.id.dialog_add_loc_tag_edt_input_id);
        Button btn_confirm= (Button) rootView.findViewById(R.id.dialog_add_loc_tag_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_add_loc_tag_btn_cancel);
        tv_title.setText(context.getString(R.string.edit_loc_tag));
        String tagName = tag.getTagName();
        edt_inputName.setText(tagName);
        edt_inputName.setSelection(tagName.length());
        String tagId = String.valueOf(tag.getId());
        edt_inputId.setText(tagId);
        edt_inputId.setSelection(tagId.length());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_inputName.getText().toString().trim();
                String id = edt_inputId.getText().toString().trim();
                if(!TextUtils.isEmpty(name)){
                    if(name.length()>15){
                        ToastUtil.showToast(context.getString(R.string.warning_exceed_len_limit));
                    }else {
                        if(TextUtils.isEmpty(id)){
                            if(helper.alter(tag.getTagName(),tag.getId(),name, tag.getId())){
                                ToastUtil.showToast(context.getString(R.string.alter_loc_tag_success));
                            }else {
                                ToastUtil.showToast(context.getString(R.string.alter_loc_tag_fail));
                            }
                            mAlertDialog.dismiss();
                            onConfirm.run();
                        }else {
                            Integer tagId = Integer.valueOf(id);
                            if(tagId>99||tagId<1){
                                ToastUtil.showToast(context.getString(R.string.warning_exceed_id_limit));
                            }else {
                                if(helper.alter(tag.getTagName(),tag.getId(),name, tagId)){
                                    ToastUtil.showToast(context.getString(R.string.alter_loc_tag_success));
                                }else {
                                    ToastUtil.showToast(context.getString(R.string.alter_loc_tag_fail));
                                }
                                mAlertDialog.dismiss();
                                onConfirm.run();
                            }
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
                mAlertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog=builder.setView(rootView).setCancelable(true).create();
        mAlertDialog.show();
    }


    public static void showChooseImageSourceDialog(final Activity context){
        View rootView=View.inflate(context,R.layout.dialog_choose_pic_source,null);
        ImageView iv_takePhoto= (ImageView) rootView.findViewById(R.id.dialog_choose_pic_source_iv_photo);
        ImageView iv_fromGallery= (ImageView) rootView.findViewById(R.id.dialog_choose_pic_source_iv_gallery);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_choose_pic_source_btn_cancel);
        iv_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                takePhoto(context);
            }
        });

        iv_fromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                pickPhoto(context);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog=builder.setView(rootView).create();
        mAlertDialog.show();
    }

    public static void takePhoto(Activity activity){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if(!dir.exists()){
                dir.mkdirs();
            }
            photoFile=new File(dir,System.currentTimeMillis()+".png");
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0.5);
            activity.startActivityForResult(intent,REQUEST_TAKE_PHOTO);
        }else {
            ToastUtil.showToast(activity.getString(R.string.warning_sd_card_not_mounted));
        }
    }

    public static void pickPhoto(Activity activity){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent,GET_IMAGE_FROM_ALBUM);
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data, RequestTakingPhotoCallback callback) {
       if(resultCode==activity.RESULT_OK){
           if(requestCode==REQUEST_TAKE_PHOTO){
               callback.onPhotoTaken(photoFile.getAbsolutePath());
           }else if(requestCode==GET_IMAGE_FROM_ALBUM){
               callback.onPhotoTaken(UriUtil.getLocalFilePath(activity,data.getData()));
           }
       }
    }

}
