package com.skycaster.geomapper.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.util.ImageUtil;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class PicIteratorFragment extends BaseFragment {
    private ImageView mImageView;
    private static final String PIC_PATH="pic_path";
    private String mPath;

    public PicIteratorFragment(String picPath) {
        Bundle bundle=new Bundle();
        bundle.putString(PIC_PATH,picPath);
        setArguments(bundle);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_pic;
    }

    @Override
    protected void initView() {
        mImageView= (ImageView) findViewById(R.id.fragment_pic_iterator_iv_pic);

    }

    @Override
    protected void initData(Bundle arguments) {
        mPath = arguments.getString(PIC_PATH);
        Bitmap bitmap = ImageUtil.getFixedWidthBitmap(mPath, BaseApplication.getDisplayMetrics().widthPixels);
        if(bitmap!=null){
            mImageView.setImageBitmap(bitmap);
        }else {
            mImageView.setImageResource(R.drawable.pic_file_deleted);
        }

    }

    @Override
    protected void initListeners() {

    }
}
