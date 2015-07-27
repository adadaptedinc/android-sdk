package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity;
import com.adadapted.android.sdk.ui.model.ContentPayload;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

/**
 * Created by chrisweeden on 7/1/15.
 */
class AdActionHandler {
    private static final String TAG = AdActionHandler.class.getName();

    private final Context context;

    public AdActionHandler(Context context) {
        this.context = context;
    }

    /**
     *
     * @param ad
     * @return Whether the Ad Interaction should be tracked or not.
     */
    public boolean handleAction(ViewAdWrapper ad) {
        if(!ad.hasAd()) { return false; }

        boolean result = true;
        switch(ad.getAd().getAdAction().getActionType()) {
            case AdAction.CONTENT:
                handleContentAction(ad);
                break;

            case AdAction.DELEGATE:
                handleDelegateAction(ad);
                break;

            case AdAction.LINK:
                handleLinkAction(ad);
                break;

            case AdAction.NULLACTION:
                result = false;
                break;

            case AdAction.POPUP:
                handlePopupAction(ad);
                break;
        }

        return result;
    }

    private void handleContentAction(ViewAdWrapper ad) {
        ContentPayload payload = ContentPayload.createAddToListContent(ad);
        AdAdapted.getInstance().publishContent(ad.getAd().getZoneId(), payload);
    }

    private void handleDelegateAction(ViewAdWrapper ad) {

    }

    private void handleLinkAction(ViewAdWrapper ad) {

    }

    private void handlePopupAction(ViewAdWrapper ad) {
        Intent intent = AaWebViewPopupActivity.createActivity(context, ad);
        context.startActivity(intent);
    }
}
