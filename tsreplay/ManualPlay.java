package com.ts.cyd.tsreplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.ts.cyd.tsreplay.WebviewActivity.curVODUrl;

/**
 * Created by david on 2018/4/11.
 */

public class ManualPlay extends Activity {
    private VideoPlayerIJK ijkPlayer;
    private static final String TAG = "ManualPlay";
    private String editContent;
    private EditText etOne;
    public static String mplayUrl = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manualplay);
        etOne = (EditText) findViewById(R.id.et_phone);
        editContent = etOne.getText().toString();
        etOne.setFocusable(true);
        etOne.setFocusableInTouchMode(true);
        etOne.requestFocus();
        TextView txtView = (TextView) findViewById(R.id.tv_login);
        txtView.setFocusable(true);
        txtView.setFocusableInTouchMode(true);
        etOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                editContent = etOne.getText().toString();
                Log.i("Ansen","内容改变之后调用:"+editContent);
//                Toast.makeText(getApplicationContext(), "edit text content" + editContent, Toast.LENGTH_SHORT).show();
            }
        });
        txtView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mplayUrl = "http://live.com/live/"+editContent+".m3u8";
                Toast.makeText(getApplicationContext(),
                        "The video url : " + mplayUrl,
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ManualPlay.this, MPlayActivity.class);
                startActivity(intent);
            }

        });
    }
}
