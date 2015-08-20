package com.adadapted.android.sdk.core.ad;

/**
 * Created by chrisweeden on 5/20/15.
 */
public interface AdImageLoader {
    void getImage(String url, AdImageLoaderListener listener);
}
