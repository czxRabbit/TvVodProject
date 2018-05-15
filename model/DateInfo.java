package com.ts.cyd.tsreplay.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cyd on 16-7-1.
 */
public class DateInfo {


    private String mChannelId;
    private int mIndex;
    private String mDate;

    public boolean isLoaded;

    public String getChannelId() {
        return mChannelId;
    }

    public void setChannelId(String mChannelId) {
        this.mChannelId = mChannelId;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }


    public List<ReplayVideo> mReplayVideos;

    public DateInfo(String id){
        mChannelId = id;

        mReplayVideos = new ArrayList<ReplayVideo>();

    }

    public void AddVideo(ReplayVideo video) {

        mReplayVideos.add(video);
    }


}
