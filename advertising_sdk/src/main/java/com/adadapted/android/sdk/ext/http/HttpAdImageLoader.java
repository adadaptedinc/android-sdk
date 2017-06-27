package com.adadapted.android.sdk.ext.http;

import android.graphics.Bitmap;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdImageLoader;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class HttpAdImageLoader implements AdImageLoader {
    private static final String LOGTAG = HttpAdImageLoader.class.getName();

    public HttpAdImageLoader() {}

    public void getImage(final String url,
                         final Callback callback) {
        if(url == null || !url.toLowerCase().startsWith("http")) {
            Log.w(LOGTAG, "No URL has been provided.");
            AdAnomalyTrackingManager.registerAnomaly("",
                url,
                "AD_IMAGE_REQUEST_FAILED",
                "No URL has been provided."
            );

            callback.adImageLoadFailed();

            return;
        }

        final Bitmap bitmap = ImageCache.getInstance().getImage(url);

        if(bitmap == null) {
            loadRemoteImage(url, callback);
        }
        else {
            callback.adImageLoaded(bitmap);
        }
    }

    private void loadRemoteImage(final String url,
                                 final Callback callback) {
        if(url == null || callback == null) {
            return;
        }

        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(final Bitmap bitmap) {
                        if (null == ImageCache.getInstance().getImage(url)) {
                            ImageCache.getInstance().putImage(url, bitmap);
                        }

                        callback.adImageLoaded(bitmap);
                    }

                }, 0, 0, Bitmap.Config.ARGB_8888,

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.w(LOGTAG, "Problem loading image URL: " + url);

                        if(error instanceof NoConnectionError || error instanceof NetworkError) {
                            return;
                        }

                        AdAnomalyTrackingManager.registerAnomaly("",
                            url,
                            "AD_IMAGE_REQUEST_FAILED",
                            error.getMessage()
                        );

                        callback.adImageLoadFailed();
                    }
                }
        );

        HttpRequestManager.queueRequest(imageRequest);
    }
}
