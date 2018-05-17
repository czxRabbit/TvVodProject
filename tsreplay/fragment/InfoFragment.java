package com.ts.cyd.tsreplay.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ts.cyd.tsreplay.R;


/**
 * Created by cyd on 16-8-23.
 */
public class InfoFragment extends Fragment {



    private TextView mChannelTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_info, container, false);

        mChannelTextView = (TextView)v.findViewById(R.id.info_channel);

        return v;

    }

    public void Update(String channel)
    {
        mChannelTextView.setText(channel);
    }

}
