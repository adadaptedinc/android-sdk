package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisher;
import com.adadapted.android.sdk.ui.model.AdContentPayload;

class AdActionHandler {
    private static final String LOGTAG = AdActionHandler.class.getName();

    private final Context context;

    public AdActionHandler(final Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     *
     * @param ad The Ad to handle the action for
     * @return Whether the Ad Interaction should be tracked or not.
     */
    public boolean handleAction(final Ad ad) {
        if(ad == null) { return false; }

        boolean result = true;
        final String actionType = ad.getActionType();
        switch(actionType) {
            case Ad.ActionTypes.CONTENT:
                handleContentAction(ad);
                break;

            case Ad.ActionTypes.LINK:
                handleLinkAction(ad);
                break;

            case Ad.ActionTypes.POPUP:
                handlePopupAction(ad);
                break;

            default:
                Log.w(LOGTAG, "Cannot handle Action type: " + actionType);
                result = false;
        }

        return result;
    }

    private void handleContentAction(final Ad ad) {
        String zoneId = ad.getZoneId();

        AdContentPayload payload = AdContentPayload.createAddToListContent(ad);
        SdkContentPublisher.getInstance().publishContent(zoneId, payload);
    }

    private void handleLinkAction(final Ad ad) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(ad.getActionPath()));

        context.startActivity(intent);
    }

    private void handlePopupAction(final Ad ad) {
        final Intent intent = AaWebViewPopupActivity.createActivity(context, ad);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}
