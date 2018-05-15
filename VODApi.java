package com.ts.cyd.tsreplay;

import android.text.TextUtils;
import android.util.Log;


import com.ts.cyd.tsreplay.api.OnGetAlbumDetailListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
/**
 * Created by david on 2018/4/2.
 */

public class VODApi extends BaseSiteApi {

    private static final String TAG = VODApi.class.getSimpleName();

    private static final String LETV_CHANNELID_MOVIE = "%E7%94%B5%E5%BD%B1"; //电影频道ID
    private static final String LETV_CHANNELID_SERIES = "%E7%94%B5%E8%A7%86%E5%89%A7"; //电视剧频道ID
    private static final String LETV_CHANNELID_VARIETY = "%E7%94%B5%E5%BD%B1"; //综艺频道ID
    private static final String LETV_CHANNELID_DOCUMENTRY = "%E7%94%B5%E5%BD%B1"; //纪录片频道ID
    private static final String LETV_CHANNELID_COMIC = "%E7%94%B5%E8%A7%86%E5%89%A7"; //动漫频道ID
    private static final String LETV_CHANNELID_MUSIC = "%E7%94%B5%E5%BD%B1"; //音乐频道ID
    private static final int BITSTREAM_SUPER = 100;
    private static final int BITSTREAM_NORMAL = 101;
    private static final int BITSTREAM_HIGH = 102;
    //http://1.8.6.210/vod/api/?main_category=%E7%94%B5%E5%BD%B1&page=2
    private final static String ALBUM_LIST_URL_FORMAT = "http://1.8.6.210/vod/api/" + "?main_category=%s&" + "page=%s";



    @Override
    public void onGetChannelAlbums(VODChannel channel, int pageNo, int pageSize, OnGetChannelAlbumListener listener) {
        String url =  getChannelAlbumUrl(channel,pageNo, pageSize);
        Log.d(TAG,"<< url="+url);
        doGetChannelAlbumsByUrl(url, listener);
    }

    private String getChannelAlbumUrl(VODChannel channel, int pageNo, int pageSize) {
        return String.format(ALBUM_LIST_URL_FORMAT, conVertChannleId(channel), pageNo);
    }
    private String conVertChannleId(VODChannel channel) {
        String channelId = null;// null 无效值
        switch (channel.getChannelId()) {
            case VODChannel.SHOW:
                channelId = LETV_CHANNELID_SERIES;
                break;
            case VODChannel.MOVIE:
                channelId = LETV_CHANNELID_MOVIE;
                break;
            case VODChannel.COMIC:
                channelId = LETV_CHANNELID_COMIC;
                break;
            case VODChannel.MUSIC:
                channelId = LETV_CHANNELID_MUSIC;
                break;
            case VODChannel.DOCUMENTRY:
                channelId = LETV_CHANNELID_DOCUMENTRY;
                break;
            case VODChannel.VARIETY:
                channelId = LETV_CHANNELID_VARIETY;
                break;
        }
        return channelId;
    }
    private void doGetChannelAlbumsByUrl(final String url, final OnGetChannelAlbumListener listener) {
        OkHttpUtils.excute(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    ErrorInfo info  = buildErrorInfo(url, "doGetChannelAlbumsByUrl", e, ErrorInfo.ERROR_TYPE_URL);
                    listener.onGetChannelAlbumFailed(info);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    ErrorInfo info  = buildErrorInfo(url, "doGetChannelAlbumsByUrl", null, ErrorInfo.ERROR_TYPE_HTTP);
                    listener.onGetChannelAlbumFailed(info);
                    return;
                }
                //String json = response.body().string();
                String json = response.body().string();
                try {
                    JSONObject resultJson = new JSONObject(json);
                    int count = resultJson.optInt("count");
                    Log.d(TAG,">>count"+count);
                    if (count > 0) {
                        AlbumList list = new AlbumList();
                        JSONArray albumListJosn = resultJson.optJSONArray("results");
                        for (int i = 0; i< albumListJosn.length(); i++) {
                            Album album = new Album();
                            JSONObject albumJson = albumListJosn.getJSONObject(i);
                            album.setAlbumId(albumJson.getString("id"));
                            album.setAlbumDesc(albumJson.getString("category"));
                            album.setTitle(albumJson.getString("title"));
                            album.setTip(albumJson.getString("slug"));
                            String image = "http://1.8.6.210" + albumJson.getString("image");
                            album.setHorImgUrl(image);
                            list.add(album);
                        }
                        if (list != null) {
                            if (list.size() > 0 && listener != null) {
                                listener.onGetChannelAlbumSuccess(list);
                            }
                        } else {
                            ErrorInfo info  = buildErrorInfo(url, "doGetChannelAlbumsByUrl", null, ErrorInfo.ERROR_TYPE_DATA_CONVERT);
                            listener.onGetChannelAlbumFailed(info);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ErrorInfo info  = buildErrorInfo(url, "doGetChannelAlbumsByUrl", null, ErrorInfo.ERROR_TYPE_PARSE_JSON);
                    listener.onGetChannelAlbumFailed(info);
                }
            }
        });
    }
    private ErrorInfo buildErrorInfo(String url, String functionName, Exception e, int type) {
        ErrorInfo info  = new ErrorInfo(Site.LETV, type);
        info.setExceptionString(e.getMessage());
        info.setFunctionName(functionName);
        info.setUrl(url);
        info.setTag(TAG);
        info.setClassName(TAG);
        return info;
    }


}
