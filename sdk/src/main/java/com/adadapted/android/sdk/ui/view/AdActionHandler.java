package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.ui.model.ViewAd;

/**
 * Created by chrisweeden on 7/1/15.
 */
public class AdActionHandler {
    private static final String TAG = AdActionHandler.class.getName();

    private final Context context;

    public AdActionHandler(Context context) {
        this.context = context;
    }

    public void handleAction(ViewAd ad) {
        switch(ad.getAd().getAdAction().getActionType()) {
            case AdAction.CONTENT:
                Log.d(TAG, "Handling CONTENT Ad Action");
                handleContentAction(ad);
                break;

            case AdAction.DELEGATE:
                Log.d(TAG, "Handling DELEGATE Ad Action");
                break;

            case AdAction.NULLACTION:
                Log.d(TAG, "Handling NULL Ad Action");
                break;

            case AdAction.POPUP:
                Log.d(TAG, "Handling POPUP Ad Action");
                handlePopupAction(ad);
                break;
        }
    }

    private void handleContentAction(ViewAd ad) {
        ContentPayload payload = ContentPayload.createAddToListContent(ad);
        AdAdapted.getInstance().publishContent(ad.getAd().getZoneId(), payload);
    }

    private void handlePopupAction(ViewAd ad) {
        Intent intent = WebViewPopupActivity.createActivity(context, ad);
        context.startActivity(intent);
    }
}
