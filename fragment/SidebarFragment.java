package com.ts.cyd.tsreplay.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ts.cyd.tsreplay.MainActivity;
import com.ts.cyd.tsreplay.R;
import com.ts.cyd.tsreplay.framework.DividerItemDecoration;
import com.ts.cyd.tsreplay.model.Channel;
import com.ts.cyd.tsreplay.model.DateInfo;
import com.ts.cyd.tsreplay.model.ReplayVideo;
import com.ts.cyd.tsreplay.provider.InfoProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import android.content.Intent;

/**
 * Created by cyd on 16-6-28.
 */
public class SidebarFragment extends Fragment {
    private final String TAG = "SidebarFragment";

    private final int SEEK_TIME = 60;

    private RecyclerView mChannelRecyclerView;
    private RecyclerView mDateRecyclerView;
    private RecyclerView mVideoRecyclerView;



    private ChannelAdapter mChannelAdapter;
    private DateAdapter mDateAdapter;
    private VideoAdapter mVideoAdapter;

    private int mChannelIndex = 0;
    private int mChannelTag=0;
    private boolean keydown;


    private boolean isChannelFocus = true;
    private boolean isDateFocus = false;
    private boolean isVideoFocus = false;

    private boolean isLive = true;
    private boolean bootomTag = false ;
    private boolean isVisibleCache = false;
    public boolean leftToRight=false;

    private boolean upTag=false;



    private LinearLayoutManager lm;
    private LinearLayoutManager lm2;
    private LinearLayoutManager lm3;

    private FrameLayout fl1;
    private RelativeLayout fl2;
    private FrameLayout fl3;


    private InfoProvider mProvider;

    private boolean isDownloaded = false;
    private boolean isLoaded = false;

    private FrameLayout mRoot;


    public  boolean isVisBottom (RecyclerView mChannelRecyclerView){
        int totalItemCount = mChannelAdapter.getItemCount();
        int state = mChannelRecyclerView.getScrollState();
        if( mChannelIndex == totalItemCount - 1 && state == mChannelRecyclerView.SCROLL_STATE_IDLE) {

            return true;
        }
        else
        {
            return false;
        }

    }
    public boolean isVisUp (RecyclerView  mChannelRecyclerView){

        int state = mChannelRecyclerView.getScrollState();
        if( mChannelIndex == 0 && state == mChannelRecyclerView.SCROLL_STATE_IDLE) {

            return true;
        }
        else
        {
            return false;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchInfoTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_sideber,container,false);

        mChannelRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view1);
        lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        mChannelAdapter = new ChannelAdapter();
        mChannelRecyclerView.setAdapter(mChannelAdapter);

        mChannelRecyclerView.setLayoutManager(lm);
        //mChannelRecyclerView.setFocusable(false);

        mChannelRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),lm.getOrientation()));


        mChannelRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Log.i(TAG,"hasfocus:"+hasFocus);
                if(hasFocus){
                    Log.d(TAG,"channel has focus");

                    if(isVideoFocus) return;

                    if(isChannelFocus) {
                        if (!leftToRight)
                        {
                            Log.d(TAG, "channel count:" + mChannelRecyclerView.getChildCount());
                            if (mChannelRecyclerView.getChildCount() > 0) {
                                final int fPosition = lm.findFirstVisibleItemPosition();

                                int totalitem = lm.getItemCount();
                                bootomTag = isVisBottom(mChannelRecyclerView);
                                upTag = isVisUp(mChannelRecyclerView);

                                Log.d(TAG, "mChannelTag" + mChannelTag + "mChannelAdapter.mLastIndex" + mChannelAdapter.mLastIndex + "bootomTag" + bootomTag);
                                if (mChannelTag > mChannelAdapter.mLastIndex) {
                                    if (bootomTag) {    //位于底部时回到顶端

                                        mChannelRecyclerView.scrollToPosition(0);
                                        mChannelRecyclerView.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mChannelRecyclerView.getChildAt(0).requestFocus();
                                                mChannelTag = 0;
                                                mChannelAdapter.mLastIndex = 0;
                                                mChannelIndex = 0;
                                            }
                                        }, 100);

                                    }


                                } else if (mChannelTag < mChannelAdapter.mLastIndex) // up
                                {


                                    Log.d(TAG, "mChannelTag2:" + mChannelTag + "mChannelAdapter.mLastIndex" + mChannelAdapter.mLastIndex);
                                    mChannelRecyclerView.scrollToPosition((totalitem - 1));
                                    mChannelRecyclerView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            int SumChannels = mProvider.mChannels.size();//小于9个频道

                                            if (SumChannels > 8) {
                                                mChannelRecyclerView.getChildAt(8).requestFocus();
                                            } else {
                                                mChannelRecyclerView.getChildAt(SumChannels - 1);
                                            }

                                            mChannelTag = mChannelAdapter.getItemCount() - 1;
                                            mChannelAdapter.mLastIndex = mChannelAdapter.getItemCount() - 1;
                                            mChannelIndex = mChannelAdapter.getItemCount() - 1;
                                            upTag = false;
                                        }
                                    }, 100);

                                } else
                                    {
                                    Log.d(TAG, "last index3:" + mChannelAdapter.mLastIndex + "  firstVisibleItemPosition:" + fPosition);

                                    {

                                        if (mChannelAdapter.mLastIndex - fPosition >= 9) {
                                            mChannelRecyclerView.scrollBy(0, (int) getResources().getDimension(R.dimen.channel_item_height) * (mChannelAdapter.mLastIndex - 8));
                                        }

                                        if (mChannelAdapter.mLastIndex - fPosition < 0) {
                                            mChannelRecyclerView.scrollBy(0, (int) getResources().getDimension(R.dimen.channel_item_height) * (mChannelAdapter.mLastIndex - fPosition));

                                        }


                                        Log.d(TAG, "last index:" + mChannelAdapter.mLastIndex + "firstVisibleItemPosition:" + fPosition);


                                        mChannelRecyclerView.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                int fPosition = lm.findFirstVisibleItemPosition();

                                                Log.d(TAG, "mChannelAdapter.mLastIndex13:" + mChannelAdapter.mLastIndex + "  firstVisibleItemPosition:" + fPosition);

                                                mChannelRecyclerView.getChildAt(mChannelAdapter.mLastIndex - fPosition).requestFocus();

                                            }


                                        }, 100);


                                    }
                                }
                            }
                        }
                        else{
                            if(!isVisibleCache) {

                                Log.d(TAG, "scrollto3: " + mChannelAdapter.mLastClickIndex);
                                mChannelRecyclerView.scrollToPosition(mChannelAdapter.mLastClickIndex);
                                mChannelRecyclerView.getChildAt(0).requestFocus();

                            }
                        }
                    }

                }else{
                    Log.d(TAG,"channel lose focus");
                }
            }
        });




        mDateRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view2);
        lm2 = new LinearLayoutManager(getActivity());
        lm2.setOrientation(LinearLayoutManager.VERTICAL);

        mDateAdapter = new DateAdapter();
        mDateRecyclerView.setAdapter(mDateAdapter);

        mDateRecyclerView.setLayoutManager(lm2);

        mDateRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Log.d(TAG,"date has focus");

                    mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex).requestFocus();

                }else{
                    Log.d(TAG,"date lose focus");
//
                }
            }
        });

        mVideoRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view3);
        lm3 = new LinearLayoutManager(getActivity());
        lm3.setOrientation(LinearLayoutManager.VERTICAL);


        mVideoAdapter = new VideoAdapter();
        mVideoRecyclerView.setAdapter(mVideoAdapter);

        mVideoRecyclerView.setLayoutManager(lm3);


        mVideoRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("abc","hasfocus:"+hasFocus);
                if(hasFocus){
                    Log.d(TAG,"video has focus");


                    if(mVideoRecyclerView.getChildCount()>0){
                        int fPosition =  lm3.findFirstVisibleItemPosition();

                        Log.d(TAG,"last index:"+ mVideoAdapter.mLastIndex +"firstVisibleItemPosition:"+ fPosition) ;

                        if(isDateFocus) mVideoAdapter.mLastIndex = 0;

                        if(fPosition !=1){
                            if(mVideoAdapter.mLastIndex < mVideoAdapter.getItemCount() && mVideoAdapter.mLastIndex - fPosition >=0)
                            mVideoRecyclerView.getChildAt(mVideoAdapter.mLastIndex - fPosition).requestFocus();
                            else {
                                mVideoRecyclerView.getChildAt(0).findViewById(R.id.video_title).requestFocus();

                            }
                        }

                    }
                }else{
                    Log.d(TAG,"video lose focus");

                }
            }
        });

        fl1 = (FrameLayout)v.findViewById(R.id.sidebar1);
        fl2 = (RelativeLayout)v.findViewById(R.id.sidebar2);
        //fl3 = (FrameLayout)v.findViewById(R.id.sidebar3);

        mRoot = (FrameLayout)(getActivity().findViewById(R.id.fragment_siderbar));




        return v;

    }

    public void setProvider(InfoProvider provider)
    {
        mProvider = provider;
    }


    private class ChannelHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;

        public ChannelHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView)itemView;


        }
    }

    private class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {
        private List<Channel> mChannels;

        public int mLastIndex = 0;
        public int mLastClickIndex = 0;

        public boolean mFocusCache = false;

        public ChannelAdapter(){
            mChannels = new ArrayList<Channel>();

        }

        public void ChangeList(List<Channel> channels)
        {
            mChannels = channels;
        }

        @Override
        public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_channel,parent,false);

            Log.d(TAG,"channel createview");

            return new ChannelHolder(view);
        }

        @Override
        public void onBindViewHolder(final ChannelHolder holder, final int position)
        {
            Channel channel = mChannels.get(position);
            holder.mTitleTextView.setText( (position+1) + "  " + channel.getName());

            if(0 == position ){
                holder.mTitleTextView.setNextFocusUpId(R.id.recycler_view1);
            }else if(position == getItemCount()-1){
                holder.mTitleTextView.setNextFocusDownId(R.id.recycler_view1);
            }else{

                holder.mTitleTextView.setNextFocusDownId(0);
                holder.mTitleTextView.setNextFocusUpId(0);
            }

//            if(position == getItemCount()-1){
//                holder.mTitleTextView.setNextFocusDownId(R.id.recycler_view1);
//            }



            holder.mTitleTextView.setFocusable(true);
            holder.mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Channel channel = mChannels.get(position);


                    Log.d(TAG,"CLICK!"+"position:"+ position + "url:"+ channel.getUrl() + " name:"+ channel.getName());

                    if(fl2.getVisibility() == View.VISIBLE)
                    {
                        fl2.setVisibility(View.INVISIBLE);
                    }

                    mRoot.setVisibility(View.GONE);
                    isVisibleCache = false;
                    Log.d(TAG,"play"+  mLastClickIndex + "islive" + isLive);

                    if(position != mLastClickIndex || !isLive)
                    {
                        Log.d(TAG,"play"+  mLastClickIndex + "islive" + isLive);
                        String name = channel.getName();
                        //if(channel.getName().length()>8) name = channel.getName().substring(0,1);

                        mPlayVideoCallBack.onPlayVideo(channel.getUrl(), String.format("%03d",position+1) + "\n" + name,null);

                    }

                    mLastClickIndex = position;
                    mChannelIndex=position;
                    Log.d(TAG,"mLastClickIndex and mChannelIndex = position"+  mLastClickIndex);

                    isLive = true;
                }
            });
            holder.mTitleTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    final Channel channel = mChannels.get(position);
                    if (hasFocus){
                        Log.d(TAG,"FUCOS1!"+"position:"+ position + "url:"+ channel.getUrl());
                        if(position  <=  mChannelAdapter.getItemCount())

                        {


                        }
                        mChannelIndex = position ;
                        Log.d(TAG,"mChannelIndex"+mChannelIndex);
                        ((TextView)v).setBackground(getResources().getDrawable(R.drawable.item_channel));
                        ((TextView)v).setTextAppearance(getActivity(),R.style.ChannelStyle_Selected);
                        if(!isChannelFocus){

                            mFocusCache = true;

                            if(position != mLastIndex)
                            {
                                isChannelFocus = true;
                                isDateFocus = false;
                                isVideoFocus = false;

                                //fl2.setVisibility(View.INVISIBLE);
                                //MoveRight();

                                int fPosition =  lm.findFirstVisibleItemPosition();

                                Log.d(TAG,"last index:"+ mLastIndex +"firstVisibleItemPosition:"+ fPosition) ;
                                mChannelRecyclerView.getChildAt(mLastIndex - fPosition).requestFocus();
                                return;
                            }


                        }

                        if(isVisibleCache) Log.d(TAG,"isVisibleCache is true "+ mLastClickIndex);
                        else Log.d(TAG,"isVisibleCache is false " + mLastClickIndex);

                        if(!isVisibleCache)
                        {
                            isVisibleCache = true;

                            if(position != mLastClickIndex)
                            {
                                int fPosition =  lm.findFirstVisibleItemPosition();

                                if(mLastClickIndex - fPosition>=9)
                                {
                                    mChannelRecyclerView.scrollBy(0,(int)getResources().getDimension(R.dimen.channel_item_height)*(mLastClickIndex-8));
                                }
                                if(mLastClickIndex - fPosition <0)
                                {
                                    mChannelRecyclerView.scrollBy(0,(int)getResources().getDimension(R.dimen.channel_item_height)*(mLastClickIndex - fPosition));

                                }


                                Log.d(TAG,"position != mLastClickIndex last click index:"+ mLastClickIndex +"firstVisibleItemPosition:"+ fPosition+"Position:"+ position) ;
                                // mChannelRecyclerView.getChildAt(mLastClickIndex - fPosition).requestFocus();

                                mChannelRecyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int fPosition =  lm.findFirstVisibleItemPosition();
                                        mChannelRecyclerView.getChildAt(mLastClickIndex - fPosition).requestFocus();
                                    }
                                },100);


                                return;
                            }


                        }

                        Log.d(TAG,"CHANNEL ADD BLUE");
                        //((DateHolder) mDateRecyclerView.findViewHolderForAdapterPosition(mDateAdapter.mLastIndex)).mTitleTextView.setBackgroundColor(getResources().getColor(R.color.backDateItem));
                       // ((TextView) mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex)).setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));
                        ((TextView) mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex)).setBackground(getResources().getDrawable(R.drawable.item_date));


                        if(mFocusCache)
                        {

                            //((DateHolder) mDateRecyclerView.findViewHolderForAdapterPosition(mDateAdapter.mLastIndex)).mTitleTextView.setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));


                            //((TextView) mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex)).setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));
                            ((TextView) mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex)).setBackground(getResources().getDrawable(R.drawable.item_date));
                           Log.d(TAG,"date + " + ((TextView) mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex)).getText().toString());

                           //((DateHolder) mDateRecyclerView.findViewHolderForAdapterPosition(mDateAdapter.mLastIndex)).mTitleTextView.setText(R.string.exit);


                            mFocusCache = false;
                        }
                        else
                        {
//                            if(isDateFocus)
                            mDateAdapter.mLastIndex = 0;

                           // ((DateHolder) mDateRecyclerView.findViewHolderForAdapterPosition(0)).mTitleTextView.setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));
                          //  ((TextView) mDateRecyclerView.getChildAt(0)).setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));


                            Log.d(TAG,"channel + "+ ((TextView) mDateRecyclerView.getChildAt(0)).getText().toString());



                            // ((DateHolder) mDateRecyclerView.findViewHolderForAdapterPosition(mDateAdapter.mLastIndex)).mTitleTextView.setText(R.string.close);


                        }

                        Log.d(TAG,"UPDATE LIST");

                        Thread thread=new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                channel.Update();

                                mDateRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        mDateAdapter.ChangeList(channel.mDateInfos);
                                        mVideoAdapter.ChangeList(channel.mDateInfos.get(mDateAdapter.mLastIndex).mReplayVideos);

                                    }
                                });


                            }
                        });
                        thread.start();



//                        mVideoRecyclerView.post(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//                            }
//                        });


                        int fPosition =  lm.findFirstVisibleItemPosition();
                        if( position > mLastIndex )
                        {// down
                            if(position -fPosition > 4)
                            {

                                mChannelRecyclerView.scrollBy(0,(int)getResources().getDimension(R.dimen.channel_item_height));
                                Log.d(TAG,"position"+position);
                            }


                        }

                        //mLastIndex = position;

                        isChannelFocus = true;
                        isDateFocus = false;
                        isVideoFocus = false;


                    }
                    else{
                        mLastIndex = position;

                        ((TextView)v).setTextAppearance(getActivity(),R.style.ChannelStyle_Normal);

                        ((TextView)v).setBackgroundColor(Color.argb(0,79,79,79));
                    }
                }
            });



        }

        @Override
        public int getItemCount(){
            return mChannels.size();
        }

    }

    private class DateHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;

        public DateHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView)itemView;

        }
    }

    private class DateAdapter extends RecyclerView.Adapter<DateHolder> {
        private List<DateInfo> mDateInfos;

        public int mLastIndex = 0;

        public DateAdapter(){
            mDateInfos = new ArrayList<DateInfo>();

            for(int i= 0 ;i < 7;i++)
            {
                DateInfo dateInfo = new DateInfo("none");
                dateInfo.setDate("none"+i);
                mDateInfos.add(dateInfo);
            }

            notifyDataSetChanged();
        }


        public void ChangeList(List<DateInfo> dateInfos){
            mDateInfos = dateInfos;

//            for(int i = 0 ;i<mDateInfos.size();i++)
//            {
//                mDateInfos.set(i,dateInfos.get(i));
//            }

            notifyDataSetChanged();
        }



        @Override
        public DateHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_date,parent,false);

            view.setBackgroundColor(getResources().getColor(R.color.backDateItem));

            return new DateHolder(view);
        }

        @Override
        public void onBindViewHolder(final DateHolder holder, final int position)
        {
            holder.setIsRecyclable(false);

            DateInfo dateInfo = mDateInfos.get(position);

            String dateString = dateInfo.getDate();

            holder.mTitleTextView.setText(dateInfo.getDate());

            if(position == getItemCount()-1){
                holder.mTitleTextView.setNextFocusDownId(R.id.recycler_view2);
            }else{

                holder.mTitleTextView.setNextFocusDownId(0);
                holder.mTitleTextView.setNextFocusUpId(0);
            }



            holder.mTitleTextView.setFocusable(true);
            holder.mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (mOnItemClickLitener != null){
//                        mOnItemClickLitener.onItemClick(holder.itemView, position);
//                    }
            TextView t = (TextView)v;

            DateInfo dateInfo = mDateInfos.get(position);

                    Log.d(TAG,"CLICK!"+"position:"+ position + "url:"+ dateInfo.getDate());

                }
            });

            if(position == mLastIndex)
            {
//                holder.mTitleTextView.setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));
                holder.mTitleTextView.setBackground(getResources().getDrawable(R.drawable.item_date));
            }

            holder.mTitleTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    //Log.d("RecyclerAdapter","onFocusChange hasFocus:" + hasFocus + ",position:" + position);
                    DateInfo dateInfo = mDateInfos.get(position);
                    if (hasFocus){
                        Log.d(TAG,"FOCUS!"+"position:"+ position + "url:"+ dateInfo.getDate());
                        ((TextView)v).setTextColor(getResources().getColor(R.color.textYellow));
                        ((TextView)v).setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));

                        mChannelAdapter.mFocusCache = false;


                        if(isChannelFocus){
                            isChannelFocus = false;
                            isDateFocus = true;
                            isVideoFocus = false;

                            //int fPosition =  lm.findFirstVisibleItemPosition();

                           // Log.v(TAG,"last index:"+ mLastIndex +"firstVisibleItemPosition:"+ fPosition) ;

                            mDateRecyclerView.getChildAt(mLastIndex).requestFocus();

                            int fPosition =  lm.findFirstVisibleItemPosition();

                            //mChannelRecyclerView.getChildAt(mChannelAdapter.mLastIndex - fPosition ).setBackground(getResources().getDrawable(R.drawable.item_channel));
                            ((TextView) mChannelRecyclerView.getChildAt(mChannelAdapter.mLastIndex - fPosition )).setTextAppearance(getActivity(),R.style.ChannelStyle_Selected);



                            return;
                        }

                        if(isDateFocus)
                        {
                            ((TextView)v).setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));

                        }

                        if(isVideoFocus){
                            isChannelFocus = false;
                            isDateFocus = true;
                            isVideoFocus = false;

                            mDateRecyclerView.getChildAt(mLastIndex).requestFocus();

                            return;
                        }

                        mVideoAdapter.ChangeList(dateInfo.mReplayVideos);

                        isChannelFocus = false;
                        isDateFocus = true;
                        isVideoFocus = false;

                    }else{
                        mLastIndex = position;
                        Log.d(TAG,"REMOVE BACK BLUE");
                        ((TextView)v).setTextColor(getResources().getColor(R.color.textWhite));
                        ((TextView)v).setBackgroundColor(getResources().getColor(R.color.backDateItem));
                    }


                }
            });


        }

        @Override
        public int getItemCount(){
            return mDateInfos.size();
        }

    }


    private class VideoHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;

        public TextView mTitleTextView;
        public TextView mTimeTextView;

        public VideoHolder(View itemView) {
            super(itemView);

            mLinearLayout = (LinearLayout) itemView;

            mTitleTextView = (TextView)mLinearLayout.findViewById(R.id.video_title);
            mTimeTextView = (TextView)mLinearLayout.findViewById(R.id.video_time);


        }
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {
        private List<ReplayVideo> mReplayVideos;

        public int mLastIndex= 0;
        public int mLastClickIndex = 0;

        public ReplayVideo mLastVideo;

        public VideoAdapter(){
            mReplayVideos = new ArrayList<ReplayVideo>();



            for(int i= 0 ;i < 30;i++)
            {
                ReplayVideo replayVideo = new ReplayVideo();
                replayVideo.setTitle("NONE"+i);

                mReplayVideos.add(replayVideo);
            }
//
//            notifyDataSetChanged();
        }

        public void ChangeList(List<ReplayVideo> replayVideos) {
            //mReplayVideos = replayVideos;
            Log.d("CHANGE","CHANGELIST size:" + replayVideos.size());
            mReplayVideos.clear();
            for (int i = 0; i < replayVideos.size(); i++)
            {
                mReplayVideos.add(replayVideos.get(i));
            }

            notifyDataSetChanged();
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_video,parent,false);

            return new VideoHolder(view);
        }

        @Override
        public void onBindViewHolder(final VideoHolder holder, final int position)
        {
            final ReplayVideo replayVideo = mReplayVideos.get(position);

            holder.mTitleTextView.setText(replayVideo.getTitle());
            holder.mTimeTextView.setText(replayVideo.getStart_time() + " -- " +replayVideo.getEnd_time());


           //Log.v(TAG,"bind finished" + replayVideo.getFinished());
            if("0".equals(replayVideo.getFinished()))
            {
                holder.mTitleTextView.setTextColor(getResources().getColor(R.color.textGray));
                holder.mTimeTextView.setTextColor(getResources().getColor(R.color.textGray));

            }else{
                holder.mTitleTextView.setTextColor(getResources().getColor(R.color.textWhite));
                holder.mTimeTextView.setTextColor(getResources().getColor(R.color.textWhite));

            }

           // holder.mTitleTextView.setMovementMethod(new ScrollingMovementMethod());

            if(0 == position ){
                holder.mTitleTextView.setNextFocusUpId(R.id.recycler_view3);
            }else if(position == getItemCount()-1){
                holder.mTitleTextView.setNextFocusDownId(R.id.recycler_view3);
            }else{

                holder.mTitleTextView.setNextFocusDownId(0);
                holder.mTitleTextView.setNextFocusUpId(0);
            }

            //holder.mLinearLayout.setFocusable(true);
            holder.mTitleTextView.setFocusable(true);



            //holder.mTitleTextView.setFocusable(true);
            holder.mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (mOnItemClickLitener != null){
//                        mOnItemClickLitener.onItemClick(holder.itemView, position);
//                    }

                    ReplayVideo replayVideo1 = mReplayVideos.get(position);
                    Log.d(TAG,"positon:"+ position+ "  mLastClick:"+mLastClickIndex);



                    if(replayVideo != mLastVideo)
                    {
                        if(!"".equals(replayVideo.getUrl()))
                        {

                            mPlayVideoCallBack.onPlayVideo(replayVideo.getUrl(),null,replayVideo.getTitle());
                            mChannelAdapter.mLastClickIndex = mChannelAdapter.mLastIndex;
                        }

                        //FrameLayout root = (FrameLayout)(getActivity().findViewById(R.id.fragment_siderbar));

                    }

                    mRoot.setVisibility(View.GONE);
                    isVisibleCache = false;
                    Log.d(TAG,"CLICK!"+"position:"+ position + "url:"+ replayVideo.getTitle());
                    mLastClickIndex = position;

                    mLastVideo = replayVideo;
                    isLive = false;

                }
            });

            holder.mTitleTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    //Log.d("RecyclerAdapter","onFocusChange hasFocus:" + hasFocus + ",position:" + position);
                    ReplayVideo replayVideo1 = mReplayVideos.get(position);
                    if (hasFocus){
                        Log.d(TAG,"FOCUS!"+"position:"+ position + "url:"+ replayVideo.getUrl());



                        ((TextView)v).setTextColor(getResources().getColor(R.color.textYellow));
                        ((View)v.getParent()).setBackgroundColor(getResources().getColor(R.color.backChannelItemSelected));

                        if(isDateFocus){
                            Log.d(TAG,"ADD BACK BLUE");
                            //((TextView) mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex)).setBackgroundColor(getResources().getColor(R.color.backDateItemSelected));
                            ((TextView) mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex)).setBackground(getResources().getDrawable(R.drawable.item_date));


                        }

                        int fPosition =  lm3.findFirstVisibleItemPosition();
                        if( position > mLastIndex ) {// down\
                            if(position -fPosition > 3)
                            {

                                mVideoRecyclerView.scrollBy(0,(int)getResources().getDimension(R.dimen.video_item_height) + (int)getResources().getDimension(R.dimen.video_time_item_height) );
                            }


                        }



                        isChannelFocus = false;
                        isDateFocus = false;
                        isVideoFocus = true;

                    }else{
                        mLastIndex = position;

                        Log.d(TAG,"finishe:"+ replayVideo.getFinished());
                        if("1".equals(replayVideo.getFinished()))
                        {
                            ((TextView)v).setTextColor(getResources().getColor(R.color.textWhite));

                        }else  ((TextView)v).setTextColor(getResources().getColor(R.color.textGray));


                        ((View)v.getParent()).setBackgroundColor(Color.argb(0,0,0,0));

                    }


                }
            });



        }

        @Override
        public int getItemCount(){
            return mReplayVideos.size();
        }

    }

    private class FetchInfoTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground( Void... params) {

            if(mProvider.FetchChannels(getContext())){


                while
                        (!isLoaded){

                }

                isDownloaded = true;

                mChannelAdapter.ChangeList(mProvider.mChannels);
                if(mProvider.mChannels.size()>0)
                mPlayVideoCallBack.onPlayVideo(mProvider.mChannels.get(0).getUrl(),null,null);

                mProvider.FetchVideos();
                //ReplayTest[] = mProvider.ReplayTest[];
                //Log.d(TAG, "ReplayTest "+ ReplayTest);
            }



            return null;

        }

    }

    public void onKeyUp(int keyCode, KeyEvent event)
    {
        if (!keydown) {
            if (isDownloaded && isLoaded) {
                if (mRoot.getVisibility() == View.GONE || mRoot.getVisibility() == View.INVISIBLE) {
                    if(isChannelFocus) {
                        if (keyCode == 19 || keyCode == 20) // up and down
                        {
                            Channel channel = mProvider.mChannels.get(mChannelAdapter.mLastClickIndex);
                            String name = channel.getName();
                            mPlayVideoCallBack.onPlayVideo(channel.getUrl(), String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + name, null);

                        }
                    }
                }
            }
        }
        if (keyCode == 92)//page_up
        {
            Log.d(TAG, "page_up pressed");
            leftToRight = false;
            if ((mRoot.getVisibility() != View.GONE && mRoot.getVisibility() != View.INVISIBLE)) {
                if (isChannelFocus) {
                    if (!isDateFocus) {
                        if (!isVideoFocus) {
                            //在频道栏 变化
                            mChannelTag-=8;
                            leftToRight=false;
                            Log.d(TAG, "mChannelTag3" + mChannelTag);
                            mChannelRecyclerView.scrollBy(0,(int)getResources().getDimension(R.dimen.channel_item_height)*(-8)-7);
                            Log.d(TAG,"position");
                            mChannelRecyclerView.getChildAt(0).requestFocus();
                        }
                    }

                }

                Log.d(TAG, "mChnnelTag4" + mChannelTag);
            }

        }

        if (keyCode == 93)//page_down
        {
            Log.d(TAG, "page_down pressed");
            leftToRight = false;
            if ((mRoot.getVisibility() != View.GONE && mRoot.getVisibility() != View.INVISIBLE)) {
                if (isChannelFocus) {
                    if (!isDateFocus) {
                        if (!isVideoFocus) {
                            //在频道栏 变化
                            mChannelTag+=8;
                            leftToRight=false;
                            Log.d(TAG, "mChannelTag3" + mChannelTag);
                            mChannelRecyclerView.scrollBy(0,(int)getResources().getDimension(R.dimen.channel_item_height)*8+9);
                            Log.d(TAG,"position");
                            mChannelRecyclerView.getChildAt(0).requestFocus();

                        }
                    }

                }

                Log.d(TAG, "mChnnelTag4" + mChannelTag);
            }

        }
    }


    public void onKeyDown(int keyCode, KeyEvent event) {


        //Toast.makeText(getActivity(),"sidebar keycode:" + keyCode,Toast.LENGTH_SHORT).show();


        if (isDownloaded && isLoaded) {

            if (keyCode >= 7 && keyCode <= 16) {
                if (isLive) {
                    int temp;

                    if (MainActivity.curNum == -1) {
                        temp = keyCode - 7;
                    } else {
                        if (MainActivity.curNum > 99) MainActivity.curNum = 0;
                        //temp = MainActivity.curNum % 1000;
                        temp = MainActivity.curNum * 10 + keyCode - 7;
                    }

                    MainActivity.curNum = temp;
                    if (temp <= mProvider.mChannels.size()) {

                        mPlayVideoCallBack.onPressNumber(temp, mProvider.mChannels.get(temp - 1).getName(), mProvider.mChannels.get(temp - 1).getUrl());
                        mChannelTag=temp-1;
                        mChannelIndex=temp-1;
                        Log.d(TAG,"按数字键跳转"+mChannelTag);

                    } else mPlayVideoCallBack.onPressNumber(temp, "", "");


                }


            }


            if (keyCode == 23 || keyCode == 66) {
                leftToRight = true;
                Log.d(TAG, "enter pressed!");
                Log.d(TAG, String.valueOf(isChannelFocus) + String.valueOf(isDateFocus) + String.valueOf(isVideoFocus));


                // FrameLayout root = (FrameLayout)(getActivity().findViewById(R.id.fragment_siderbar));

                // mChannelAdapter.notifyDataSetChanged();

                if (mRoot.getVisibility() == View.GONE || mRoot.getVisibility() == View.INVISIBLE)
                {
                    Log.d(TAG, "change visibility from invisible to visible");
                    mRoot.setVisibility(View.VISIBLE);

                    if (!isLive) {
                        if (fl2.getVisibility() == View.INVISIBLE) {
                            fl2.setVisibility(View.VISIBLE);
                        }

                        int fPosition = lm3.findFirstVisibleItemPosition();

                        // Log.d(TAG,"last index:"+ mVideoAdapter.mLastIndex +"firstVisibleItemPosition:"+ fPosition) ;

                        mVideoRecyclerView.getChildAt(mVideoAdapter.mLastClickIndex - fPosition).requestFocus();

                    }

                    //mChannelRecyclerView.getChildAt(3).requestFocus();


                    if (isLive) {

                        if (fl2.getVisibility() == View.VISIBLE) {
                            fl2.setVisibility(View.INVISIBLE);
                        }

                        int fPosition = lm.findFirstVisibleItemPosition();

                        Log.d(TAG, "lastindex:" + mChannelAdapter.mLastIndex + "  fPositon:" + fPosition);


                        if (mChannelAdapter.mLastClickIndex - fPosition >= 9) {
                            mChannelRecyclerView.scrollBy(0, (int) getResources().getDimension(R.dimen.channel_item_height) * (mChannelAdapter.mLastClickIndex - 8));
                        }

                        if (mChannelAdapter.mLastClickIndex - fPosition < 0) {
                            mChannelRecyclerView.scrollBy(0, (int) getResources().getDimension(R.dimen.channel_item_height) * (mChannelAdapter.mLastClickIndex - fPosition));

                        }


                        Log.d(TAG, "last index:" + mChannelAdapter.mLastClickIndex + "firstVisibleItemPosition:" + fPosition);
                        // mChannelRecyclerView.getChildAt(mLastClickIndex - fPosition).requestFocus();

                        mChannelRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int fPosition = lm.findFirstVisibleItemPosition();
                                Log.d(TAG, "last index2:" + mChannelAdapter.mLastIndex + "firstVisibleItemPosition:" + fPosition);

                                mChannelRecyclerView.getChildAt(mChannelAdapter.mLastClickIndex - fPosition).requestFocus();
                            }
                        }, 100);


                        //mChannelRecyclerView.getChildAt(mChannelAdapter.mLastIndex - fPosition).requestFocus();


                        //mChannelRecyclerView.requestFocus();

                    }

                } else
                    {
                    Log.d(TAG, "change visibility from visible to INvisible");
                    mRoot.setVisibility(View.GONE);
                    isVisibleCache = false;


                }

            }



            if (keyCode == 22)//right
            {

                Log.d(TAG, "right pressed!!");
                Log.d(TAG, String.valueOf(isChannelFocus) + String.valueOf(isDateFocus) + String.valueOf(isVideoFocus));
                if (mRoot.getVisibility() == View.GONE || mRoot.getVisibility() == View.INVISIBLE) {

                    if (isVideoFocus) {
                        mPlayVideoCallBack.onSeekTo(SEEK_TIME);
                    } else {
                        leftToRight = true;
                        mRoot.setVisibility(View.VISIBLE);
                        mChannelTag=mChannelAdapter.mLastClickIndex;
                        mChannelIndex=mChannelAdapter.mLastClickIndex;
                    }

                } else {
                    if (isChannelFocus) {
                        if (fl2.getVisibility() == View.INVISIBLE) {
                            if (mProvider.ReplayTest[mChannelIndex]) {
                                fl2.setVisibility(View.VISIBLE);
                                //   MoveLeft();
                                //  fl3.setVisibility(View.VISIBLE);
                            }

                        }
                    }


                }


            }

            if (keyCode == 21)//left
            {
                Log.d(TAG, "left pressed!!");
                leftToRight = false;
                if (isChannelFocus) {
                    if (fl2.getVisibility() == View.VISIBLE) {
//                    {   mChannelTag=mChannelAdapter.mLastClickIndex;
//                        Log.v(TAG, "mChannelTag5" + mChannelTag+"mChannelAdapter.mLastClickIndex5" + mChannelAdapter.mLastClickIndex );

                        fl2.setVisibility(View.INVISIBLE);
                    } else {
                        mRoot.setVisibility(View.GONE);
                        mChannelTag=mChannelAdapter.mLastClickIndex;
                        mChannelIndex=mChannelAdapter.mLastClickIndex;
//                        mChannelTag=mChannelAdapter.mLastClickIndex;
//                        Log.v(TAG, "mChannelTag5" + mChannelTag+"mChannelAdapter.mLastClickIndex5" + mChannelAdapter.mLastClickIndex );


                        if (!isLive) {
                            mChannelRecyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Channel channel = mProvider.mChannels.get(mChannelAdapter.mLastIndex);
                                    mPlayVideoCallBack.onPlayVideo(channel.getUrl(), String.format("%03d", mChannelAdapter.mLastIndex + 1) + "\n" + channel.getName(), null);
                                    mChannelAdapter.mLastClickIndex = mChannelAdapter.mLastIndex;
                                    isLive = true;

                                }
                            }, 100);
                        }


                        isVisibleCache = false;
                    }
                }

                if (mRoot.getVisibility() == View.GONE || mRoot.getVisibility() == View.INVISIBLE) {
                    if (isVideoFocus) {
                        mPlayVideoCallBack.onSeekTo(-1 * SEEK_TIME);
                    }
                }


            }

           if (keyCode == 20) //down
           {
                Log.d(TAG, "down pressed!!");
                leftToRight = false;
                if ((mRoot.getVisibility() != View.GONE && mRoot.getVisibility() != View.INVISIBLE)) {
                    if (isChannelFocus) {
                        if (!isDateFocus) {
                            if (!isVideoFocus) {
                                //在频道栏 变化
                                mChannelTag++;
                                leftToRight=false;
                                Log.d(TAG, "mChannelTag3" + mChannelTag);
                            }
                        }

                    }

                    Log.d(TAG, "mChnnelTag4" + mChannelTag);
                }

                if (mRoot.getVisibility() == View.GONE || mRoot.getVisibility() == View.INVISIBLE) {

                    if (isChannelFocus) {

                        if (mChannelAdapter.mLastClickIndex + 1 < mProvider.mChannels.size()) {
                            mChannelAdapter.mLastClickIndex++;
                            mChannelAdapter.mLastIndex = mChannelAdapter.mLastClickIndex;
                            mChannelTag = mChannelAdapter.mLastClickIndex;


                        } else { // 在直播focus 回到第一个节目
                            mChannelAdapter.mLastClickIndex = 0;
                            mChannelAdapter.mLastIndex = mChannelAdapter.mLastClickIndex;
                            mChannelTag = 0;

                        }
                        Channel channel = mProvider.mChannels.get(mChannelAdapter.mLastClickIndex);
                        String name = channel.getName();
                        Log.d(TAG, "event.getRepeatCount" + event.getRepeatCount());
//
                        if (event.getRepeatCount() == 0) {
                            mPlayVideoCallBack.onPlayVideo(channel.getUrl(), String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + name, null);
                            keydown=true;
                        }
                        else
                        {
                            mPlayVideoCallBack.InfoUpdate(String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + name);
                            keydown =false;
                        }


//                        }
                        //                       else {  keydown =  false; }

                    }
                }

            }

            if (keyCode == 19)  //up
            {

                Log.d(TAG, "up pressed!!");
                if (mRoot.getVisibility() != View.GONE || mRoot.getVisibility() != View.INVISIBLE) {

                    if (isChannelFocus) {
                        if (!isDateFocus) {
                            if (!isVideoFocus) {
                                mChannelTag--;
                                leftToRight=false;
                                Log.d(TAG, "mChannelTag5" + mChannelTag);
                            }
                        }

                    }
                }
                if (mRoot.getVisibility() == View.GONE || mRoot.getVisibility() == View.INVISIBLE)

                //侧边栏未出现
                {
                    if (isChannelFocus) {

                        if (mChannelAdapter.mLastClickIndex == 0) {
                            mChannelAdapter.mLastClickIndex = mChannelAdapter.getItemCount() - 1;
                            mChannelAdapter.mLastIndex = mChannelAdapter.mLastClickIndex;
                            mChannelTag = mChannelAdapter.mLastClickIndex;

                        } else {

                            mChannelAdapter.mLastClickIndex--;
                            mChannelAdapter.mLastIndex = mChannelAdapter.mLastClickIndex;
                            mChannelTag = mChannelAdapter.mLastClickIndex;
                            mChannelTag = mChannelAdapter.mLastClickIndex;

                            Log.d(TAG, "mChannelTag6" + mChannelTag);

                        }

                        Channel channel = mProvider.mChannels.get(mChannelAdapter.mLastClickIndex);
                        Log.d(TAG, "event.getRepeatCount" + event.getRepeatCount());


                        String name = channel.getName();

                        if (event.getRepeatCount() == 0) {
                            mPlayVideoCallBack.onPlayVideo(channel.getUrl(), String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + name, null);
                            keydown = true ;
                        }
                        else
                        {
                            mPlayVideoCallBack.InfoUpdate(String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + name);
                            keydown = false;
                        }
//                        InfoFragment mInfoFragment = (InfoFragment)getActivity()
//                                .getSupportFragmentManager()
//                                .findFragmentByTag("mInfoFragment");
//
//                        mInfoFragment.Update(String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + name);
//
//
//                        if (event.getRepeatCount() == 0 || event.getRepeatCount() == 1 ) {
//
//                            mPlayVideoCallBack.onPlayVideo(channel.getUrl(), String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + name, null);
//                            keydown = true;
//                        }   else {  keydown =  false; }
//                    }
                    }


                }


            }


        }
    }


    private void MoveLeft(){
        FrameLayout root = (FrameLayout)(getActivity().findViewById(R.id.fragment_siderbar));

        float start = root.getLeft();
        float end = start - 400;

        Log.d(TAG, "start:"+start +" end:"+end);

        ObjectAnimator leftAnimator = ObjectAnimator.ofFloat(root,"x",start,end)
                .setDuration(600);
        leftAnimator.setInterpolator(new DecelerateInterpolator());


        leftAnimator.start();

    }

    private void MoveRight(){
        FrameLayout root = (FrameLayout)(getActivity().findViewById(R.id.fragment_siderbar));

        float start = -400;//root.getLeft();
        float end = 0;

        Log.d(TAG, "start:"+start +" end:"+end);

        ObjectAnimator leftAnimator = ObjectAnimator.ofFloat(root,"x",start,end)
                .setDuration(600);
        leftAnimator.setInterpolator(new DecelerateInterpolator());


        leftAnimator.start();

    }

    private PlayVideoCallBack mPlayVideoCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PlayVideoCallBack)) {
            throw new IllegalStateException("Interface not founded");
        }
        mPlayVideoCallBack=(PlayVideoCallBack) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPlayVideoCallBack = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isLoaded = true;
    }

    //定义一个业务接口
    //该Fragment所在Activity需要实现该接口
    //该Fragment将通过此接口与它所在的Activity交互
    public interface PlayVideoCallBack{
        public void onPlayVideo(String url,String title,String replayTitle);
        public void InfoUpdate(String title);
        public void onSeekTo(int second);
        public void onPressNumber(int num,String title,String url);
    }

    public void setCurChannelPosition(int i)
    {
        mChannelAdapter.mLastClickIndex = i ;
        mChannelAdapter.mLastIndex = i;
    }


    public void onBackPressed() {

        if(mRoot.getVisibility() != View.GONE)
        {
            if(isVideoFocus)
            {
                mDateRecyclerView.getChildAt(mDateAdapter.mLastIndex).requestFocus();
                return;
            }

            if(isDateFocus){
                int fPosition =  lm.findFirstVisibleItemPosition();

                //Log.d(TAG,"lastindex:"+ mChannelAdapter.mLastIndex + "  fPositon:"+fPosition);
                mChannelRecyclerView.getChildAt(mChannelAdapter.mLastIndex - fPosition).requestFocus();

                return;
            }


            if(isChannelFocus)
            {
                Log.d(TAG,"isChannelFocus");
                //从节目栏焦点 日期栏 video栏 都在 直接返回直播
                //收起 日期 video栏
                if(fl2.getVisibility()==View.VISIBLE)
                {
                    fl2.setVisibility(View.INVISIBLE);
                    Log.d(TAG,"f12 visible to invisible");
                }
                else {
                    //日期栏 video栏不可见

                    if (mRoot.getVisibility() == View.VISIBLE)
                    {
                        mRoot.setVisibility(View.GONE);
                        isVisibleCache=false;
                        Log.d(TAG, "mRoot visible to gone");

                    }
                    else {
                        Log.d(TAG, "mRoot invisible");


                        if (!isLive) {
                            Log.d(TAG, "isLive=false");
                            mChannelRecyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Channel channel = mProvider.mChannels.get(mChannelAdapter.mLastIndex);
                                    mPlayVideoCallBack.onPlayVideo(channel.getUrl(), String.format("%03d", mChannelAdapter.mLastClickIndex + 1) + "\n" + channel.getName(), null);
                                    mChannelAdapter.mLastClickIndex = mChannelAdapter.mLastIndex;
                                    isLive = true;

                                }
                            }, 100);
                        } else
                            {
                            Log.d(TAG, "isLive = true");
                            if (mRoot.getVisibility() != View.VISIBLE) {
                                getActivity().finish();//结束Activity
                                Log.d(TAG, "mRoot gone finish ");
                            }
                        }
                        isVisibleCache = false;


                    }
                }


            }
        }
        else
        {
            getActivity().finish();
            Log.d(TAG,"mSidebarFragment onBackPressed");
        }



    }



}
