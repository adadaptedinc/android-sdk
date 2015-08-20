package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adadapted.android.sdk.core.ad.AdImageLoaderListener;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.ad.model.ImageAdType;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.ext.http.HttpAdImageLoader;

/**
 * Created by chrisweeden on 5/20/15.
 */
class ImageAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String LOGTAG = ImageAdViewBuildingStrategy.class.getName();

    private final HttpAdImageLoader mImageLoader;

    private final Context mContext;
    private final DeviceInfo mDeviceInfo;
    private final ImageView mView;

    private Bitmap mAdImage;

    private final Runnable buildAdRunnable = new Runnable() {
        public void run() {
            mView.setImageBitmap(mAdImage);
        }
    };

    private final Handler buildAdHandler = new Handler(Looper.getMainLooper());

    private final Listener mListener;

    public ImageAdViewBuildingStrategy(final Context context,
                                       final DeviceInfo deviceInfo,
                                       final Listener listener) {
        mContext = context;
        mDeviceInfo = deviceInfo;
        mListener = listener;

        mImageLoader = new HttpAdImageLoader();
        mView = new ImageView(context);
    }

    private String getPresentOrientation() {
        int orientation = mContext.getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return AdImage.LANDSCAPE;
        }

        return AdImage.PORTRAIT;
    }

    public View getView() {
        return mView;
    }

    @Override
    public void buildView(Ad ad, int width, int height) {
        mView.setLayoutParams(new ViewGroup.LayoutParams(width, height));

        ImageAdType adType = (ImageAdType) ad.getAdType();

        String imageResolution = mDeviceInfo.chooseImageSize();

        String imageUrl = adType.getImageUrlFor(imageResolution, getPresentOrientation());
        mImageLoader.getImage(imageUrl, new AdImageLoaderListener() {
            @Override
            public void onSuccess(final Bitmap bitmap) {
                mAdImage = bitmap;
                buildAdHandler.post(buildAdRunnable);

                mListener.onStrategyViewLoaded();
            }

            @Override
            public void onFailure() {
                mListener.onStrategyViewLoadFailed();
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
