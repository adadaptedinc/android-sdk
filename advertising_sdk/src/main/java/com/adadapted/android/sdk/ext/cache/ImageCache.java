package com.adadapted.android.sdk.ext.cache;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

public class ImageCache {
    private static final String LOGTAG = ImageCache.class.getName();

    private static ImageCache instance;

    private final LruCache<String, Bitmap> imageCache;

    private ImageCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        imageCache = new LruCache<String, Bitmap>(cacheSize) {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public int sizeOf(final String key,
                              final Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static ImageCache getInstance() {
        if(instance == null) {
            instance = new ImageCache();
        }

        return instance;
    }

    public Bitmap getImage(final String url) {
        return imageCache.get(url);
    }

    public void putImage(final String url, final Bitmap bitmap) {
        imageCache.put(url, bitmap);
    }

    public void purgeCache() {
        imageCache.evictAll();
    }
}
