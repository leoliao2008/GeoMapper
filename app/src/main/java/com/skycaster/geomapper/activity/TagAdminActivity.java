package com.skycaster.geomapper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.LocTagListAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.data.LocTagListOpenHelper;
import com.skycaster.geomapper.data.MappingDataTagsOpenHelper;
import com.skycaster.geomapper.data.TagListOpenHelper;
import com.skycaster.geomapper.data.TagType;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.util.ArrayList;

import static com.skycaster.geomapper.data.StaticData.RESULT_CODE_MODIFICATION_SUCCESS;

public class TagAdminActivity extends BaseActionBarActivity {

    private ListView mListView;
    private Button btn_addTag;
    private LocTagListAdapter mAdapter;
    private ArrayList<Tag> mList=new ArrayList<>();
    private TagListOpenHelper mOpenHelper;
    private LinearLayout ll_noDataWarning;
    private AlertDialog mAlertDialog;
    private static final String TAG_TYPE="Tag_Type";
    private TagType mTagType;


    public static void start(Context context,TagType tagType) {
        Intent intent = new Intent(context, TagAdminActivity.class);
        intent.putExtra(TAG_TYPE,tagType);
        context.startActivity(intent);
    }

    public static void startForResult(Activity activity,TagType tagType,int requestCode){
        Intent intent = new Intent(activity, TagAdminActivity.class);
        intent.putExtra(TAG_TYPE,tagType);
        activity.startActivityForResult(intent,requestCode);
    }

    public static void startForResult(Fragment fragment,TagType tagType,int requestCode){
        Intent intent = new Intent(fragment.getContext(), TagAdminActivity.class);
        intent.putExtra(TAG_TYPE,tagType);
        fragment.startActivityForResult(intent,requestCode);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_loc_tag_admin;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.tag_admin;
    }

    @Override
    protected void initChildViews() {
        mListView= (ListView) findViewById(R.id.activity_loc_tag_admin_lst_view);
        btn_addTag= (Button) findViewById(R.id.activity_loc_tag_admin_btn_add_tag);
        ll_noDataWarning = (LinearLayout) findViewById(R.id.activity_loc_tag_admin_ll_mask);

    }
    @Override
    protected void initRegularData() {
        mAdapter=new LocTagListAdapter(mList,this);
        mListView.setAdapter(mAdapter);
        Intent intent = getIntent();
        if(intent!=null){
            mTagType = (TagType) intent.getSerializableExtra(TAG_TYPE);
            if(mTagType ==TagType.TAG_TYPE_LOC){
                mOpenHelper = new LocTagListOpenHelper(this);
            }else {
                mOpenHelper=new MappingDataTagsOpenHelper(this);
            }
        }

        updateList();
    }

    private void updateList() {
        ArrayList<Tag> list = mOpenHelper.getTagList();
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
        if(mList.size()>0){
            ll_noDataWarning.setVisibility(View.GONE);
        }else {
            ll_noDataWarning.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void initListeners() {
        btn_addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.showAddLocTagDialog(TagAdminActivity.this, mOpenHelper, new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                        setResult(RESULT_CODE_MODIFICATION_SUCCESS);
                    }
                });
            }
        });

        ll_noDataWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.showAddLocTagDialog(TagAdminActivity.this, mOpenHelper, new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                        setResult(RESULT_CODE_MODIFICATION_SUCCESS);
                    }
                });
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(TagAdminActivity.this);
                mAlertDialog = builder
                        .setTitle(getString(R.string.please_select))
                        .setSingleChoiceItems(
                                getResources().getStringArray(R.array.array_loc_tag_options),
                                -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case 0:
                                                AlertDialogUtil.showEditLocTagDialog(
                                                        TagAdminActivity.this,
                                                        mOpenHelper,
                                                        mList.get(position),
                                                        mTagType,
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                updateList();
                                                                setResult(RESULT_CODE_MODIFICATION_SUCCESS);
                                                            }
                                                        });
                                                break;
                                            case 1:
                                                AlertDialogUtil.showHint(
                                                        TagAdminActivity.this,
                                                        getString(R.string.warning_delete_loc_tag),
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if(mOpenHelper.delete(mList.get(position))){
                                                                    showToast(getString(R.string.delete_loc_tag_success));
                                                                    setResult(RESULT_CODE_MODIFICATION_SUCCESS);
                                                                    updateList();
                                                                }else {
                                                                    showToast(getString(R.string.delete_loc_tag_fail));
                                                                }
                                                            }
                                                        },
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //do nothing.
                                                            }
                                                        }
                                                );
                                                break;
                                        }
                                        mAlertDialog.dismiss();

                                    }
                                }
                        )
                        .setNegativeButton(
                                getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAlertDialog.dismiss();
                                    }
                                }
                        )
                        .create();
                mAlertDialog.show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOpenHelper.close();
    }


}
