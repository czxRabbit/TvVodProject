package com.ts.cyd.tsreplay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends Activity {
    private WebView mWebview;
    private final Handler mHandler=new Handler();
    private static final String TAG = "WebviewActivity";
    public static String curVODUrl = "";

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebview = (WebView) findViewById(R.id.webview1);
        //声明WebSettings子类
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setUserAgentString("app/tongshi");
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式


        mWebview.addJavascriptInterface(new JsToJava(), "android");  //JsToJava是内部类，代码在后面。android 是接口名字。
        //window.android.getfs(rtn);//JS 中 的代码，这句代码的意思是，通过android这个java暴露的接口，调用getfs（）这个方法
        mWebview.clearCache(true);
        mWebview.loadUrl("http://"+HomeActivity.playsite+"/vod/index.html#/");
        //用本地服务器测试
        //mWebview.loadUrl("http://1.8.90.91/www_pc/test/indexb.html");
//        mWebview.clearCache(true);
        //mWebview.loadUrl("http://1.8.90.91/indexc.html");
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
//        Intent intent = new Intent(WebviewActivity.this, HomeActivity.class);
//        startActivity(intent);
//        finish();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        Log.i("INFO", "Activity1: onRestoreInstanceState, key1[" + savedInstanceState.getString("key1") + "]");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
        if(keyCode==4 && mWebview.canGoBack()){ //后退键
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    public class JsToJava {
        JsToJava(){}
        @JavascriptInterface
        public void getfs(String url) {//Android暴露的方法，用于 JS 调用
            //这个方法就是JS 调用java方法 ，传回 返回值，这样我们就接收到JS 返回给我们的值了

            Log.e(TAG, "js返回结果===" + url);//处理返回的结果

            curVODUrl = url;

            Intent intent = new Intent(WebviewActivity.this, VODActivity.class);
            startActivity(intent);


        }
    }


    

}
