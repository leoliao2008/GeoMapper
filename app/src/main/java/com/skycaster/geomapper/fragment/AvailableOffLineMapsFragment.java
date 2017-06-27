package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.OffLineMapAdminActivity;
import com.skycaster.geomapper.adapter.AvailableOffLineMapListAdapter;
import com.skycaster.geomapper.base.BaseFragment;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class AvailableOffLineMapsFragment extends BaseFragment {
    private ListView mListView;
    private OffLineMapAdminActivity mContext;
    private AvailableOffLineMapListAdapter mAdapter;
    private EditText edt_inputSearch;

    public AvailableOffLineMapsFragment(OffLineMapAdminActivity context) {
        mContext=context;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_available_ol_map;
    }

    @Override
    protected void initView() {
        mListView= (ListView) findViewById(R.id.fragment_available_ol_map_lst_view_all);
        edt_inputSearch= (EditText) findViewById(R.id.fragment_available_ol_map_edt_search_input);
    }

    @Override
    protected void initData(Bundle arguments) {
        mAdapter=new AvailableOffLineMapListAdapter(mContext.getAvailableMapList(),mContext);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
        edt_inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if(!TextUtils.isEmpty(input)){
                    mContext.searchCity(input);
                }else {
                    mContext.updateAvailableView();
                }
            }
        });

    }

    public void updateContents(){
        mAdapter.notifyDataSetChanged();
    }

}
