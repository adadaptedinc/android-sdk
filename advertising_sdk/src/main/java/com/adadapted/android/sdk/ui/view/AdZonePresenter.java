package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdActionType;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.ui.activity.AaWebViewPopupActivity;
import com.adadapted.android.sdk.ui.messaging.AdContentPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class AdZonePresenter extends SessionListener {
    private static final String LOGTAG = AdZonePresenter.class.getName();

    interface Listener {
        void onZoneAvailable(Zone zone);
        void onAdsRefreshed(Zone zone);
        void onAdAvailable(Ad ad);
        void onNoAdAvailable();
    }

    private String zoneId;
    private final Context context;

    private Listener listener;

    private boolean attached;

    private final Lock zoneLock = new ReentrantLock();
    private String sessionId;
    private boolean zoneLoaded;
    private Zone currentZone;

    private final PixelWebView pixelWebView;

    private int viewCount;

    private Ad currentAd;
    private boolean adStarted;
    private boolean adCompleted;

    private boolean timerRunning;
    private final Lock timerLock = new ReentrantLock();
    private final Timer timer;
    private final AdEventClient adEventClient;
    private final AppEventClient appEventClient;

    AdZonePresenter(final Context context) {
        this.context = context.getApplicationContext();
        adEventClient = AdEventClient.Companion.getInstance();
        appEventClient = AppEventClient.Companion.getInstance();
        pixelWebView = new PixelWebView(context.getApplicationContext());

        attached = false;

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
            appEventClient.trackSdkEvent("zone_loaded", params);
        }
    }

    void stop() {
        this.onDetach();

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

        zoneLock.lock();
        try {
            if (!attached) {
                attached = true;

                this.listener = l;

                SessionClient.Companion.getInstance().addPresenter(this);
            }

            setNextAd();
        }
        finally {
            zoneLock.unlock();
        }
    }

    void onDetach() {
        zoneLock.lock();
        try {
            if(attached) {
                attached = false;

                this.listener = null;

                completeCurrentAd();
                SessionClient.Companion.getInstance().removePresenter(this);
            }
        }
        finally {
            zoneLock.unlock();
        }
    }

    private void setNextAd() {
        if(!zoneLoaded || timerRunning) {
            return;
        }

        completeCurrentAd();

        zoneLock.lock();
        try {
            if(listener != null && currentZone.hasAds()) {
                final int idx = viewCount % currentZone.getAds().size();
                viewCount++;

                currentAd = currentZone.getAds().get(idx);
            }
            else {
                currentAd = new Ad();
            }

            adStarted = false;
            adCompleted = false;
        }
        finally {
            zoneLock.unlock();
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
            zoneLock.lock();
            try {
                adCompleted = true;
                AdEventClient.Companion.getInstance().trackImpressionEnd(currentAd);
            }
            finally {
                zoneLock.unlock();
            }
        }
    }

    void onAdDisplayed(final Ad ad) {
        zoneLock.lock();
        try {
            adStarted = true;
            AdEventClient.Companion.getInstance().trackImpression(ad);
            pixelWebView.loadData(ad.getTrackingHtml(), "text/html", null);

            startZoneTimer();
        }
        finally {
            zoneLock.unlock();
        }
    }

    void onAdDisplayFailed(final Ad ad) {
        zoneLock.lock();
        try {
            adStarted = true;
            currentAd = new Ad();

            startZoneTimer();
        }
        finally {
            zoneLock.unlock();
        }
    }

    void onBlankDisplayed() {
        zoneLock.lock();
        try {
            adStarted = true;
            currentAd = new Ad();

            startZoneTimer();
        }
        finally {
            zoneLock.unlock();
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

        final Map<String, String> params = new HashMap<>();
        params.put("ad_id", ad.getId());

        switch(actionType) {
            case AdActionType.CONTENT:
                appEventClient.trackSdkEvent("atl_ad_clicked", params);
                handleContentAction(ad);
                break;

            case AdActionType.LINK:
            case AdActionType.EXTERNAL_LINK:
                adEventClient.trackInteraction(ad);
                handleLinkAction(ad);
                break;

            case AdActionType.POPUP:
                adEventClient.trackInteraction(ad);
                handlePopupAction(ad);
                break;

            case AdActionType.CONTENT_POPUP:
                appEventClient.trackSdkEvent("popup_ad_clicked", params);
                handlePopupAction(ad);
                break;

            default:
                Log.w(LOGTAG, "Cannot handle Action type: " + actionType);
        }
    }

    private void handleContentAction(final Ad ad) {
        final String zoneId = ad.getZoneId();
        AdContentPublisher.getInstance().publishContent(zoneId, ad.getContent());
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

    private void notifyAdsRefreshed() {
        if(listener != null) {
            listener.onAdsRefreshed(currentZone);
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

    private boolean updateSessionId(final String sessionId) {
        zoneLock.lock();
        try {
            if(this.sessionId == null || !this.sessionId.equals(sessionId)) {
                this.sessionId = sessionId;
                return true;
            }
        }
        finally {
            zoneLock.unlock();
        }

        return false;
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

        if(currentAd == null || currentAd.isEmpty()) {
            setNextAd();
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        updateCurrentZone(session.getZone(zoneId));

        if(updateSessionId(session.getId())) {
            notifyZoneAvailable();
        }
    }

    @Override
    public void onAdsAvailable(final Session session) {
        updateCurrentZone(session.getZone(zoneId));
        notifyAdsRefreshed();
    }

    @Override
    public void onSessionInitFailed() {
        updateCurrentZone(Zone.emptyZone());
    }
}
