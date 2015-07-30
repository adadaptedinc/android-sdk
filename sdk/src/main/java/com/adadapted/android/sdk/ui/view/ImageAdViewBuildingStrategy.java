package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.ad.model.ImageAdType;
import com.adadapted.android.sdk.ext.http.HttpAdImageLoader;

/**
 * Created by chrisweeden on 5/20/15.
 */
class ImageAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String TAG = ImageAdViewBuildingStrategy.class.getName();

    private final HttpAdImageLoader imageLoader;

    private final Context context;
    private final ImageView view;

    private Bitmap adImage;

    private final Runnable buildAdRunnable = new Runnable() {
        public void run() {
            view.setImageBitmap(adImage);
        }
    };
    private final Handler buildAdHandler = new Handler(Looper.getMainLooper());

    private final Listener listener;

    public ImageAdViewBuildingStrategy(final Context context, final Listener listener) {
        this.context = context;
        this.listener = listener;

        imageLoader = new HttpAdImageLoader();
        view = new ImageView(context);
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
    public void buildView(Ad ad, int width, int height) {
        view.setLayoutParams(new ViewGroup.LayoutParams(width, height));

        ImageAdType adType = (ImageAdType) ad.getAdType();

        String imageResolution = AdAdapted.getInstance().getDeviceInfo().chooseImageSize();

        String imageUrl = adType.getImageUrlFor(imageResolution, getPresentOrientation());
        imageLoader.getImage(imageUrl, new HttpAdImageLoader.Listener() {
            @Override
            public void onAdImageLoaded(Bitmap bitmap) {
                adImage = bitmap;
                buildAdHandler.post(buildAdRunnable);

                listener.onStrategyViewLoaded();
            }

            @Override
            public void onAdImageLoadFailed() {
                listener.onStrategyViewLoadFailed();
            }
        });
    }

    @Override
    public void buildView(Ad ad, int width, int height, int resourceId) {
        buildView(ad, width, height);
    }

    @Override
    public String toString() {
        return "ImageAdViewBuildingStrategy{}";
    }
}
