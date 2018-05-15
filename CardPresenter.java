package com.ts.cyd.tsreplay;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ts.cyd.tsreplay.api.Video;

import org.json.*;
import org.json.JSONObject;

import static com.ts.cyd.tsreplay.HomeActivity.TAG;
import static com.ts.cyd.tsreplay.HomeActivity.loadJson;

/**
 * Created by david on 2018/4/4.
 */

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {
    private int mSelectedBackgroundColor = -1;
    private int mDefaultBackgroundColor = -1;
    private Drawable mDefaultCardImage;
    public String mvod_api_category = "123456" ;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mDefaultBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.default_background);
        mSelectedBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.selected_background);
        mDefaultCardImage = parent.getResources().getDrawable(R.drawable.movie, null);

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            //选中之后改变颜色
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    private void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? mSelectedBackgroundColor : mDefaultBackgroundColor;

        // Both background colors should be set because the view's
        // background is temporarily visible during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
//        Video video = (Video) item;
//        Object albumJson = item;
//        Album album = new Album();
//        ImageCardView cardView = (ImageCardView) viewHolder.view;
//        cardView.setTitleText(album.getTitle());
//        //cardView.setContentText(video.studio);
//
//        if (album.getHorImgUrl() != null) {
//            // Set card size from dimension resources.
//            Resources res = cardView.getResources();
//            int width = res.getDimensionPixelSize(R.dimen.card_width);
//            int height = res.getDimensionPixelSize(R.dimen.card_height);
//            cardView.setMainImageDimensions(width, height);
//
//            Glide.with(cardView.getContext())
//                    .load(album.getHorImgUrl())
//                    .apply(RequestOptions.errorOf(mDefaultCardImage))
//                    .into(cardView.getMainImageView());
//        }
        Video video = (Video) item;

        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(video.title);
        cardView.setContentText(video.studio);

        if (video.cardImageUrl != null) {
            // Set card size from dimension resources.
            Resources res = cardView.getResources();
            int width = res.getDimensionPixelSize(R.dimen.card_width);
            int height = res.getDimensionPixelSize(R.dimen.card_height);
            cardView.setMainImageDimensions(width, height);

            Glide.with(cardView.getContext())
                    .load(video.cardImageUrl)
                    .apply(RequestOptions.errorOf(mDefaultCardImage))
                    .into(cardView.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        // Remove references to images so that the garbage collector can free up memory.
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
