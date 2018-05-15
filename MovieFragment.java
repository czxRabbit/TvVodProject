package com.ts.cyd.tsreplay;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.View;

import com.ts.cyd.tsreplay.api.Video;
import com.ts.cyd.tsreplay.data.FetchVideoService;
import com.ts.cyd.tsreplay.data.VideoContract;

/**
 * Created by david on 2018/4/4.
 */

/*
 * MovieFragment shows a grid of videos that can be scrolled vertically.
 */
public class MovieFragment extends android.support.v17.leanback.app.VerticalGridFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NUM_COLUMNS = 5;
    private CursorObjectAdapter mVideoCursorAdapter =
            new CursorObjectAdapter(new CardPresenter());
    private static final int ALL_VIDEOS_LOADER = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVideoCursorAdapter.setMapper(new VideoCursorMapper());
        setAdapter(mVideoCursorAdapter);

        setTitle(getString(R.string.vertical_grid_title));

        if (savedInstanceState == null) {
            prepareEntranceTransition();
        }
        setupFragment();
    }

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);

        getLoaderManager().initLoader(ALL_VIDEOS_LOADER, null, this);

        // After 500ms, start the animation to transition the cards into view.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startEntranceTransition();
            }
        }, 500);

        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), SearchActivity.class);
//                startActivity(intent);
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                VideoContract.VideoEntry.CONTENT_URI,
                null, // projection
                null, // selection
                null, // selection clause
                null  // sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == ALL_VIDEOS_LOADER && cursor != null && cursor.moveToFirst()) {
            mVideoCursorAdapter.changeCursor(cursor);
        }
        // Start an Intent to fetch the videos.
        Intent serviceIntent = new Intent(getActivity(), FetchVideoService.class);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVideoCursorAdapter.changeCursor(null);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            //点击视频进入详情页
            if (item instanceof Video) {
                Video video = (Video) item;

//                Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
//                intent.putExtra(VideoDetailsActivity.VIDEO, video);
//
//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        getActivity(),
//                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
//                        VideoDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
//                getActivity().startActivity(intent, bundle);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
        }
    }
}
