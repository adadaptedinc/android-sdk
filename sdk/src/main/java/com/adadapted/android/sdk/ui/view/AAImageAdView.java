package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
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
class AAImageAdView extends ImageView implements HttpAdImageLoader.Listener {
    private static final String TAG = AAImageAdView.class.getName();

    private final HttpAdImageLoader imageLoader;

    private Bitmap adImage;

    private final Runnable buildAdRunnable = new Runnable() {
        public void run() {
            Log.d(TAG, "Setting image view bitmap.");
            setImageBitmap(adImage);
        }
    };

    private final Handler buildAdHandler = new Handler();

    public interface Listener {
        void onImageViewLoaded();
    }

    private Listener listener;

    public AAImageAdView(Listener listener, Context context) {
        super(context);

        this.listener = listener;

        imageLoader = new HttpAdImageLoader();
        imageLoader.addListener(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
    }

    private String getPresentOrientation() {
        int orientation = getContext().getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return AdImage.LANDSCAPE;
        }

        return AdImage.PORTRAIT;
    }

    void loadImage(Ad ad) {
        ImageAdType adType = (ImageAdType) ad.getAdType();

        String imageResolution = AdAdapted.getInstance().getDeviceInfo().chooseImageSize();

        String imageUrl = adType.getImageUrlFor(imageResolution, getPresentOrientation());
        imageLoader.getImage(imageUrl);
    }

    @Override
    public void onAdImageLoaded(Bitmap bitmap) {
        Log.d(TAG, "Calling onAdImageLoaded()");

        adImage = bitmap;
        buildAdHandler.post(buildAdRunnable);

        listener.onImageViewLoaded();
    }

    @Override
    public void onAdImageLoadFailed() {
        Log.d(TAG, "Calling onAdImageLoadFailed()");
    }
}
