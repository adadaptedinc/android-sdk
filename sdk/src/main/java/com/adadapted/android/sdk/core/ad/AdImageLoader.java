package com.adadapted.android.sdk.core.ad;

import android.graphics.Bitmap;

/**
 * Created by chrisweeden on 5/20/15.
 */
public interface AdImageLoader {
    interface Listener {
        void onAdImageLoaded(Bitmap bitmap);
        void onAdImageLoadFailed();
    }

    void getImage(String url);
}
