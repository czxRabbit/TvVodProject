package com.ts.cyd.tsreplay;


/**
 * Created by david on 2018/4/2.
 */

public abstract class BaseSiteApi {
    public abstract void onGetChannelAlbums(VODChannel channel, int pageNo, int pageSize, OnGetChannelAlbumListener listener);
}
