package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.LocTagListAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.bean.LocationTag;
import com.skycaster.geomapper.data.LocTagListOpenHelper;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.util.ArrayList;

import static com.skycaster.geomapper.data.Constants.CONTENT_CHANGED;

public class LocTagAdminActivity extends BaseActionBarActivity {

    private ListView mListView;
    private Button btn_addTag;
    private LocTagListAdapter mAdapter;
    private ArrayList<LocationTag> mList=new ArrayList<>();
    private LocTagListOpenHelper mOpenHelper;
    private LinearLayout ll_noDataWarning;
    private AlertDialog mAlertDialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, LocTagAdminActivity.class);
        context.startActivity(starter);
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
        mOpenHelper = new LocTagListOpenHelper(this);
        updateList();
    }

    private void updateList() {
        ArrayList<LocationTag> list = mOpenHelper.getTagList();
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
                AlertDialogUtil.showAddLocTagDialog(LocTagAdminActivity.this, mOpenHelper, new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                        setResult(CONTENT_CHANGED);
                    }
                });
            }
        });

        ll_noDataWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.showAddLocTagDialog(LocTagAdminActivity.this, mOpenHelper, new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                        setResult(CONTENT_CHANGED);
                    }
                });
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LocTagAdminActivity.this);
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
                                                        LocTagAdminActivity.this,
                                                        mOpenHelper,
                                                        mList.get(position),
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                updateList();
                                                                setResult(CONTENT_CHANGED);
                                                            }
                                                        });
                                                break;
                                            case 1:
                                                AlertDialogUtil.showHint(
                                                        LocTagAdminActivity.this,
                                                        getString(R.string.warning_delete_loc_tag),
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if(mOpenHelper.delete(mList.get(position))){
                                                                    showToast(getString(R.string.delete_loc_tag_success));
                                                                    setResult(CONTENT_CHANGED);
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
