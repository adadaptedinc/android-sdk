package com.adadapted.android.sdk;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

/**
 * Created by chrisweeden on 3/23/15.
 */
class ImageCache {
    private static ImageCache instance;
    private LruCache<String, Bitmap> imageCache;

    private ImageCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        imageCache = new LruCache<String, Bitmap>(cacheSize) {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    static ImageCache getInstance() {
        if(instance == null) {
            instance = new ImageCache();
        }

        return instance;
    }

    public Bitmap getImage(String url) {
        return imageCache.get(url);
    }

    public void putImage(String url, Bitmap bitmap) {
        imageCache.put(url, bitmap);
    }
}
