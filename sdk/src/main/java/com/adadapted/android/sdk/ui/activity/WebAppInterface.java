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
    WebAppInterface(Context c) {
        mContext = c;
    }

    public void registerImpression(String zoneId, String adId) {

    }

    @JavascriptInterface
    public void favoriteRecipe(String recipeId) {
        Log.d(TAG, "Attempting to favorite recipe: " + recipeId);
    }
}
