package com.ts.cyd.tsreplay.provider;

import android.content.Context;
import android.util.Log;

import com.ts.cyd.tsreplay.model.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.ts.cyd.tsreplay.R;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.ts.cyd.tsreplay.HomeActivity.playback;

/**
 * Created by cyd on 16-7-1.
 */
public class InfoProvider {

    private static final String TAG = "InfoProvider";
//    public static InfoProvider INFO;
//
//
//    public static InfoProvider newInstance()
//    {
//        INFO = new InfoProvider();
//
//        return INFO;
//    }

    private String mIp;

    public InfoProvider(){
        mChannels = new ArrayList<Channel>();

    }
    public List<Channel> mChannels;
    public boolean isLoaded;


    public boolean[] ReplayTest = new boolean [ 200];


    public String getIP(Context ctx)
    {
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.ip);
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(inputStreamReader);
        String ip = null;
        try {
            ip = reader.readLine();
            ip = playback;

        } catch (IOException e) {
            e.printStackTrace();
        }

        mIp = ip;
        return ip;

    }

    public boolean FetchChannels(Context ctx){

        String urlString = getIP(ctx);
        urlString = "http://"+ urlString +":8080/replay/serv?feed=category";
        BufferedReader reader = null;

        Log.v(TAG,"connect:" + urlString);

        try {
            java.net.URL url = new java.net.URL(urlString);
            URLConnection urlConnection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            //urlConnection.getInputStream(), "iso-8859-1"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();

            JSONObject json = new JSONObject(jsonString);
            Log.v(TAG,"json string:" + jsonString);

            JSONArray array = json.getJSONArray("category");

            for(int i = 0 ;i < array.length() ;i++)
            {

                String channelId = array.getJSONObject(i).getString("channel_id");
                String channelName = array.getJSONObject(i).getString("channel_name");
                String rtmpUrl = array.getJSONObject(i).getString("rtmp_url");

                Channel channel = new Channel(channelId, channelName,rtmpUrl);

                mChannels.add(channel);

                Log.v(TAG,"channelID:"+ channel.getId() + "  name:"+ channel.getName() + " url:"+ channel.getUrl());
            }


            //return new JSONObject(json);
        } catch (Exception e) {
            Log.d(TAG, "Failed to parse the json for media list", e);
            return false;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(TAG, "JSON feed closed", e);
                }
            }
        }

        return true;
    }


    public void FetchVideos(){

        for(int i = 0;i<mChannels.size();i++){

            Channel channel = mChannels.get(i);
            for(int j=0;j<7;j++)
            {

                channel.getVideos(mIp,j);
                ReplayTest[i]=(ReplayTest[i]||channel.testVideos()) ;


                //channel.getVideosTest(mIp,j);
            }
            channel.mIsLoaded = true;

        }

    }


}
