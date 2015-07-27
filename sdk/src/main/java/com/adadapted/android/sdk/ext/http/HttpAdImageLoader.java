package com.adadapted.android.sdk.ext.http;

import android.graphics.Bitmap;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdImageLoader;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class HttpAdImageLoader implements AdImageLoader {
    private static final String TAG = HttpAdImageLoader.class.getName();

    public HttpAdImageLoader() {

    }

    public void getImage(final String url, final Listener listener) {
        if(url == null || !url.toLowerCase().startsWith("http")) {
            Log.w(TAG, "No URL has been provided.");
            notifyAdImageLoadFailed(listener);
            return;
        }

        Bitmap bitmap = ImageCache.getInstance().getImage(url);

        if(bitmap == null) {
            loadRemoteImage(url, listener);
        }
        else {
            notifyAdImageLoaded(listener, bitmap);
        }
    }

    private void loadRemoteImage(final String url, final Listener listener) {
        ImageRequest imageRequest = new ImageRequest(url,
            new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap response) {
                    if(null == ImageCache.getInstance().getImage(url)) {
                        ImageCache.getInstance().putImage(url, response);
                    }

                    notifyAdImageLoaded(listener, response);
                }

            }, 0, 0, Bitmap.Config.ARGB_8888,

            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    notifyAdImageLoadFailed(listener);
                }
            }
        );

        HttpRequestManager.getQueue().add(imageRequest);
    }

    private void notifyAdImageLoaded(Listener listener, Bitmap bitmap) {
        if(listener != null & bitmap != null) {
            listener.onAdImageLoaded(bitmap);
        }
        else {
            Log.w(TAG, "No Bitmap to return.");
        }
    }

    private void notifyAdImageLoadFailed(Listener listener) {
        if(listener != null) {
            listener.onAdImageLoadFailed();
        }
    }
}
