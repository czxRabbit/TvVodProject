package com.ts.cyd.tsreplay.data;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ts.cyd.tsreplay.MainActivity;
import com.ts.cyd.tsreplay.R;


/**
 * fetch api
 */
public class FetchVodApi extends Activity {
    public static final String TAG = "FetchVodApi";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


    }



}
