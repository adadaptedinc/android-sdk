package com.adadapted.android.sdk.core.ad;

import android.graphics.Bitmap;

public interface AdImageLoader {
    void getImage(String url, Callback callback);

    interface Callback {
        void adImageLoaded(Bitmap bitmap);
        void adImageLoadFailed();
    }
}
