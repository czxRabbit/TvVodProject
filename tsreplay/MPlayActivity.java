package com.ts.cyd.tsreplay;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.ts.cyd.tsreplay.ManualPlay.mplayUrl;
import static com.ts.cyd.tsreplay.WebviewActivity.curVODUrl;

/**
 * Created by david on 2018/4/11.
 */

public class MPlayActivity extends Activity {
    private VideoPlayerIJK ijkPlayer;
    private static final String TAG = "MPlayActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mplay);
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
        if(mplayUrl != null){
            loadVideo(mplayUrl);

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
        if(mplayUrl != null) {
            ijkPlayer.setVideoPath(mplayUrl);
            Toast.makeText(MPlayActivity.this,"确定键暂停，右键开始播放",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(MPlayActivity.this,"没有视频",Toast.LENGTH_LONG).show();
        }
        ijkPlayer.start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG,"keycode:" + keyCode);
        if (keyCode == 23 || keyCode == 66){
            Log.d(TAG, "enter pressed");
            ijkPlayer.pause();

        }
        if (keyCode == 22){
            Log.d(TAG, "right pressed");
            Toast.makeText(MPlayActivity.this,"开始播放",Toast.LENGTH_SHORT).show();
            ijkPlayer.start();
        }
        return super.onKeyDown(keyCode,event);
    }
}
