package com.ts.cyd.tsreplay.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by cyd on 16-6-28.
 */
public class Channel {
    private static final String TAG = "Channel";

    private String mChannelId;

    private String mName;
    private String mUrl;
    boolean Replaytest= false ;

    private String mIp;

    public List<DateInfo> mDateInfos;

    public void Update()
    {

        for(int j=0;j<7;j++)
        {
            getVideos(j);
            //channel.getVideosTest(mIp,j);
        }

    }

    public Channel(String id, String name, String url){
        mChannelId = id;
        mName = name;
        mUrl = url;

        mIsLoaded = false;
        mDateInfos = new ArrayList<DateInfo>();

        for(int i= 0;i<7;i++){
            mDateInfos.add(new DateInfo(id));
        }

    }

    private List<DateInfo> mDates;

    public boolean mIsLoaded;


    public String getId() {return  mChannelId;}
    public String getUrl() {
        return mUrl;
    }
    public String getName() {
        return mName;
    }


    public void getVideos(int day)
    {
        getVideos(mIp,day);
    }






    public boolean testVideos()
    {
       return  Replaytest;
    }




    public void getVideos(String urlString,int day)
    {

        mIp = urlString;

        urlString = "http://"+ urlString +":8080/replay/serv?feed=channel&list="+mChannelId+"&d="+day;

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
            //Log.v(TAG,"json string:" + jsonString);

            //JSONArray array = json.getJSONArray("category");
            String dateString = json.getString("date");

            DateInfo dateInfo = mDateInfos.get(day);
            dateInfo.mReplayVideos.clear();

            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd");

            Date date;
            try {
                date = formatter.parse(dateString);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);


                //Log.d(TAG,"monthe:"+ cal.get(Calendar.MONTH));
                dateString = (cal.get(Calendar.MONTH)+1)+" 月" + " "+ date.getDate() + " 日";
                //holder.mTitleTextView.setText(date.getMonth()+" 月" + " "+ date.getDate() + " 日");

            }catch (Exception e)
            {
                //holder.mTitleTextView.setText("");
                dateString = "";
            }

            dateInfo.setDate(dateString);

            JSONArray list = json.getJSONArray("list");

            for(int i =0;i< list.length();i++)
            {
                JSONObject object = list.getJSONObject(i);

                String title = object.getString("title");
                String video_url = object.getString("url");

                String start_time = object.getString("start_time");
                String end_time = object.getString("end_time");

                start_time = start_time.substring(11,16);
                end_time = end_time.substring(11,16);

                String finished = object.getString("finished");

                ReplayVideo video = new ReplayVideo();

                video.setUrl(video_url);
                video.setTitle(title);
                video.setStart_time(start_time);
                video.setEnd_time(end_time);
                video.setFinished(finished);

                video.setDate(dateInfo.getDate());
                video.setChannelId(mChannelId);

                if("1".equals(finished))
                    dateInfo.AddVideo(video);



            }

            if(dateInfo.mReplayVideos.size()==0)
            {
                Replaytest = false ;
            }
            else {
                Replaytest = true;
            }

            Collections.reverse(dateInfo.mReplayVideos);

            dateInfo.isLoaded = true;



            //return new JSONObject(json);
        } catch (Exception e) {
            Log.d(TAG, "Failed to parse the json for media list", e);
            //return false;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(TAG, "JSON feed closed", e);
                }
            }
        }
    }

    public void getVideosTest(String urlString,int day)
    {
        String dateString = "2016-07-24";

        DateInfo dateInfo = mDateInfos.get(day);

        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date date;
        try {
            date = formatter.parse(dateString);

            dateString = date.getMonth()+" 月" + " "+ date.getDate() + " 日";
            //holder.mTitleTextView.setText(date.getMonth()+" 月" + " "+ date.getDate() + " 日");

        }catch (Exception e)
        {
            //holder.mTitleTextView.setText("");
            dateString = "";
        }

        dateInfo.setDate(dateString);

        for(int i=0;i<10;i++)
        {
            String title = mChannelId +" " +"day"+day+" "+ i;
            String video_url = mChannelId +" " +"channel"+ i;


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

            String start_time = "2016-07-26 14:00:00.0";
            String end_time = "2016-07-26 14:00:00.0";

            start_time = start_time.substring(11,16);
            end_time = end_time.substring(11,16);
//            try {
//                date = formatter.parse(start_time);
//                start_time = date.getHours()+":" + date.getMinutes();
//                //holder.mTitleTextView.setText(date.getMonth()+" 月" + " "+ date.getDate() + " 日");
//
//            }catch (Exception e)
//            {
//                //holder.mTitleTextView.setText("");
//                start_time = "";
//            }
//
//            try {
//                date = formatter.parse(end_time);
//                end_time = date.getHours()+":" + date.getMinutes();
//                //holder.mTitleTextView.setText(date.getMonth()+" 月" + " "+ date.getDate() + " 日");
//
//            }catch (Exception e)
//            {
//                //holder.mTitleTextView.setText("");
//                end_time = "";
//            }

            Log.v(TAG, start_time + "   " + end_time);

            String finished = "1";

            ReplayVideo video = new ReplayVideo();

            video.setUrl(video_url);
            video.setTitle(title);
            video.setStart_time(start_time);
            video.setEnd_time(end_time);
            video.setFinished(finished);

            dateInfo.AddVideo(video);

        }

        dateInfo.isLoaded = true;
    }

}
