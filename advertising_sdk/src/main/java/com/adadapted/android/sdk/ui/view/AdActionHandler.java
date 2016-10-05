package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisherFactory;
import com.adadapted.android.sdk.ui.model.AdContentPayload;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

/**
 * Created by chrisweeden on 7/1/15
 */
class AdActionHandler {
    private static final String LOGTAG = AdActionHandler.class.getName();

    private final Context mContext;

    public AdActionHandler(final Context context) {
        mContext = context;
    }

    /**
     *
     * @param ad The Ad to handle the action for
     * @return Whether the Ad Interaction should be tracked or not.
     */
    public boolean handleAction(final ViewAdWrapper ad) {
        if(ad == null || !ad.hasAd()) { return false; }

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
                Log.w(LOGTAG, "Cannot handle Action type: " + ad.getAd().getAdAction().getActionType());
                result = false;
                break;

            case AdAction.POPUP:
                handlePopupAction(ad);
                break;

            default:
                Log.w(LOGTAG, "Cannot handle Action type: " + ad.getAd().getAdAction().getActionType());
                result = false;
        }

        return result;
    }

    private void handleContentAction(final ViewAdWrapper ad) {
        String zoneId = ad.getAd().getZoneId();

        AdContentPayload payload = AdContentPayload.createAddToListContent(ad);
        SdkContentPublisherFactory.getContentPublisher().publishContent(zoneId, payload);
    }

    private void handleDelegateAction(final ViewAdWrapper ad) {}

    private void handleLinkAction(final ViewAdWrapper ad) {}

    private void handlePopupAction(final ViewAdWrapper ad) {
        Intent intent = AaWebViewPopupActivity.createActivity(mContext, ad);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
