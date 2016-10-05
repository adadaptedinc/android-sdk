package com.adadapted.android.sdk.core.ad;

import android.graphics.Bitmap;

/**
 * Created by chrisweeden on 5/20/15.
 */
public interface AdImageLoader {
    void getImage(String url, Callback callback);

    interface Callback {
        void adImageLoaded(Bitmap bitmap);
        void adImageLoadFailed();
    }
}
