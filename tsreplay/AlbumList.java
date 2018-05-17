package com.ts.cyd.tsreplay;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by david on 2018/4/2.
 */

public class AlbumList extends ArrayList<Album> {
    private static final String TAG = AlbumList.class.getSimpleName();

    public void debug () {
        for (Album a : this) {
            Log.d(TAG, ">> albumlist " + a.toString());
        }
    }
}
