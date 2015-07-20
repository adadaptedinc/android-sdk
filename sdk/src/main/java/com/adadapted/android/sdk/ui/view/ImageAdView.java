package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.ad.model.ImageAdType;
import com.adadapted.android.sdk.ext.http.HttpAdImageLoader;

/**
 * Created by chrisweeden on 5/20/15.
 */
class ImageAdView implements AdView {
    private static final String TAG = ImageAdView.class.getName();

    private final HttpAdImageLoader imageLoader;

    private final Context context;
    private final ImageView view;

    private Bitmap adImage;

    private final Runnable buildAdRunnable = new Runnable() {
        public void run() {
            view.setImageBitmap(adImage);
        }
    };
    private final Handler buildAdHandler = new Handler();

    public interface Listener {
        void onImageViewLoaded();
    }

    private final Listener listener;

    public ImageAdView(final Context context, final Listener listener) {
        this.context = context;
        this.listener = listener;

        imageLoader = new HttpAdImageLoader();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        view = new ImageView(context);
        view.setLayoutParams(layoutParams);
    }

    private String getPresentOrientation() {
        int orientation = context.getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return AdImage.LANDSCAPE;
        }

        return AdImage.PORTRAIT;
    }

    public View getView() {
        return view;
    }

    @Override
    public void buildView(Ad ad) {
        ImageAdType adType = (ImageAdType) ad.getAdType();

        String imageResolution = AdAdapted.getInstance().getDeviceInfo().chooseImageSize();

        String imageUrl = adType.getImageUrlFor(imageResolution, getPresentOrientation());
        imageLoader.getImage(imageUrl, new HttpAdImageLoader.Listener() {
            @Override
            public void onAdImageLoaded(Bitmap bitmap) {
                adImage = bitmap;
                buildAdHandler.post(buildAdRunnable);

                listener.onImageViewLoaded();
            }

            @Override
            public void onAdImageLoadFailed() {}
        });
    }
}
