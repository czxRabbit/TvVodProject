package com.ts.cyd.tsreplay.api;

import com.ts.cyd.tsreplay.Album;
import com.ts.cyd.tsreplay.ErrorInfo;

/**
 * Created by david on 2018/4/3.
 */

public interface OnGetAlbumDetailListener {
    void onGetAlbumDetailSuccess(Album album);
    void onGetAlbumDetailFailed(ErrorInfo info);
}
