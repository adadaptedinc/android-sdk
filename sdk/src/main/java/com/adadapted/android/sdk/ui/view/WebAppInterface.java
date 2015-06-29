package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by chrisweeden on 6/29/15.
 */
public class WebAppInterface {
    private static final String TAG = WebAppInterface.class.getName();

    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void favoriteRecipe(String recipeId) {
        Log.d(TAG, "Attempting to favorite recipe: " + recipeId);
    }
}
