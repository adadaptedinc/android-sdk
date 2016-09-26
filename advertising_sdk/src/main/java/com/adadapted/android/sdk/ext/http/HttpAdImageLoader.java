package com.adadapted.android.sdk.ext.http;

import android.graphics.Bitmap;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdImageLoader;
import com.adadapted.android.sdk.core.ad.AdImageLoaderListener;
import com.adadapted.android.sdk.ext.cache.ImageCache;
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class HttpAdImageLoader implements AdImageLoader {
    private static final String LOGTAG = HttpAdImageLoader.class.getName();

    public HttpAdImageLoader() {}

    public void getImage(final String url,
                         final AdImageLoaderListener listener) {
        if(url == null || !url.toLowerCase().startsWith("http")) {
            Log.w(LOGTAG, "No URL has been provided.");
            AnomalyTrackerFactory.registerAnomaly("",
                    url,
                    "AD_IMAGE_REQUEST_FAILED",
                    "No URL has been provided.");
            listener.onFailure();
            return;
        }

        final Bitmap bitmap = ImageCache.getInstance().getImage(url);

        if(bitmap == null) {
            loadRemoteImage(url, listener);
        }
        else {
            listener.onSuccess(bitmap);
        }
    }

    private void loadRemoteImage(final String url,
                                 final AdImageLoaderListener listener) {
        if(url == null || listener == null) {
            return;
        }

        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if (null == ImageCache.getInstance().getImage(url)) {
                            ImageCache.getInstance().putImage(url, bitmap);
                        }

                        listener.onSuccess(bitmap);
                    }

                }, 0, 0, Bitmap.Config.ARGB_8888,

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(LOGTAG, "Problem loading image URL: " + url);
                        AnomalyTrackerFactory.registerAnomaly("",
                                url,
                                "AD_IMAGE_REQUEST_FAILED",
                                error.getMessage());
                        listener.onFailure();
                    }
                }
        );

        HttpRequestManager.getQueue().add(imageRequest);
    }
}
