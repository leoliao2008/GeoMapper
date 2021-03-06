package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.LocationPicListAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.customized.FullLengthListView;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.data.LocTagListOpenHelper;
import com.skycaster.geomapper.data.LocationOpenHelper;
import com.skycaster.geomapper.data.TagType;
import com.skycaster.geomapper.interfaces.RequestTakingPhotoCallback;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/6/27.
 */

public class EditLocationActivity extends BaseActionBarActivity {
    private double latitude=0;
    private double longitude=0;
    private double altitude=0;
    private boolean isBaiduCoord=true;
    private int iconStyle=0;
    private String title="null";
    private String comments="null";
    private ArrayList<String> mPicList =new ArrayList<>();
    private Tag mTag;
    private EditText edt_title;
    private Button btn_adminLocTags;
    private RadioGroup mRadioGroup;
    private EditText edt_comments;
    private Button btn_save;
    private Button btn_photo;
    private Button btn_gallery;
    private FullLengthListView mListView;
    private AppCompatSpinner spn_catalog;
    private ArrayAdapter<Tag> mSpinnerAdapter;
    private ArrayList<Tag> mTags =new ArrayList<>();
    private LocTagListOpenHelper mOpenHelper;
    private EditText edt_latitude;
    private EditText edt_longitude;
    private EditText edt_altitude;
    private LocationPicListAdapter mPicListAdapter;
    private ScrollView mScrollView;
    private java.text.SimpleDateFormat mDateFormat=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private Location mLocation;
    private Location mLocationBackUp;

    public static void start(Context context,Location location) {
        Intent intent = new Intent(context, EditLocationActivity.class);
        intent.putExtra(StaticData.LOCATION_INFO,location);
        context.startActivity(intent);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_add_location;
    }

    @Override
    protected String setActionBarTitle() {
        return getResources().getString(R.string.edit_location);
    }

    @Override
    protected void initChildViews() {
        edt_altitude= (EditText) findViewById(R.id.activity_save_location_edt_altitude);
        edt_latitude = (EditText) findViewById(R.id.activity_save_location_edt_latitude);
        edt_longitude= (EditText) findViewById(R.id.activity_save_location_edt_longitude);
        edt_title= (EditText) findViewById(R.id.activity_save_location_edt_loc_title);
        edt_comments= (EditText) findViewById(R.id.activity_save_location_edt_comments);
        btn_adminLocTags = (Button) findViewById(R.id.activity_save_location_btn_admin_loc_tags);
        btn_save= (Button) findViewById(R.id.activity_save_location_btn_save);
        btn_photo= (Button) findViewById(R.id.activity_save_location_btn_photo);
        btn_gallery= (Button) findViewById(R.id.activity_save_location_btn_gallery);
        mListView= (FullLengthListView) findViewById(R.id.activity_save_location_lst_view);
        spn_catalog= (AppCompatSpinner) findViewById(R.id.activity_save_location_spin_loc_catalogue);
        mScrollView= (ScrollView) findViewById(R.id.activity_save_location_scroll_view);
        mRadioGroup= (RadioGroup) findViewById(R.id.activity_save_location_icon_group);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mLocation = (Location) intent.getSerializableExtra(StaticData.LOCATION_INFO);
        if(mLocation!=null){
            mLocationBackUp=mLocation.clone();
            latitude=mLocation.getLatitude();
            longitude=mLocation.getLongitude();
            altitude=mLocation.getAltitude();
            isBaiduCoord=mLocation.isBaiduCoordinateSystem();
            comments=mLocation.getComments();
            iconStyle=mLocation.getIconStyle();
            mTag =mLocation.getTag();
            title= mLocation.getTitle();
        }



        edt_title.setText(title);
        edt_title.setSelection(title.length());
        edt_altitude.setText(String.valueOf(altitude));
        edt_altitude.setSelection(String.valueOf(altitude).length());
        edt_latitude.setText(String.valueOf(latitude));
        edt_latitude.setSelection(String.valueOf(latitude).length());
        edt_longitude.setText(String.valueOf(longitude));
        edt_longitude.setSelection(String.valueOf(longitude).length());
        edt_comments.setText(comments);
        edt_comments.setSelection(comments.length());

        mSpinnerAdapter =new ArrayAdapter<Tag>(this,android.R.layout.simple_spinner_item, mTags){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view= (TextView) View.inflate(EditLocationActivity.this, R.layout.item_drop_down_view, null);
                view.setText(mTags.get(position).getTagName());
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view= (TextView) View.inflate(EditLocationActivity.this, R.layout.item_drop_down_view, null);
                view.setText(mTags.get(position).getTagName());
                return view;
            }
        };
        spn_catalog.setAdapter(mSpinnerAdapter);



        mOpenHelper = new LocTagListOpenHelper(this);

        mPicListAdapter=new LocationPicListAdapter(mPicList,this);
        mListView.setAdapter(mPicListAdapter);
        if(mLocation!=null&&mLocation.getPicList()!=null){
            mPicList.addAll(mLocation.getPicList());
            mPicListAdapter.notifyDataSetChanged();
        }

        int buttonIndex=0;
        for(int i=0,count=mRadioGroup.getChildCount();i<count;i++){
            View child = mRadioGroup.getChildAt(i);
            if(child instanceof RadioButton){
                if(buttonIndex==iconStyle){
                    ((RadioButton)child).setChecked(true);
                    break;
                }
                buttonIndex++;
            }
        }


    }

    @Override
    protected void initListeners() {
        btn_adminLocTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagAdminActivity.startForResult(EditLocationActivity.this, TagType.TAG_TYPE_LOC,1235);
            }
        });

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.takePhoto(EditLocationActivity.this);
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.pickPhoto(EditLocationActivity.this);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            spn_catalog.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    spn_catalog.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    spn_catalog.setDropDownVerticalOffset(spn_catalog.getMeasuredHeight());
                }
            });
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSpinner();
    }

    private void updateSpinner() {
        ArrayList<Tag> list = mOpenHelper.getTagList();
        if(list!=null){
            mTags.clear();
            mTags.addAll(list);
            mSpinnerAdapter.notifyDataSetChanged();
            for(int i = 0, size = mTags.size(); i<size; i++){
                if(mTags !=null&& mTags.get(i).equals(mTag)){
                    spn_catalog.setSelection(i);
                    break;
                }
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== StaticData.RESULT_CODE_MODIFICATION_SUCCESS){
            if(mLocation!=null){
                mLocation.setTag((Tag) spn_catalog.getSelectedItem());
            }
            setResultOK();
        }else {
            AlertDialogUtil.onActivityResult(requestCode, resultCode, data, new RequestTakingPhotoCallback() {
                @Override
                public void onPhotoTaken(String path) {
                    mPicList.add(path);
                    updatePicList();
                }
            });
        }

    }

    private void updatePicList() {
        mPicListAdapter.notifyDataSetChanged();
        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },500);
    }

    private void submitData(){
        boolean isValid=true;

        String title = edt_title.getText().toString().trim();
        if(TextUtils.isEmpty(title)){
            isValid=false;
        }

        Tag tag =null;
        try{
            tag = mTags.get(spn_catalog.getSelectedItemPosition());
        }catch (Exception e){
            isValid=false;
        }
        int iconType=0;
        int childCount = mRadioGroup.getChildCount();
        int buttonIndex=0;
        for(int i=0;i<childCount;i++){
            View view = mRadioGroup.getChildAt(i);
            if(view instanceof RadioButton){
                RadioButton temp= (RadioButton) view;
                if(temp.isChecked()){
                    iconType=buttonIndex;
                    break;
                }
                buttonIndex++;
            }
        }
        double latitude=0;
        double longitude=0;
        double altitude=0;
        try {
            latitude=Double.parseDouble(edt_latitude.getText().toString().trim());
            longitude=Double.parseDouble(edt_longitude.getText().toString().trim());
            altitude=Double.parseDouble(edt_altitude.getText().toString().trim());
        }catch (Exception e){
            isValid=false;
        }

        String comments=edt_comments.getText().toString().trim();
        if(TextUtils.isEmpty(comments)){
            isValid=false;
        }

        if(isValid){
            mLocation.setTitle(title);
            mLocation.setIconStyle(iconType);
            mLocation.setLatitude(latitude);
            mLocation.setLongitude(longitude);
            mLocation.setAltitude(altitude);
            mLocation.setComments(comments);
            mLocation.setPicList(mPicList);
            mLocation.setBaiduCoordinateSystem(isBaiduCoord);
            mLocation.setTag(tag);
            mLocation.setSubmitDate(mDateFormat.format(new Date()));
            if(LocationOpenHelper.getInstance(this).alter(mLocationBackUp,mLocation)){
                showToast(getString(R.string.submit_success));
                setResultOK();
                onBackPressed();
            }else {
                showToast(getString(R.string.submit_fails));
            }
        }else {
            showToast(getString(R.string.warning_invalid_input));
        }
    }

    private void setResultOK(){
        Intent intent=new Intent();
        intent.putExtra(StaticData.LOCATION_INFO,mLocation);
        setResult(StaticData.RESULT_CODE_MODIFICATION_SUCCESS,intent);
    }

}
