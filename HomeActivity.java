package com.ts.cyd.tsreplay;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends Activity {
    public static final String TAG = "HomeActivity";
    protected BrowseFragment mBrowseFragment;
    public static String playsite = "1.8.6.210";
    public HomeActivity() throws IOException, JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final FragmentManager fragmentManager = getFragmentManager();
        mBrowseFragment = (BrowseFragment) fragmentManager.findFragmentById(
                R.id.browse_fragment);
        // Set display parameters for the BrowseFragment
//        mBrowseFragment.setHeadersState(BrowseFragment.HEADERS_ENABLED);
//        mBrowseFragment.setBadgeDrawable(getResources().getDrawable(
//                R.drawable.header));
        //设置镶边颜色
        //mBrowseFragment.setBrandColor(getResources().getColor(
        //        R.color.fastlane_background));
        buildRowsAdapter();
        setupEventListeners();

    }

    public class StringPresenter extends Presenter {
        private static final String TAG = "StringPresenter";
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
            textView.setBackground(
                    parent.getContext().getResources().getDrawable(R.drawable.home_img));
            textView.setTextSize(40);//设置ArrayObjectAdapter的字体大小
            return new ViewHolder(textView);
        }
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText(item.toString());
        }
        public void onUnbindViewHolder(ViewHolder viewHolder) {
            // no op
        }
    }
    private ArrayObjectAdapter mRowsAdapter;

    private final int NUM_ROWS = 4;
    private void buildRowsAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        for (int i = 0; i < NUM_ROWS; ++i) {
            if(i == 0) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                        new StringPresenter());
                listRowAdapter.add("            直播回看");
                //CursorObjectAdapter
                HeaderItem header = new HeaderItem(i, "直播回看 " );
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
            }
            else if(i == 1) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                        new StringPresenter());
                listRowAdapter.add("            点播节目");

                HeaderItem header = new HeaderItem(i, "点播节目 " );
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
            }
            else if(i == 2) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                        new StringPresenter());

                listRowAdapter.add("            5号点");
                listRowAdapter.add("            6号点");
                listRowAdapter.add("            7号点");
                listRowAdapter.add("            自动");
                HeaderItem header = new HeaderItem(i, "设置IP " );
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
            }
            else if(i == 3) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                        new StringPresenter());
                listRowAdapter.add("            手动播放");

                HeaderItem header = new HeaderItem(i, "手动播放 " );
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
            }
            else {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                        new StringPresenter());
                listRowAdapter.add("Media Item 1");
                listRowAdapter.add("Media Item 2");
                listRowAdapter.add("Media Item 3");
                HeaderItem header = new HeaderItem(i, "点播类型 " + i);
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
            }

        }
        mBrowseFragment.setAdapter(mRowsAdapter);
    }
    @Override
    protected void onStop() {
        super.onStop();

//        new Thread(JsonCategory).start();
    }
    Runnable JsonCategory = new Runnable(){

        @Override
        public void run() {
            Log.d(TAG ,loadJson("http://1.8.6.210/vod/api/?main_category=%E7%94%B5%E5%BD%B1"));
            String vod_api_category = loadJson("http://1.8.6.210/vod/api/?main_category=%E7%94%B5%E5%BD%B1");
            String json = vod_api_category;
            try {
                JSONObject resultJson = new JSONObject(json);
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

                        String ID = albumJson.getString("id");
                        Log.d(TAG,">>id "+ID);
                        String title = albumJson.getString("title");
                        Log.d(TAG,">>title "+title);
                        String image = "http://1.8.6.210" + albumJson.getString("image");
                        Log.d(TAG,">>image "+image);
                        String category = albumJson.getString("category");
                        Log.d(TAG,">>category "+category);
                        String defination = albumJson.getString("definition");
                        Log.d(TAG,">>defination "+defination);
                        String duration = albumJson.getString("duration");
                        Log.d(TAG,">>duration "+duration);
                        String slug = albumJson.getString("slug");
                        Log.d(TAG,">>slug "+slug);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"json :");
            //new VODApi().onGetChannelAlbums(new VODChannel(channelId, context), pageNo, pageSize , listener)

        }
    };
    //跳转到直播录播界面
    private void startMainActivity() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
//        finish()代表结束当前activity，否则处于暂停状态
        finish();
    }
    private void setupEventListeners() {
        mBrowseFragment.setOnItemViewClickedListener(new ItemViewClickedListener());
        mBrowseFragment.setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }
    //点击视频图标监听事件
    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof String) {
                if (((String) item).contains("直播回看")) {
                    startMainActivity();
                } else if (((String) item).contains("点播节目")) {
                    Intent intent = new Intent(HomeActivity.this, WebviewActivity.class);
                    startActivity(intent);

                }
                else if (((String) item).contains("手动播放")) {
                    Intent intent = new Intent(HomeActivity.this, ManualPlay.class);
                    startActivity(intent);

                }else if (((String) item).contains("5")) {
//                    playsite = "10.13.5.2";//5号点IP
                    playsite = "1.8.6.210";//测试IP
                    Toast.makeText(HomeActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                }
                else if (((String) item).contains("6")) {
                    playsite = "10.13.6.2";
                    Toast.makeText(HomeActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                }
                else if (((String) item).contains("7")) {
                    playsite = "10.13.7.2";
                    Toast.makeText(HomeActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                }
                else if (((String) item).contains("自动")) {
                    playsite = "replay.com";
                    Toast.makeText(HomeActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                }else {
                    startMainActivity();
                }
            }
        }
    }
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof MediaStore.Video) {
                //选中item触发事件
                //mBackgroundURI = Uri.parse(((MediaStore.Video) item).bgImageUrl);

            }

        }
    }
    //从网络读取Json数据
    @NonNull
    public static String loadJson (String url) {
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




