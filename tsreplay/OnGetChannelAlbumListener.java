package com.ts.cyd.tsreplay;

/**
 * Created by david on 2018/4/2.
 */

public interface OnGetChannelAlbumListener {
    void onGetChannelAlbumSuccess(AlbumList albumList);
    void onGetChannelAlbumFailed(ErrorInfo info);
}
