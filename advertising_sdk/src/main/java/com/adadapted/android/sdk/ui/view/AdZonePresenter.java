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
import com.adadapted.android.sdk.ui.messaging.AdContentPublisher;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisher;
import com.adadapted.android.sdk.ui.model.AdContent;
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
        void onNoAdAvailable();
    }

    private String zoneId;
    private final Context context;

    private Listener listener;

    private boolean zoneLoaded;
    private Zone currentZone;
    private final Lock zoneLock = new ReentrantLock();

    private final PixelWebView pixelWebView;

    private int viewCount;

    private Ad currentAd;
    private boolean adStarted;
    private boolean adCompleted;
    private final Lock adLock = new ReentrantLock();

    private boolean timerRunning;
    private final Lock timerLock = new ReentrantLock();
    private final Timer timer;

    AdZonePresenter(final Context context) {
        this.context = context.getApplicationContext();

        pixelWebView = new PixelWebView(context.getApplicationContext());

        this.zoneLoaded = false;
        this.currentZone = Zone.emptyZone();
        this.viewCount = (int) (Math.random()*10);

        timer = new Timer();
    }

    void init(final String zoneId) {
        if(this.zoneId == null) {
            this.zoneId = zoneId;

            final Map<String, String> params = new HashMap<>();
            params.put("zone_id", zoneId);
            AppEventClient.trackSdkEvent("zone_loaded", params);
        }
    }

    void stop() {
        this.onDetach();
        timer.cancel();

        zoneLock.lock();
        try {
            this.zoneId = null;
            this.zoneLoaded = false;
            this.currentZone = Zone.emptyZone();
        }
        finally {
            zoneLock.unlock();
        }
    }

    void onAttach(final Listener l) {
        if(l == null) {
            Log.e(LOGTAG, "NULL Listener provided");
            return;
        }

        this.listener = l;

        SessionClient.addPresenter(this);
        setNextAd();
    }

    void onDetach() {
        this.listener = null;

        completeCurrentAd();
        SessionClient.removePresenter(this);
    }

    private void updateCurrentZone(final Zone zone) {
        zoneLock.lock();
        try {
            this.zoneLoaded = true;
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
        if(!zoneLoaded || timerRunning) {
            return;
        }

        completeCurrentAd();

        adLock.lock();
        try {
            if(currentZone.hasAds()) {
                final int idx = viewCount % currentZone.getAds().size();
                viewCount++;

                currentAd = currentZone.getAds().get(idx);
                adStarted = false;
                adCompleted = false;
            }
            else {
                currentAd = Ad.emptyAd();
            }
        }
        finally {
            adLock.unlock();
        }

        displayAd();
    }

    private void displayAd() {
        if(currentAd.isEmpty()) {
            notifyNoAdAvailable();
        }
        else {
            notifyAdAvailable(currentAd);
        }
    }

    private void completeCurrentAd() {
        if((currentAd != null && !currentAd.isEmpty()) && (adStarted && !adCompleted)) {
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

    void onAdDisplayed(final Ad ad) {
        adLock.lock();
        try {
            adStarted = true;
            AdEventClient.trackImpression(ad);
            pixelWebView.loadData(ad.getTrackingHtml(), "text/html", null);

            startZoneTimer();
        }
        finally {
            adLock.unlock();
        }
    }

    void onAdDisplayFailed(final Ad ad) {
        adLock.lock();
        try {
            adStarted = true;
            currentAd = Ad.emptyAd();

            startZoneTimer();
        }
        finally {
            adLock.unlock();
        }
    }

    void onBlankDisplayed() {
        adLock.lock();
        try {
            adStarted = true;
            currentAd = Ad.emptyAd();

            startZoneTimer();
        }
        finally {
            adLock.unlock();
        }
    }

    private void startZoneTimer() {
        if(!zoneLoaded || timerRunning) {
            return;
        }

        timerLock.lock();
        try {
            timerRunning = true;
            timer.schedule(new TimerTask(){
                @Override
                public void run() {
                    timerLock.lock();
                    try {
                        timerRunning = false;
                    }
                    finally {
                        timerLock.unlock();
                    }

                    setNextAd();
                }
            }, currentAd.getRefreshTime() * 1000);
        }
        finally {
            timerLock.unlock();
        }
    }

    void onAdClicked(final Ad ad) {
        final String actionType = ad.getActionType();
        switch(actionType) {
            case Ad.ActionTypes.CONTENT:
                final Map<String, String> params = new HashMap<>();
                params.put("ad_id", ad.getId());
                AppEventClient.trackSdkEvent("atl_ad_clicked", params);

                handleContentAction(ad);
                break;

            case Ad.ActionTypes.LINK:
                AdEventClient.trackInteraction(ad);

                handleLinkAction(ad);
                break;

            case Ad.ActionTypes.POPUP:
                AdEventClient.trackInteraction(ad);

                handlePopupAction(ad);
                break;

            default:
                Log.w(LOGTAG, "Cannot handle Action type: " + actionType);
        }
    }

    private void handleContentAction(final Ad ad) {
        final String zoneId = ad.getZoneId();

        final AdContentPayload payload = AdContentPayload.createAddToListContent(ad);
        SdkContentPublisher.getInstance().publishContent(zoneId, payload);

        final AdContent content = AdContent.createAddToListContent(ad);
        AdContentPublisher.getInstance().publishContent(zoneId, content);
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
        if(listener != null) {
            listener.onZoneAvailable(currentZone);
        }
    }

    private void notifyAdAvailable(final Ad ad) {
        if(listener != null) {
            listener.onAdAvailable(ad);
        }
    }

    private void notifyNoAdAvailable() {
        if(listener != null) {
            listener.onNoAdAvailable();
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        updateCurrentZone(session.getZone(zoneId));
        notifyZoneAvailable();
    }

    @Override
    public void onAdsAvailable(final Session session) {
        updateCurrentZone(session.getZone(zoneId));
    }

    @Override
    public void onSessionInitFailed() {
        updateCurrentZone(Zone.emptyZone());
    }
}
