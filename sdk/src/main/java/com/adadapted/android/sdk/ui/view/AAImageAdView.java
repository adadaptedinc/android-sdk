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
import com.adadapted.android.sdk.ui.listener.AdViewListenable;
import com.adadapted.android.sdk.ui.listener.AdViewListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 5/20/15.
 */
class AAImageAdView extends ImageView implements AdViewListenable, HttpAdImageLoader.Listener {
    private static final String TAG = AAImageAdView.class.getName();

    private final HttpAdImageLoader imageLoader;
    private final Set<AdViewListener> listeners;

    private Bitmap adImage;

    private final Runnable buildAdRunnable = new Runnable() {
        public void run() {
            Log.d(TAG, "Setting image view bitmap.");
            setImageBitmap(adImage);
        }
    };

    private final Handler buildAdHandler = new Handler();

    public AAImageAdView(Context context) {
        super(context);

        imageLoader = new HttpAdImageLoader();
        imageLoader.addListener(this);

        listeners = new HashSet<>();

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

    public void addListener(AdViewListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AdViewListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnViewLoaded() {
        for(AdViewListener listener : listeners) {
            listener.onViewLoaded();
        }
    }

    @Override
    public void onAdImageLoaded(Bitmap bitmap) {
        Log.d(TAG, "Calling onAdImageLoaded()");

        adImage = bitmap;
        buildAdHandler.post(buildAdRunnable);

        notifyOnViewLoaded();
    }

    @Override
    public void onAdImageLoadFailed() {
        Log.d(TAG, "Calling onAdImageLoadFailed()");
    }
}
