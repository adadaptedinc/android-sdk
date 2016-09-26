package com.adadapted.android.sdk.ui.activity;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by chrisweeden on 6/29/15.
 */
class WebAppInterface {
    private static final String TAG = WebAppInterface.class.getName();

    final Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(final Context context) {
        mContext = context;
    }

    public void registerImpression(final String zoneId, final String adId) {
        Log.d(TAG, String.format("Attempting to register Impression for Zone: %s, Ad: %s", zoneId, adId));
    }

    @JavascriptInterface
    public void favoriteRecipe(final String recipeId) {
        Log.d(TAG, "Attempting to favorite recipe: " + recipeId);
    }
}
