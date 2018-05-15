package com.ts.cyd.tsreplay;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.ts.cyd.tsreplay.WebviewActivity.curVODUrl;

/**
 * Created by david on 2018/4/11.
 */

public class VODActivity extends Activity {
    private VideoPlayerIJK ijkPlayer;
    private static final String TAG = "VODActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);
        // init player
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        ijkPlayer = findViewById(R.id.video_viewvod);
        ijkPlayer.setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                mp.seekTo(0);
                mp.start();
            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {
                mp.start();
            }

            @Override
            public void onSeekComplete(IMediaPlayer mp) {

            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                //获取到视频的宽和高
            }

        });
        if(curVODUrl != null){
            loadVideo(curVODUrl);

        }
        else{
            Toast.makeText(getApplicationContext(),"没有视频",Toast.LENGTH_LONG).show();
        }

    }
    public void loadVideo(String path) {
        ijkPlayer.setVideoPath(path);
    }
    @Override
    protected void onStop() {
        super.onStop();
        ijkPlayer.release();
        ijkPlayer.stop();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        ijkPlayer.stop();
    }
    @Override
    protected void onResume ()
    {
        super.onResume();
        if(curVODUrl != null) {
            ijkPlayer.setVideoPath(curVODUrl);
            Toast.makeText(VODActivity.this,"上下键快进快退，确定键暂停，右键开始播放",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(VODActivity.this,"没有视频",Toast.LENGTH_LONG).show();
        }
        ijkPlayer.start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG,"keycode:" + keyCode);
        if (keyCode == 23 || keyCode == 66){
            Log.d(TAG, "enter pressed");
            Toast.makeText(VODActivity.this,"当前位置 "+ijkPlayer.getCurrentPosition()/60000+"分钟",Toast.LENGTH_SHORT).show();
            ijkPlayer.pause();

        }
        if (keyCode == 22){
            Log.d(TAG, "right pressed");
            Toast.makeText(VODActivity.this,"开始播放",Toast.LENGTH_SHORT).show();
            ijkPlayer.start();
        }
        if (keyCode == 19){ //up
            Log.d(TAG, "up pressed");
            long percent = 100*ijkPlayer.getCurrentPosition()/ijkPlayer.getDuration();
            long moveforward = ijkPlayer.getCurrentPosition() +60000;//1分钟
            ijkPlayer.seekTo(moveforward);
        }
        if (keyCode == 20) { //down
            Log.d(TAG, "down pressed");
            long movebackward = ijkPlayer.getCurrentPosition() -60000;//1分钟
            ijkPlayer.seekTo(movebackward);
        }
        return super.onKeyDown(keyCode,event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 19){ //up
            Log.d(TAG, "up pressed");
            long percent = 100*ijkPlayer.getCurrentPosition()/ijkPlayer.getDuration();
            if (percent > 100){
                Toast.makeText(VODActivity.this,"播放完成 ",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(VODActivity.this,"播放进度 "+ijkPlayer.getCurrentPosition()/60000+"分钟"+" 总时长 "+ijkPlayer.getDuration()/60000+"分钟 "+percent+"%",Toast.LENGTH_SHORT).show();
            }
        }
        if (keyCode == 20) { //down
            Log.d(TAG, "down pressed");
            long percent = 100*ijkPlayer.getCurrentPosition()/ijkPlayer.getDuration();
            Toast.makeText(VODActivity.this,"播放进度 "+ijkPlayer.getCurrentPosition()/60000+"分钟"+" 总时长 "+ijkPlayer.getDuration()/60000+"分钟 "+percent+"%",Toast.LENGTH_SHORT).show();
        }
        return super.onKeyUp(keyCode,event);
    }

}
