package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisher;
import com.adadapted.android.sdk.ui.model.AdContentPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class AdZonePresenter implements SessionClient.Listener {
    private static final String LOGTAG = AdZonePresenter.class.getName();

    interface Listener {
        void onZoneAvailable(Zone zone);
        void onAdAvailable(Ad ad);
    }

    private String zoneId;
    private final Context context;

    private Listener listener;

    private Zone currentZone;
    private final Lock zoneLock = new ReentrantLock();

    private int viewCount;

    private Ad currentAd;
    private boolean adStarted;
    private boolean adCompleted;
    private final Lock adLock = new ReentrantLock();

    AdZonePresenter(final Context context) {
        this.context = context.getApplicationContext();

        this.currentZone = Zone.emptyZone();
        this.viewCount = 0;
    }

    void init(final String zoneId) {
        this.zoneId = zoneId;

        Map<String, String> params = new HashMap<>();
        params.put("zone_id", zoneId);
        AppEventClient.trackSdkEvent("zone_loaded", params);
    }

    void onAttach(final Listener l) {
        Log.d(LOGTAG, "onAttach called");

        if(listener != null) {
            Log.w(LOGTAG, "View already attached");
            return;
        }

        if(l == null) {
            Log.e(LOGTAG, "NULL Listener provided");
            return;
        }

        this.listener = l;

        SessionClient.addListener(this);
    }

    void onDetach() {
        Log.d(LOGTAG, "onDetach called");

        if(listener == null) {
            Log.w(LOGTAG, "View already detached");
            return;
        }

        listener = null;

        completeCurrentAd();
        SessionClient.removeListener(this);
    }

    private void updateCurrentZone(final Zone zone) {
        Log.d(LOGTAG, "updateCurrentZone called");

        zoneLock.lock();
        try {
            this.currentZone = zone;
        }
        finally {
            zoneLock.unlock();
        }

        if(currentAd == null) {
            setNextAd();
        }
    }

    private void setNextAd() {
        Log.d(LOGTAG, "setNextAd called");

        completeCurrentAd();

        adLock.lock();
        try {
            if(!currentZone.isEmpty() && currentZone.hasAds()) {
                final int idx = viewCount % currentZone.getAds().size();
                viewCount++;

                currentAd = currentZone.getAds().get(idx);
                adStarted = false;
                adCompleted = false;
            }
        }
        finally {
            adLock.unlock();
        }

        displayAd();
    }

    private void displayAd() {
        Log.d(LOGTAG, "displayAd called");

        if(currentZone.isNotEmpty()) {
            notifyAdAvailable(currentAd);
        }
    }

    private void completeCurrentAd() {
        Log.d(LOGTAG, "completeCurrentAd called");

        if(currentAd != null && (adStarted && !adCompleted)) {
            adLock.lock();
            try {
                adCompleted = true;
                AdEventClient.trackImpressionEnd(currentAd);
            }
            finally {
                adLock.unlock();
            }
        }
    }

    void onAdDisplayed() {
        Log.d(LOGTAG, "onAdDisplayed called");

        adLock.lock();
        try {
            adStarted = true;
            AdEventClient.trackImpression(currentAd);
        }
        finally {
            adLock.unlock();
        }

        new Timer().schedule(new TimerTask(){
            @Override
            public void run() {
                Log.i(LOGTAG, "Refreshing Ad");

                setNextAd();
            }
        }, currentAd.getRefreshTime() * 1000);
    }

    void onAdClicked() {
        Log.d(LOGTAG, "onAdClicked called");

        if(handleAction(currentAd)) {
            AdEventClient.trackInteraction(currentAd);
        }
    }

    /**
     *
     * @param ad The Ad to handle the action for
     * @return Whether the Ad Interaction should be tracked or not.
     */
    private boolean handleAction(final Ad ad) {
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

    private void notifyZoneAvailable() {
        Log.d(LOGTAG, "notifyZoneAvailable called");

        if(listener != null) {
            listener.onZoneAvailable(currentZone);
        }
    }

    private void notifyAdAvailable(final Ad ad) {
        Log.d(LOGTAG, "notifyAdAvailable called");

        if(listener != null) {
            listener.onAdAvailable(ad);
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        Log.d(LOGTAG, "onSessionAvailable called");

        updateCurrentZone(session.getZone(zoneId));
        notifyZoneAvailable();
    }

    @Override
    public void onAdsAvailable(final Session session) {
        Log.d(LOGTAG, "onAdsAvailable called");

        updateCurrentZone(session.getZone(zoneId));
    }

    @Override
    public void onSessionInitFailed() {
        Log.d(LOGTAG, "onSessionInitFailed called");

        updateCurrentZone(Zone.emptyZone());
    }
}
