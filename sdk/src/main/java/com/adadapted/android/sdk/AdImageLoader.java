package com.adadapted.android.sdk;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/30/15.
 */
class AdImageLoader {
    private static final String TAG = AdImageLoader.class.getName();

    private final Set<Listener> listeners;

    static interface Listener {
        public void onAdImageLoaded(Bitmap bitmap);
    }

    AdImageLoader() {
        listeners = new HashSet<>();
    }

    void getImage(String url) {
        if(url == null) {
            Log.w(TAG, "No URL has been provided.");
            return;
        }

        Log.d(TAG, "Grabbing image: " + url);

        Bitmap bitmap = ImageCache.getInstance().getImage(url);

        if(bitmap == null)
        {
            Log.d(TAG, "Image Cache Miss.");
            loadRemoteImage(url);
        }
        else
        {
            Log.d(TAG, "Image Cache Hit.");
            notifyAdImageLoaded(bitmap);
        }
    }

    void loadRemoteImage(final String url) {
        ImageRequest imageRequest = new ImageRequest(url,
            new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap response) {
                    if(null == ImageCache.getInstance().getImage(url)) {
                        Log.d(TAG, "Loaded image " + url);
                        ImageCache.getInstance().putImage(url, response);
                    }

                    notifyAdImageLoaded(response);
                }

            }, 0, 0, Bitmap.Config.ARGB_8888,

            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "Problem retrieving Ad Image.", error);
                }
            }
        );

        HttpRequestManager.getQueue().add(imageRequest);
    }

    void addListener(AdImageLoader.Listener listener) {
        listeners.add(listener);
    }

    void removeListener(AdImageLoader.Listener listener) {
        listeners.remove(listener);
    }

    private void notifyAdImageLoaded(Bitmap bitmap) {
        if(bitmap != null) {
            for(AdImageLoader.Listener listener : listeners) {
                listener.onAdImageLoaded(bitmap);
            }
        }
        else {
            Log.w(TAG, "No Bitmap to return.");
        }
    }
}
