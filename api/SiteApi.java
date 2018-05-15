package com.ts.cyd.tsreplay.api;

import android.content.Context;
import android.util.Log;

import com.ts.cyd.tsreplay.Album;
import com.ts.cyd.tsreplay.OnGetChannelAlbumListener;
import com.ts.cyd.tsreplay.Site;
import com.ts.cyd.tsreplay.VODApi;
import com.ts.cyd.tsreplay.VODChannel;


/**
 * Created by david on 2018/4/3.
 */


public class SiteApi {

    public static void onGetChannelAlbums(Context context, int pageNo, int pageSize, int siteId, int channelId, OnGetChannelAlbumListener listener) {
        switch (siteId) {
            case Site.LETV:
                //new VODApi().onGetChannelAlbums(new VODChannel(channelId, context), pageNo, pageSize , listener);
                break;
        }
    }

    public static void onGetAlbumDetail(Album album, OnGetAlbumDetailListener listener) {
        int siteId = album.getSite().getSiteId();
        switch (siteId) {
            case Site.LETV:
                //new VODApi().onGetAlbumDetail(album, listener);
                break;
        }
    }

    /**
     * 取video相关信息
     * @param album
     * @param listener
     */
//    public static void onGetVideo(int pageSize, int pageNo, Album album, OnGetVideoListener listener) {
//        int siteId = album.getSite().getSiteId();
//        switch (siteId) {
//            case Site.LETV:
//                new VODApi().onGetVideo(album, pageSize, pageNo, listener);
//                break;
//
//        }
//    }
//
//    public static void onGetVideoPlayUrl(Video video, OnGetVideoPlayUrlListener listener) {
//        int siteId = video.getSite();
//        switch (siteId) {
//            case Site.LETV:
//                new VODApi().onGetVideoPlayUrl(video,  listener);
//                break;
//        }
//    }

}
