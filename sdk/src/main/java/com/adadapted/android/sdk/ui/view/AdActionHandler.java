package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity;
import com.adadapted.android.sdk.ui.model.ViewAd;

/**
 * Created by chrisweeden on 7/1/15.
 */
class AdActionHandler {
    private static final String TAG = AdActionHandler.class.getName();

    private final Context context;

    public AdActionHandler(Context context) {
        this.context = context;
    }

    public void handleAction(ViewAd ad) {
        if(!ad.hasAd()) { return; }

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
                break;

            case AdAction.POPUP:
                handlePopupAction(ad);
                break;
        }
    }

    private void handleContentAction(ViewAd ad) {
        ContentPayload payload = ContentPayload.createAddToListContent(ad);
        AdAdapted.getInstance().publishContent(ad.getAd().getZoneId(), payload);
    }

    private void handleDelegateAction(ViewAd ad) {

    }

    private void handleLinkAction(ViewAd ad) {

    }

    private void handlePopupAction(ViewAd ad) {
        Intent intent = AaWebViewPopupActivity.createActivity(context, ad);
        context.startActivity(intent);
    }
}
