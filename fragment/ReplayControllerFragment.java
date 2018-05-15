package com.ts.cyd.tsreplay.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ts.cyd.tsreplay.R;

/**
 * Created by cyd on 16-8-23.
 */
public class ReplayControllerFragment extends Fragment {



    private ProgressBar mProgressBar ;
    private TextView mCurTime;
    private TextView mTotalTIme;
    private TextView mTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_replaycontroller, container, false);

        mProgressBar = (ProgressBar)v.findViewById(R.id.replay_progressbar);

        mCurTime = (TextView)v.findViewById(R.id.replay_cur_time);
        mTotalTIme =(TextView)v.findViewById(R.id.replay_total_time);
        mTitle = (TextView)v.findViewById(R.id.replay_title);

        return v;

    }


    public void Update(int curTime,int totalTime)
    {
        int temp =  (int)((float)curTime/(float)totalTime *100.0);

        mProgressBar.setProgress(temp);

        mCurTime.setText(Convert(curTime));
        mTotalTIme.setText(Convert(totalTime));
    }

    public void ChangeTitle(String title)
    {
        mTitle.setText(title);
    }

    private String Convert(int time)
    {
        time /= 1000;

        int secend = time %60;
        time /= 60;

        int minute = time %60;
        time /= 60;

        int hour = time%60;

        return String.format("%02d",hour)+":"+String.format("%02d",minute)+":"+String.format("%02d",secend);
    }


}
