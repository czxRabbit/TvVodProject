package com.ts.cyd.tsreplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static com.ts.cyd.tsreplay.HomeActivity.TAG;
import static com.ts.cyd.tsreplay.HomeActivity.loadJson;

/**
 * Created by david on 2018/4/4.
 */

public class MovieActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.grid_bg);
//        new Thread(JsonMovieCategory).start();
    }
    Runnable JsonMovieCategory = new Runnable(){

        @Override
        public void run() {
            Log.d(TAG ,loadJsonMovie("http://1.8.6.210/vod/api/?main_category=%E7%94%B5%E5%BD%B1"));
            String vod_api_category = loadJsonMovie("http://1.8.6.210/vod/api/?main_category=%E7%94%B5%E5%BD%B1");
            String json = vod_api_category;
            try {
                org.json.JSONObject resultJson = new org.json.JSONObject(json);
                int count = resultJson.optInt("count");
                Log.d(TAG,">>count "+count);
                String next = resultJson.optString("next");
                Log.d(TAG,">>next "+next);
                int length = resultJson.length();
                Log.d(TAG,">>length "+length);
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

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"json :");
            //new VODApi().onGetChannelAlbums(new VODChannel(channelId, context), pageNo, pageSize , listener)

        }
    };
    //从网络读取Json数据
    @NonNull
    public static String loadJsonMovie (String url) {
        StringBuilder json = new StringBuilder();

        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            //执行到此处时NetworkOnMainThreadException,需要新开线程
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"UTF-8"));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
