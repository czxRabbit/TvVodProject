package com.ts.cyd.tsreplay.model;

/**
 * Created by cyd on 16-7-1.
 */
public class ReplayVideo {



    private String mTitle;
    private String mUrl;

    private String start_time;
    private String end_time;

    private String finished;

    public ReplayVideo(){

    }

    private String mChannelId;
    private String mDate;

    public void setChannelId(String id){mChannelId = id;}
    public String getChannelId(){ return  mChannelId;}

    public void setDate(String date){ mDate = date;}
    public String getDate(){return mDate;}

    public void setTitle(String title) { mTitle = title;}
    public void setUrl(String url){mUrl=url;}
    public void setStart_time(String start_time){ this.start_time = start_time;}
    public void setEnd_time(String end_time) { this.end_time = end_time;}
    public void setFinished(String finished){this.finished = finished;}

    public String getTitle() {
        return mTitle;
    }
    public String getUrl() {
        return mUrl;
    }
    public String getStart_time() {
        return start_time;
    }
    public String getEnd_time() {
        return end_time;
    }
    public String getFinished() {
        return finished;
    }

}
