package com.ts.cyd.tsreplay.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ts.cyd.tsreplay.R;

/**
 * Created by cyd on 16-8-26.
 */
public class ErrorFragment extends Fragment {

    private static final int SPINNER_WIDTH = 100;
    private static final int SPINNER_HEIGHT = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // View v = inflater.inflate(R.layout.fragment_error, container, false);

        ProgressBar progressBar = new ProgressBar(container.getContext());

        FrameLayout fl = new FrameLayout(container.getContext());
        fl.setBackgroundColor(getActivity().getResources().getColor(R.color.error_back));
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(SPINNER_WIDTH, SPINNER_HEIGHT, Gravity.CENTER);
        progressBar.setLayoutParams(layoutParams);

        fl.addView(progressBar);


        return fl;

    }

}
