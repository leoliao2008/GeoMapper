package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;

/**
 * Created by 廖华凯 on 2017/8/22.
 */

public class AboutUsActivity extends BaseActionBarActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, AboutUsActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initChildViews() {
        mWebView= (WebView) findViewById(R.id.activity_about_us_web_view);

    }

    @Override
    protected int getActionBarTitle() {
        return R.string.about_us;
    }

    @Override
    protected void initRegularData() {
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                BaseApplication.showToast("网页加载失败，请检查网络。");
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if(!TextUtils.isEmpty(title)){
                    getSupportActionBar().setTitle(title);
                }
                super.onReceivedTitle(view, title);
            }
        });

        mWebView.loadUrl(StaticData.COMPANY_WEB_URL);

    }

    @Override
    protected void initListeners() {

    }
}
