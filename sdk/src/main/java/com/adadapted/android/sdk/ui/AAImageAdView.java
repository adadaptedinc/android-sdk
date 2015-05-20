package com.adadapted.android.sdk.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdImage;
import com.adadapted.android.sdk.core.ad.ImageAdType;
import com.adadapted.android.sdk.ext.http.AdImageLoader;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 5/20/15.
 */
class AAImageAdView extends ImageView implements AdViewListenable, AdImageLoader.Listener {
    private static final String TAG = AAImageAdView.class.getName();

    private AdImageLoader imageLoader;
    private Bitmap adImage;

    private Set<AdViewListener> listeners;

    private final Runnable buildAdRunnable = new Runnable() {
        public void run() {
            Log.d(TAG, "Setting image view bitmap.");
            setImageBitmap(adImage);
        }
    };

    private final Handler buildAdHandler = new Handler();

    public AAImageAdView(Context context) {
        super(context);
        init();
    }

    public AAImageAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AAImageAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AAImageAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        imageLoader = new AdImageLoader();
        imageLoader.addListener(this);

        listeners = new HashSet<>();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        setScaleType(ImageView.ScaleType.FIT_XY);
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
