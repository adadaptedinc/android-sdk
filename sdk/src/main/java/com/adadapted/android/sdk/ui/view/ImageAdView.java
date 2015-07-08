package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
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
class ImageAdView extends ImageView implements AdView, HttpAdImageLoader.Listener {
    private static final String TAG = ImageAdView.class.getName();

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

    private final Listener listener;
    private AdInteractionListener adInteractionListener;

    public ImageAdView(final Context context, final Listener listener) {
        super(context);

        this.listener = listener;

        imageLoader = new HttpAdImageLoader();
        imageLoader.addListener(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        notifyOnClick();
                        break;
                }

                return true;
            }
        });
    }

    private String getPresentOrientation() {
        int orientation = getContext().getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return AdImage.LANDSCAPE;
        }

        return AdImage.PORTRAIT;
    }

    @Override
    public void buildView(Ad ad) {
        ImageAdType adType = (ImageAdType) ad.getAdType();

        String imageResolution = AdAdapted.getInstance().getDeviceInfo().chooseImageSize();

        String imageUrl = adType.getImageUrlFor(imageResolution, getPresentOrientation());
        imageLoader.getImage(imageUrl);
    }

    @Override
    public void setAdInteractionListener(AdInteractionListener listener) {
        this.adInteractionListener = listener;
    }

    @Override
    public void removeAdInteractionListener() {
        adInteractionListener = null;
    }

    private void notifyOnClick() {
        if(adInteractionListener != null) {
            adInteractionListener.onClick();
        }
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
