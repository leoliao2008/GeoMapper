package com.skycaster.geomapper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.TagAdminActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.data.MappingDataTagsOpenHelper;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.data.TagType;
import com.skycaster.geomapper.exceptions.EmptyInputException;
import com.skycaster.geomapper.exceptions.NullTagException;
import com.skycaster.geomapper.interfaces.GetGeoInfoListener;
import com.skycaster.geomapper.models.BaiduMapModel;
import com.skycaster.geomapper.util.MapUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.skycaster.geomapper.data.StaticData.EXTRA_COORDINATES;
import static com.skycaster.geomapper.data.StaticData.MAPPING_DATA_SOURCE;


/**
 * Created by 廖华凯 on 2017/7/13.
 */

public class MappingDataBasicElementsFragment extends BaseFragment {
    private EditText edt_inputTitle;
    private EditText edt_inputAddress;
    private EditText edt_inputAdjacent;
    private EditText edt_inputComments;
    private AppCompatSpinner spin_Tag;
    private Button btn_defineNewTag;
    private ArrayList<Tag> mTagList=new ArrayList<>();
    private ArrayAdapter<Tag> mSpinnerAdapter;
    private Tag mTag;
    private static final int REQUEST_ADMIN_TAGS =3654;
    private MappingDataTagsOpenHelper mTagsOpenHelper;
    private ArrayList<LatLng> coordinates;
    private SimpleDateFormat mDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private MappingData mMappingData;
    private boolean isTagModify;

    public MappingDataBasicElementsFragment(Context context, ArrayList<LatLng>coordinates,@Nullable MappingData source) {
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(EXTRA_COORDINATES,coordinates);
        bundle.putParcelable(MAPPING_DATA_SOURCE,source);
        setArguments(bundle);
    }


    @Override
    protected int setContentView() {
        return R.layout.fragment_basic_mapping_data;
    }

    @Override
    protected void initView() {
        edt_inputTitle= (EditText) findViewById(R.id.fragment_save_mapping_data_edt_title);
        edt_inputAddress= (EditText) findViewById(R.id.fragment_save_mapping_data_edt_address);
        edt_inputAdjacent= (EditText) findViewById(R.id.fragment_save_mapping_data_edt_adjacent);
        edt_inputComments= (EditText) findViewById(R.id.fragment_save_mapping_data_edt_comments);
        spin_Tag= (AppCompatSpinner) findViewById(R.id.fragment_save_mapping_data_spin_tag);
        btn_defineNewTag= (Button) findViewById(R.id.fragment_save_mapping_data_btn_define_new_tag);

    }

    @Override
    protected void initData(Bundle arguments) {
        coordinates=arguments.getParcelableArrayList(EXTRA_COORDINATES);
        mMappingData = arguments.getParcelable(MAPPING_DATA_SOURCE);

        mTagsOpenHelper=new MappingDataTagsOpenHelper(getContext());
        mSpinnerAdapter=new ArrayAdapter<Tag>(getContext(), R.layout.item_drop_down_view, mTagList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view= (TextView) View.inflate(getContext(), R.layout.item_drop_down_view, null);
                view.setText(mTagList.get(position).getTagName());
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view= (TextView) View.inflate(getContext(), R.layout.item_drop_down_view, null);
                view.setText(mTagList.get(position).getTagName());
                return view;
            }
        };
        mSpinnerAdapter.setDropDownViewResource(R.layout.item_drop_down_view);
        spin_Tag.setAdapter(mSpinnerAdapter);
        spin_Tag.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                spin_Tag.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int offset=0;
                offset+=spin_Tag.getMeasuredHeight();
                spin_Tag.setDropDownVerticalOffset(offset);

            }
        });
        updateTagList();

        if(mMappingData==null){
            LatLng latLng = coordinates.get(0);
            BDLocation bdLocation = new BaiduMapModel().convertToBaiduCoord(latLng);
            latLng=new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapUtil.getAdjacentInfoByLatlng(latLng, new GetGeoInfoListener() {
                @Override
                public void onGetResult(ReverseGeoCodeResult result) {
                    initUIWithNewlyCreatedData(result);
                }

                @Override
                public void onNoResult() {
                    initUIWithNewlyCreatedData(null);
                }
            });
        }else {
            initUIWithExistingData(mMappingData);
        }
    }

    private void initUIWithNewlyCreatedData(@Nullable ReverseGeoCodeResult info) {

        String unknownAddress = BaseApplication.getContext().getString(R.string.unknown_address);
        String title= unknownAddress;
        String address= unknownAddress;
        String adjacent= unknownAddress;
        String comments=BaseApplication.getContext().getString(R.string.data_generate_at)+mDateFormat.format(new Date());
        if(info!=null){
            ReverseGeoCodeResult.AddressComponent detail = info.getAddressDetail();
            title= detail.city+detail.district+detail.street+detail.streetNumber;
            address=info.getAddress();
            adjacent=info.getBusinessCircle()+info.getSematicDescription();
        }

        edt_inputTitle.setText(title);
        edt_inputTitle.setSelection(title.length());

        edt_inputAddress.setText(address);
        edt_inputAddress.setSelection(address.length());

        edt_inputAdjacent.setText(adjacent);
        edt_inputAdjacent.setSelection(adjacent.length());

        edt_inputComments.setText(comments);
        edt_inputComments.setSelection(comments.length());
    }

    private void initUIWithExistingData(@NonNull MappingData data) {
        String title=data.getTitle();
        String address=data.getAddress();
        String adjacent=data.getAdjacentLoc();
        String comments=data.getComment();
        int tagId=data.getTagID();

        edt_inputTitle.setText(title);
        edt_inputTitle.setSelection(title.length());

        edt_inputAddress.setText(address);
        edt_inputAddress.setSelection(address.length());

        edt_inputAdjacent.setText(adjacent);
        edt_inputAdjacent.setSelection(adjacent.length());

        edt_inputComments.setText(comments);
        edt_inputComments.setSelection(comments.length());

        spin_Tag.setSelection(-1);
        for(int i=0,size=mTagList.size();i<size;i++){
            int id = mTagList.get(i).getId();
            if(tagId==id){
                spin_Tag.setSelection(i);
                break;
            }
        }
    }

    @Override
    protected void initListeners() {
        btn_defineNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagAdminActivity.startForResult(MappingDataBasicElementsFragment.this, TagType.TAG_TYPE_MAPPING_DATA, REQUEST_ADMIN_TAGS);
            }
        });

        spin_Tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tag tag = mTagList.get(position);
                if(mMappingData!=null){
                    mMappingData.setTagName(tag.getTagName());
                    mMappingData.setTagID(tag.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== REQUEST_ADMIN_TAGS &&resultCode== StaticData.RESULT_CODE_MODIFICATION_SUCCESS){
            isTagModify=true;
            updateTagList();
        }
    }

    private void updateTagList() {
        ArrayList<Tag> tagList = mTagsOpenHelper.getTagList();
        mTagList.clear();
        mTagList.addAll(tagList);
        mSpinnerAdapter.notifyDataSetChanged();
        if(mMappingData!=null){
            for(int i=0;i<mTagList.size();i++){
                if (mMappingData.getTagID()==mTagList.get(i).getId()){
                    spin_Tag.setSelection(i);
                    break;
                }
            }
        }
    }



    public boolean isTagModify() {
        return isTagModify;
    }

    public MappingData updateBasicData(MappingData data) throws EmptyInputException,NullTagException{
        String title=edt_inputTitle.getText().toString().trim();
        if(TextUtils.isEmpty(title)){
            throw new EmptyInputException(BaseApplication.getContext().getString(R.string.warning_empty_input_is_not_allowed));
        }
        String address = edt_inputAddress.getText().toString().trim();
        if(TextUtils.isEmpty(address)){
            address=BaseApplication.getContext().getString(R.string.no_available_address);
        }
        String adjacent = edt_inputAdjacent.getText().toString().trim();
        if(TextUtils.isEmpty(adjacent)){
            adjacent=BaseApplication.getContext().getString(R.string.no_available_address);

        }
        String comments = edt_inputComments.getText().toString().trim();
        if(TextUtils.isEmpty(comments)){
            comments="null";
        }
        Tag tag= (Tag) spin_Tag.getSelectedItem();
        if(tag==null){
            throw new NullTagException(BaseApplication.getContext().getString(R.string.warning_null_tag_is_not_allowed));
        }else {
            mTag=tag;
        }
        data.setTitle(title);
        data.setAddress(address);
        data.setAdjacentLoc(adjacent);
        data.setComment(comments);
        data.setTagName(tag.getTagName());
        data.setTagID(tag.getId());
        return data;
    }
}
