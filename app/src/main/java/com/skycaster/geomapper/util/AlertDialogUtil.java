package com.skycaster.geomapper.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.FileBrowserAdapter;
import com.skycaster.geomapper.adapter.LocationListAdapter;
import com.skycaster.geomapper.adapter.RouteAdminAdapter;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.data.LocationOpenHelper;
import com.skycaster.geomapper.data.MappingDataOpenHelper;
import com.skycaster.geomapper.data.RouteIndexOpenHelper;
import com.skycaster.geomapper.data.TagListOpenHelper;
import com.skycaster.geomapper.data.TagType;
import com.skycaster.geomapper.interfaces.AlertDialogUtilsCallBack;
import com.skycaster.geomapper.interfaces.CoordinateItemEditCallBack;
import com.skycaster.geomapper.interfaces.CreateCoordinateCallBack;
import com.skycaster.geomapper.interfaces.RequestTakingPhotoCallback;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;
import com.skycaster.geomapper.interfaces.SQLiteExecuteResultCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.skycaster.geomapper.R.string.confirm;
import static com.skycaster.geomapper.R.string.options;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class AlertDialogUtil {

    private static final int REQUEST_TAKE_PHOTO = 3005;
    private static final int GET_IMAGE_FROM_ALBUM = 3154;
    private static AlertDialog mAlertDialog;
    private static File photoFile;

    public static void showStandardDialog(Context context, String msg){
        showStandardDialog(context,msg,null);
    }

    public static void showStandardDialog(Context context, String msg, @Nullable Runnable positive){
        showStandardDialog(context,msg,positive,null);

    }

    public static void showStandardDialog(Context context, String msg, @Nullable final Runnable positive, @Nullable final Runnable negative){
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
        builder.setPositiveButton(confirm, temp);
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


    public static void showAddLocTagDialog(final Context context, final TagListOpenHelper helper, final Runnable onSuccess){
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
                                mAlertDialog.dismiss();
                                onSuccess.run();
                            }else {
                                ToastUtil.showToast(context.getString(R.string.add_loc_tag_fail));
                            }
                        }else {
                            Integer tagId = Integer.valueOf(id);
                            if(tagId>99||tagId<1){
                                ToastUtil.showToast(context.getString(R.string.warning_exceed_id_limit));
                            }else {
                                if(helper.add(new Tag(name,tagId))){
                                    ToastUtil.showToast(context.getString(R.string.add_loc_tag_success));
                                    mAlertDialog.dismiss();
                                    onSuccess.run();
                                }else {
                                    ToastUtil.showToast(context.getString(R.string.add_loc_tag_fail));
                                }

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

    public static void showEditLocTagDialog(final Context context, final TagListOpenHelper helper, final Tag tag, final TagType type, final Runnable onSuccess){
        View rootView=View.inflate(context,R.layout.dialog_add_new_loc_tag,null);
        TextView tv_title= (TextView) rootView.findViewById(R.id.dialog_add_loc_tag_tv_title);
        final EditText edt_inputName= (EditText) rootView.findViewById(R.id.dialog_add_loc_tag_edt_input_name);
        final EditText edt_inputId= (EditText) rootView.findViewById(R.id.dialog_add_loc_tag_edt_input_id);
        Button btn_confirm= (Button) rootView.findViewById(R.id.dialog_add_loc_tag_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_add_loc_tag_btn_cancel);
        LinearLayout ll_id= (LinearLayout) rootView.findViewById(R.id.dialog_add_loc_tag_ll_id);
        tv_title.setText(context.getString(R.string.edit_loc_tag));
        ll_id.setVisibility(View.GONE);
        final String tagName = tag.getTagName();
        edt_inputName.setText(tagName);
        edt_inputName.setSelection(tagName.length());
        final String tagId = String.valueOf(tag.getId());
        edt_inputId.setText(tagId);
        edt_inputId.setSelection(tagId.length());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edt_inputName.getText().toString().trim();
                if(!TextUtils.isEmpty(newName)){
                    if(!newName.equals(tagName)){
                        boolean result = helper.alter(new Tag(newName, tag.getId()));
                        if(result){
                            switch (type){
                                case TAG_TYPE_LOC:
                                    LocationOpenHelper helper1=LocationOpenHelper.getInstance(context);
                                    int Affected = helper1.updateTag(tagName, newName);
                                    helper1.close();
                                    ToastUtil.showToast(context.getString(R.string.edited_and_affected_rows_are)+Affected);
                                    break;
                                case TAG_TYPE_MAPPING_DATA:
                                    MappingDataOpenHelper helper2=new MappingDataOpenHelper(context);
                                    int rowsAffected = helper2.editTag(tagName, newName);
                                    helper2.close();
                                    ToastUtil.showToast(context.getString(R.string.edited_and_affected_rows_are)+rowsAffected);
                                    break;
                                default:
                                    break;
                            }
                            mAlertDialog.dismiss();
                            onSuccess.run();
                        }else {
                            ToastUtil.showToast(context.getString(R.string.add_loc_tag_fail));
                        }
                    }else {
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
                mAlertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog=builder.setView(rootView).setCancelable(true).create();
        mAlertDialog.show();
    }


    public static void showChooseImageSourceDialog(final Context context){
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

    public static void showChooseImageSourceDialog(final Fragment fragment){
        View rootView=View.inflate(fragment.getContext(),R.layout.dialog_choose_pic_source,null);
        ImageView iv_takePhoto= (ImageView) rootView.findViewById(R.id.dialog_choose_pic_source_iv_photo);
        ImageView iv_fromGallery= (ImageView) rootView.findViewById(R.id.dialog_choose_pic_source_iv_gallery);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_choose_pic_source_btn_cancel);
        iv_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                takePhoto(fragment);
            }
        });

        iv_fromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                pickPhoto(fragment);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(fragment.getContext());
        mAlertDialog=builder.setView(rootView).create();
        mAlertDialog.show();
    }

    public static void takePhoto(Object context){
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
            if(context instanceof Activity){
                ((Activity)context).startActivityForResult(intent,REQUEST_TAKE_PHOTO);
            }else if(context instanceof Fragment){
                ((Fragment)context).startActivityForResult(intent,REQUEST_TAKE_PHOTO);
            }

        }else {
            ToastUtil.showToast(BaseApplication.getContext().getString((R.string.warning_sd_card_not_mounted)));
        }
    }

    public static void pickPhoto(Object context){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if(context instanceof Activity){
            ((Activity)context).startActivityForResult(intent,GET_IMAGE_FROM_ALBUM);
        }else if(context instanceof Fragment){
            ((Fragment)context).startActivityForResult(intent,GET_IMAGE_FROM_ALBUM);
        }
    }

    public static void showAddCoordinateDialog(final Context context, final CreateCoordinateCallBack callBack){
        View rootView=View.inflate(context,R.layout.dialog_mapping_add_new_coordinate,null);
        final EditText edt_lat= (EditText) rootView.findViewById(R.id.dialog_mapping_add_coord_edt_input_lat);
        final EditText edt_lng= (EditText) rootView.findViewById(R.id.dialog_mapping_add_coord_input_lng);
        Button btn_confirm= (Button) rootView.findViewById(R.id.dialog_mapping_add_coord_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_mapping_add_coord_btn_cancel);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_lat = edt_lat.getText().toString().trim();
                String str_lng=edt_lng.getText().toString().trim();
                if(!TextUtils.isEmpty(str_lat)&&!TextUtils.isEmpty(str_lng)){
                    double lat=Double.parseDouble(str_lat);
                    double lng=Double.parseDouble(str_lng);
                    callBack.onCoordinateCreated(new LatLng(lat,lng));
                    mAlertDialog.dismiss();
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
        mAlertDialog=builder.setView(rootView).create();
        mAlertDialog.show();
    }

    public static void showEditCoordinateDialog(final Context context, final LatLng latLng, final CreateCoordinateCallBack callBack){
        View rootView=View.inflate(context,R.layout.dialog_mapping_add_new_coordinate,null);
        final EditText edt_lat= (EditText) rootView.findViewById(R.id.dialog_mapping_add_coord_edt_input_lat);
        final EditText edt_lng= (EditText) rootView.findViewById(R.id.dialog_mapping_add_coord_input_lng);
        Button btn_confirm= (Button) rootView.findViewById(R.id.dialog_mapping_add_coord_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_mapping_add_coord_btn_cancel);
        TextView tv_title= (TextView) rootView.findViewById(R.id.dialog_mapping_add_coord_tv_title);
        tv_title.setText(context.getString(R.string.edit_coord));
        String lat=String.valueOf(latLng.latitude);
        edt_lat.setText(lat);
        edt_lat.setSelection(lat.length());
        String lng=String.valueOf(latLng.longitude);
        edt_lng.setText(lng);
        edt_lng.setSelection(lng.length());
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_lat = edt_lat.getText().toString().trim();
                String str_lng=edt_lng.getText().toString().trim();
                if(!TextUtils.isEmpty(str_lat)&&!TextUtils.isEmpty(str_lng)){
                    double lat=Double.parseDouble(str_lat);
                    double lng=Double.parseDouble(str_lng);
                    LatLng result=new LatLng(lat,lng);
                    callBack.onCoordinateCreated(result);
                    mAlertDialog.dismiss();
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
        mAlertDialog=builder.setView(rootView).create();
        mAlertDialog.show();
    }


    public static void onActivityResult(int requestCode, int resultCode, Intent data, RequestTakingPhotoCallback callback) {
       if(resultCode==Activity.RESULT_OK){
           if(requestCode==REQUEST_TAKE_PHOTO){
               callback.onPhotoTaken(photoFile.getAbsolutePath());
           }else if(requestCode==GET_IMAGE_FROM_ALBUM){
               callback.onPhotoTaken(UriUtil.getLocalFilePath(data.getData()));
           }
       }
    }

    public static void showSaveMappingDataDialog(final Context context, final Runnable onConfirm){
        View rootView=View.inflate(context,R.layout.dialog_save_mapping_data,null);
        final EditText edt_inputTitle= (EditText) rootView.findViewById(R.id.dialog_save_mapping_data_edt_input_title);
        Button btn_confirm= (Button) rootView.findViewById(R.id.dialog_save_mapping_data_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.dialog_save_mapping_data_btn_cancel);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = edt_inputTitle.getText().toString().trim();
                if(!TextUtils.isEmpty(s)){
                    onConfirm.run();
                    mAlertDialog.dismiss();
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
        mAlertDialog=builder.setView(rootView).create();
        mAlertDialog.show();
    }

    public static void showLatLngOptions(final Context context, final LatLng latLng, final int position, final CoordinateItemEditCallBack callback){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog = builder.setTitle(context.getString(options))
                .setSingleChoiceItems(
                        R.array.array_latlng_options,
                        -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAlertDialog.dismiss();
                                switch (which) {
                                    case 0:
                                        //插入
                                        showAddCoordinateDialog(context, new CreateCoordinateCallBack() {
                                            @Override
                                            public void onCoordinateCreated(LatLng location) {
                                                callback.onInsertNewLatlng(position + 1, location);
                                            }
                                        });
                                        break;
//                                    case 1:
//                                        callback.onLongClickToGetLatlng(position+1);
//                                        break;
                                    case 1:
                                        //导入
                                        selectAvailableLocation(context, new CreateCoordinateCallBack() {
                                            @Override
                                            public void onCoordinateCreated(LatLng location) {
                                                callback.onInsertNewLatlng(position + 1, location);
                                            }
                                        });
                                        break;
                                    case 2:
                                        //另存
                                        callback.onSaveAs(position, latLng);
                                        break;
                                    case 3:
                                        //编辑
                                        showEditCoordinateDialog(context, latLng, new CreateCoordinateCallBack() {
                                            @Override
                                            public void onCoordinateCreated(LatLng location) {
                                                callback.onEdit(position, location);
                                            }
                                        });
                                        break;
                                    case 4:
                                        //删除
                                        callback.onDeleteLatlng(position, latLng);
                                        break;
                                }
                            }
                        }
                )
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                })
                .create();
        mAlertDialog.show();
    }

    private static void selectAvailableLocation(Context context, final CreateCoordinateCallBack callBack){
        LocationOpenHelper helper=LocationOpenHelper.getInstance(context);
        final ArrayList<Location> locations = helper.getLocationList();
        helper.close();
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.available_locations));
        if(locations.size()>0){
            builder.setSingleChoiceItems(
                    new LocationListAdapter(locations,context),
                    -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlertDialog.dismiss();
                            Location location = locations.get(which);
                            callBack.onCoordinateCreated(new LatLng(location.getLatitude(),location.getLongitude()));
                        }
                    }
            );
        }else {
            builder.setMessage(context.getString(R.string.warning_location_record_is_empty));
        }
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog=builder.create();
        mAlertDialog.show();

    }


    public static void showChooseSK9042BaudRate(Context context,AlertDialogUtilsCallBack callBack) {
        showSingleOptionFromSpinnerDialog(
                context,
                context.getResources().getString(R.string.please_select_bd_rate),
                new String[]{"9600","57600","19200","115200"},
                callBack);
    }

    private static void showSingleOptionFromSpinnerDialog(Context context, String title, final String[] options, final AlertDialogUtilsCallBack callBack) {
        View rootView=View.inflate(context,R.layout.dialog_single_option_spinner,null);
        TextView tv_title= (TextView) rootView.findViewById(R.id.tv_title);
        AppCompatSpinner spinner= (AppCompatSpinner) rootView.findViewById(R.id.spinner);
        Button btn_confirm= (Button) rootView.findViewById(R.id.btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.btn_cancel);

        ArrayAdapter<String>adapter=new ArrayAdapter<String>(
                context,
                R.layout.item_simple_list_view_item,
                options
        );
        adapter.setDropDownViewResource(R.layout.item_simple_list_view_item);
        spinner.setAdapter(adapter);
        final String[] result=new String[1];
        tv_title.setText(title);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                result[0]=options[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                result[0]=options[0];

            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onGetData(result[0]);
                mAlertDialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog=builder.setCancelable(false).setView(rootView).create();
        mAlertDialog.show();
    }

    public static void showSetSk9042Freq(Context context,AlertDialogUtilsCallBack callBack) {
        showGetSingleInputDialog(
                context,
                context.getResources().getString(R.string.please_enter_freq_value),
                InputType.TYPE_CLASS_NUMBER,
                callBack);
    }

    private static void showGetSingleInputDialog(Context context, String title, int inputType, final AlertDialogUtilsCallBack callBack) {
        View rootView=View.inflate(context,R.layout.dialog_single_input,null);
        TextView tv_title= (TextView) rootView.findViewById(R.id.tv_title);
        final EditText editText= (EditText) rootView.findViewById(R.id.edit_text);
        Button btn_confirm= (Button) rootView.findViewById(R.id.btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.btn_cancel);

        tv_title.setText(title);
        editText.setInputType(inputType);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(input)){
                    callBack.onGetData(input);
                    mAlertDialog.dismiss();
                }else {
                    ToastUtil.showToast("输入值不能为空");
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
        mAlertDialog=builder.setView(rootView).setCancelable(false).create();
        mAlertDialog.show();
    }


    public static void showChooseSk9042RevMode(Context context, AlertDialogUtilsCallBack callBack) {
        showSingleOptionFromSpinnerDialog(
                context,
                context.getResources().getString(R.string.please_set_receive_mode),
                new String[]{"2","3","4"},
                callBack
        );
    }

    public static void showSetSk9042LogLevel(Context context, AlertDialogUtilsCallBack callBack) {
        showSingleOptionFromSpinnerDialog(
                context,
                context.getResources().getString(R.string.please_select_log_level),
                new String[]{"0","1","2","3","4","5"},
                callBack
        );
    }

    public static void showCheckSk9042Freq(Context context, AlertDialogUtilsCallBack callBack) {
        showGetSingleInputDialog(
                context,
                context.getResources().getString(R.string.please_enter_freq_value),
                InputType.TYPE_CLASS_NUMBER,
                callBack
        );
    }

    /**
     * 弹出一个窗口，点选升级文件，开始升级SK9042的系统
     */
    public static void showPickSK9042UpgradeFileWindow(Context context, final AlertDialogUtilsCallBack callBack) {
        //views
        View rootView=View.inflate(context,R.layout.dialog_pick_src_file,null);
        Button btn_back= (Button) rootView.findViewById(R.id.btn_back);
        Button btn_exit= (Button) rootView.findViewById(R.id.btn_exit);
        final TextView tv_path= (TextView) rootView.findViewById(R.id.tv_path);
        RecyclerView browser= (RecyclerView) rootView.findViewById(R.id.browser);
        //data
        File directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        tv_path.setText(directory.getAbsolutePath());
        File[] files = directory.listFiles();
        final ArrayList<File>list=new ArrayList<>();
        for(File temp:files){
            list.add(temp);
        }
        final FileBrowserAdapter adapter=new FileBrowserAdapter(list,context);
        GridLayoutManager layoutManager=new GridLayoutManager(context,5,GridLayoutManager.VERTICAL,false);
        browser.setLayoutManager(layoutManager);
        browser.setAdapter(adapter);
        //listener
        adapter.setOnItemClickListener(new FileBrowserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                File file = list.get(position);
                if(file.isFile()){
                    callBack.onGetFile(file);
                    mAlertDialog.dismiss();
                }else {
                    tv_path.setText(file.getAbsolutePath());
                    adapter.changeDir(file);
                }

            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.back();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        //build dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog=builder.setView(rootView).setCancelable(false).create();
        mAlertDialog.show();
    }
}
