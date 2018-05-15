package com.ts.cyd.tsreplay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ts.cyd.tsreplay.fragment.ErrorFragment;
import com.ts.cyd.tsreplay.fragment.InfoFragment;
import com.ts.cyd.tsreplay.fragment.ReplayControllerFragment;
import com.ts.cyd.tsreplay.fragment.SidebarFragment;
import com.ts.cyd.tsreplay.provider.InfoProvider;
import com.ts.cyd.tsreplay.widget.media.IjkVideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import android.content.Intent;
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements SidebarFragment.PlayVideoCallBack,IjkVideoView.VideoListener,IMediaPlayer.OnCompletionListener,IMediaPlayer.OnErrorListener{
    private static final String TAG = "MainActivity";

    public static int curNum = -1;

    private static final int HIDE_REPLAY_FRAGMENT = 0;
    private static final int HIDE_INFO_FRAGMENT = 1;

    private static final int HIDE_INFO_NUMBER = 2;
    private static final int SEEK_VIDEO = 3;
    private static final int RETRY = 4;
    private static final int UPDATE=5;


    private SidebarFragment mSidebarFragment;
    private ReplayControllerFragment mReplayControllerFragment;
    private InfoFragment mInfoFragment;
    private ErrorFragment mErrorFragment;

    private IjkVideoView mVideoView;

    private boolean isLive = true;

    private static int curDurationLevel = 0;  // max 20;
    private static int curDuration = 0;
    private static boolean seekFlag = false;

    private static String curUrl = "";


    private final String VersionUrl = "http://tv.com/app/version.txt";
    private Update update;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private ProgressDialog progressDialog;
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent)
        {
            ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isAvailable() )
            {

                progressDialog.dismiss();
                mHandler.sendEmptyMessage(RETRY);
                Toast.makeText(context, "网络已连接", Toast.LENGTH_SHORT).show();

            }
            else
            {   progressDialog.setTitle("网络未连接");
                progressDialog.setMessage("请检查网线是否正确连接");
                progressDialog.setCancelable(false);
                progressDialog.show();

                mVideoView.stopPlayback();

                mVideoView.stopBackgroundPlay();

            }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        SysApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);


        new Thread (new Runnable() {
            @Override
            public void run() {
                download(VersionUrl);


            }
        }).start();



        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);
        progressDialog =new ProgressDialog(MainActivity.this);

        {
            FragmentManager fm = getSupportFragmentManager();
            mSidebarFragment = (SidebarFragment) fm.findFragmentById(R.id.fragment_siderbar);

            if (mSidebarFragment == null) {
                mSidebarFragment = new SidebarFragment();
                mSidebarFragment.setProvider(new InfoProvider());
                fm.beginTransaction()
                        .add(R.id.fragment_siderbar, mSidebarFragment)
                        .commit();
            }

            //init replay controller fragment
            if (mReplayControllerFragment == null) {
                mReplayControllerFragment = new ReplayControllerFragment();
                // mReplayControllerFragment.setProvider(new InfoProvider());
                fm.beginTransaction()
                        .add(R.id.fragment_replaycontroller, mReplayControllerFragment)
                        .commit();
            }

            if (mReplayControllerFragment.getUserVisibleHint()) {
                fm.beginTransaction().hide(mReplayControllerFragment).commit();
            }

            //init infor fragment
            if (mInfoFragment == null) {
                mInfoFragment = new InfoFragment();
                // mReplayControllerFragment.setProvider(new InfoProvider());
                fm.beginTransaction()
                        .add(R.id.fragment_info, mInfoFragment)
                        .commit();
            }

            if (mInfoFragment.getUserVisibleHint()) {
                fm.beginTransaction().hide(mInfoFragment).commit();
            }

            // init error fragment
            if (mErrorFragment == null) {
                mErrorFragment = new ErrorFragment();
                // mReplayControllerFragment.setProvider(new InfoProvider());
                fm.beginTransaction()
                        .add(R.id.fragment_error, mErrorFragment)
                        .commit();
            }

            if (mErrorFragment.getUserVisibleHint()) {
                fm.beginTransaction().hide(mErrorFragment).commit();
            }

            // init player
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//
            mVideoView = (IjkVideoView) findViewById(R.id.video_view);
            mVideoView.setVideoListener(this);

            mVideoView.setOnCompletionListener(this);
            mVideoView.setOnErrorListener(this);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode ,String[] permissions , int[] grantResults)
    {
        switch (requestCode){
            case 1 :
                if (grantResults.length>0 && grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public boolean onKeyUp(int keyCode , KeyEvent event)
    {

         mSidebarFragment.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode,event);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG,"keycode:" + keyCode);
        mSidebarFragment.onKeyDown(keyCode,event);
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onStop() {
        super.onStop();
            mVideoView.release(true);
            mVideoView.stopPlayback();

            mVideoView.stopBackgroundPlay();

            IjkMediaPlayer.native_profileEnd();  //释放动态库

    }

    @Override
    protected void onDestroy() {
        SysApplication.getInstance().exit();
        super.onDestroy();
        if(mVideoView.isPlaying())
        {
            mVideoView.stopPlayback();
        }

        mVideoView.release(true);

        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        mVideoView.stopPlayback();
        mVideoView.stopBackgroundPlay();
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        mVideoView.setVideoPath(curUrl);
        mVideoView.start();
    }

    public void InfoUpdate(String title)
    {

        mInfoFragment.Update(title);

    }

    @Override
    public void onPlayVideo(String url,String title,String replayTitle) {

        //url = url.replace("1.9.90.92","1.8.90.63");
       // url = url.replace("1.9.90.93","1.8.90.64");

        curUrl = url;

        mHandler.removeMessages(RETRY);

        getSupportFragmentManager().beginTransaction().show(mErrorFragment).commit();

        getSupportFragmentManager().beginTransaction().hide(mReplayControllerFragment).commit();
        mHandler.removeMessages(HIDE_REPLAY_FRAGMENT);



        mVideoView.stopPlayback();
        mVideoView.stopBackgroundPlay();
        mVideoView.setVideoPath(url);

        //mVideoView.setVideoPath("rtmp://1.8.23.98/live/stream");

       // Log.d(TAG,"PLAY--- duration:"+mVideoView.getDuration() + "   curPos:" +mVideoView.getCurrentPosition()) ;

        curDurationLevel = 0;

        mVideoView.start();

        if(title != null)
        {
            isLive = true;

            mInfoFragment.Update(title);

            getSupportFragmentManager().beginTransaction().show(mInfoFragment).commit();

            mHandler.removeMessages(HIDE_INFO_FRAGMENT);
            mHandler.sendEmptyMessageDelayed(HIDE_INFO_FRAGMENT,2000);
        }

        if(replayTitle != null)
        {
            isLive = false;

            Log.d(TAG,"show replay fragment");

            mReplayControllerFragment.ChangeTitle(replayTitle);
        }

    }

    @Override
    public void onSeekTo(int second)
    {
        Log.d(TAG,"onSeekTo --- duration:"+mVideoView.getDuration() + "   curPos:" +mVideoView.getCurrentPosition()) ;


        if(!seekFlag)
        {
            curDurationLevel = mVideoView.getCurrentPosition();
        }

        //curDuration = mVideoView.getCurrentPosition();

//        if(Math.abs((mVideoView.getCurrentPosition() - curDuration)) > 5000)
//        {
//            curDurationLevel = mVideoView.getCurrentPosition();
//            curDuration = mVideoView.getCurrentPosition();
//        }


        if(mVideoView.getDuration()<=0) return;

//        if(second > 0) curDurationLevel++;
//        else curDurationLevel--;
//
//        if(curDurationLevel<0) curDurationLevel = 0;
//        if(curDurationLevel>20) curDurationLevel = 20;

        curDurationLevel = curDurationLevel + second *1000;
//        int temp = mVideoView.getDuration()/20 * curDurationLevel;


        if(curDurationLevel < 0 ) curDurationLevel = 0;
        if(curDurationLevel > mVideoView.getDuration()) curDurationLevel = mVideoView.getDuration();

        mReplayControllerFragment.Update(curDurationLevel,mVideoView.getDuration());

        getSupportFragmentManager().beginTransaction().show(mReplayControllerFragment).commit();

        mHandler.removeMessages(HIDE_REPLAY_FRAGMENT);
        mHandler.sendEmptyMessageDelayed(HIDE_REPLAY_FRAGMENT,2800);

        mHandler.removeMessages(SEEK_VIDEO);

        Message msg = new Message();
        msg.what = SEEK_VIDEO;
        Bundle b = new Bundle();
        b.putInt("time",curDurationLevel);
        //b.putString("url",url);
        msg.setData(b);

        mHandler.sendMessageDelayed(msg,800);

        //mVideoView.seekTo(temp);

        //if(!mVideoView.isPlaying()) mVideoView.start();

        seekFlag = true;


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBackPressed");
       // super.onBackPressed();

     mSidebarFragment.onBackPressed();

    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:

                    Log.d(TAG,"UPDATE");
                    update=new Update(MainActivity.this);
                    update.checkUpdate();

                    break;
                case HIDE_REPLAY_FRAGMENT:
                    getSupportFragmentManager().beginTransaction().hide(mReplayControllerFragment).commit();
                    break;
                case HIDE_INFO_FRAGMENT:
                    getSupportFragmentManager().beginTransaction().hide(mInfoFragment).commit();
                    break;
                case HIDE_INFO_NUMBER:
                    getSupportFragmentManager().beginTransaction().hide(mInfoFragment).commit();


                    if(msg.getData().getString("url").equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"no channel",Toast.LENGTH_SHORT).show();

                    }else
                    {
                        onPlayVideo(msg.getData().getString("url"),msg.getData().getString("title"),null);
                        mSidebarFragment.setCurChannelPosition(curNum -1);
                    }


                    curNum = -1;
                    break;
                case SEEK_VIDEO:
                    //int temp = msg.getData().getInt("time");
                    mVideoView.seekTo(curDurationLevel);
                    if(!mVideoView.isPlaying()) mVideoView.start();

                    curDuration = curDurationLevel;
                    seekFlag = false;
                    break;
                case RETRY:
                    mVideoView.stopPlayback();
                    mVideoView.stopBackgroundPlay();
                    mVideoView.setVideoPath(curUrl);
                    mVideoView.start();
                    break;
            }
        }


    };


    @Override
    public void onVideoStart() {
        getSupportFragmentManager().beginTransaction().hide(mErrorFragment).commit();


        if(!isLive)
        {
            mReplayControllerFragment.Update(0,mVideoView.getDuration());

            getSupportFragmentManager().beginTransaction().show(mReplayControllerFragment).commit();

            mHandler.removeMessages(HIDE_REPLAY_FRAGMENT);
            mHandler.sendEmptyMessageDelayed(HIDE_REPLAY_FRAGMENT,2000);
        }

    }

    @Override
    public void onPressNumber(int num,String title,String url) {

        mInfoFragment.Update(String.format("%03d",num));

        getSupportFragmentManager().beginTransaction().show(mInfoFragment).commit();

        mHandler.removeMessages(HIDE_INFO_NUMBER);

        Message msg = new Message();
        msg.what = HIDE_INFO_NUMBER;
        Bundle b = new Bundle();
        b.putString("title",String.format("%03d",num) +"\n" + title);
        b.putString("url",url);
        msg.setData(b);

        mHandler.sendMessageDelayed(msg,2500);




    }

    @Override
    public void onCompletion(IMediaPlayer mp) {

        if(!isLive)
        {
            mReplayControllerFragment.Update(mVideoView.getDuration(),mVideoView.getDuration());
            getSupportFragmentManager().beginTransaction().show(mReplayControllerFragment).commit();
        }


    }


    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {

        mHandler.removeMessages(HIDE_INFO_NUMBER);
        mHandler.sendEmptyMessageDelayed(RETRY,3000);

        getSupportFragmentManager().beginTransaction().show(mErrorFragment).commit();

        return true;
    }


    private void download(String downloadUrl) {
        try {
            Log.e(TAG, "download");
            URL url = new URL(downloadUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            Log.e(TAG, "contentLength = " + contentLength);
            //创建文件夹 MyDownLoad，在存储卡下
            String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory + filename);
            //不存在创建

            if (file.exists()) {
                file.delete();
            }
            //创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(file);
            //写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            //完成后关闭流
            Log.e(TAG, "download-finish");
            os.close();
            is.close();
            mHandler.sendEmptyMessage(UPDATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
