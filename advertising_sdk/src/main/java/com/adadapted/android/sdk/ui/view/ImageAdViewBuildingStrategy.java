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
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;

class ImageAdViewBuildingStrategy implements AdViewBuildingStrategy {
    private static final String LOGTAG = ImageAdViewBuildingStrategy.class.getName();

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

    private Listener mListener;

    ImageAdViewBuildingStrategy(final Context context,
                                final DeviceInfo deviceInfo) {
        mContext = context.getApplicationContext();
        mDeviceInfo = deviceInfo;

        mView = new ImageView(context.getApplicationContext());
    }

    private String getPresentOrientation() {
        final Configuration configuration = mContext.getResources().getConfiguration();
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
        new HttpAdImageLoader().getImage(imageUrl, new AdImageLoader.Callback() {
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

                    AdAnomalyTrackingManager.registerAnomaly(
                        ad.getAdId(),
                        imageUrl,
                        "AD_IMAGE_LOAD_FAILED",
                        "Ad image failed to load."
                    );
                }
            }
        });
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }
}
