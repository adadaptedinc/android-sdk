package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.AdImageLoader;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.ad.model.ImageAdType;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.ext.http.HttpAdImageLoader;

/**
 * Created by chrisweeden on 5/20/15
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
    public void buildView(final Ad ad,
                          final int width,
                          final int height,
                          final AaZoneViewProperties zoneProperties) {
        mView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        mView.setBackgroundColor(zoneProperties.getBackgroundColor());

        final ImageAdType adType = (ImageAdType) ad.getAdType();

        final String imageResolution = mDeviceInfo.chooseImageSize();

        final String imageUrl = adType.getImageUrlFor(imageResolution, getPresentOrientation());
        mImageLoader.getImage(imageUrl, new AdImageLoader.Callback() {
            @Override
            public void adImageLoaded(final Bitmap bitmap) {
                mAdImage = bitmap;
                buildAdHandler.post(buildAdRunnable);

                if(mListener != null) {
                    mListener.onStrategyViewLoaded();
                }
            }

            @Override
            public void adImageLoadFailed() {
                if(mListener != null) {
                    mListener.onStrategyViewLoadFailed();
                }
            }
        });
    }

    @Override
    public String toString() {
        return "ImageAdViewBuildingStrategy{}";
    }
}
