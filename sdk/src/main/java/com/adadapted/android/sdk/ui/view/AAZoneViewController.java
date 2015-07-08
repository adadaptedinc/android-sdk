package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;
import com.adadapted.android.sdk.ui.model.ViewAd;

import java.util.Map;

/**
 * Created by chrisweeden on 7/1/15.
 */
class AaZoneViewController implements SessionManager.Listener, AdFetcher.Listener,
        AdZoneRefreshScheduler.Listener, AdViewBuilder.Listener {
    private static final String TAG = AaZoneViewController.class.getName();

    public interface Listener {
        void onViewReadyForDisplay(View v);
        void onResetDisplayView();
    }

    private final Context context;
    private final String zoneId;
    private final int resourceId;

    private final AdViewBuilder adViewBuilder;
    private final AdActionHandler adActionHandler;

    private Listener listener;

    private String sessionId = "";
    private Zone zone;
    private ViewAd currentAd;
    private boolean timerRunning = false;

    public AaZoneViewController(final Context context, final String zoneId, final int resourceId) {
        this.context = context;
        this.zoneId = zoneId;
        this.resourceId = resourceId;

        adViewBuilder = new AdViewBuilder(context);
        adViewBuilder.setListener(this);

        adActionHandler = new AdActionHandler(context);

        this.zone = Zone.createEmptyZone(zoneId);
        this.currentAd = ViewAd.createEmptyCurrentAd(context, sessionId);

        SessionManagerFactory.getInstance(context).createSessionManager().addListener(this);
    }

    private void setNextAd() {
        Log.d(TAG, zoneId + ": setNextAd() Called.");
        currentAd.flush();

        Ad ad = zone.getNextAd();
        if(ad != null) {
            currentAd = new ViewAd(context, sessionId, ad);
        }
        else {
            currentAd = ViewAd.createEmptyCurrentAd(context, sessionId);
        }

        displayAd();
    }

    private void completeCurrentAd() {
        Log.d(TAG, zoneId + ": completeAd() Called.");

        currentAd.completeAdTracking();
        notifyResetDisplayView();
    }

    private void displayAd() {
        Log.d(TAG, zoneId + ": displayAd() Called.");

        if(currentAd.hasAd()) {
            buildAdView();
        }
    }

    private void buildAdView() {
        Log.d(TAG, zoneId + ": buildAdView() Called.");

        adViewBuilder.buildView(currentAd, resourceId);
    }

    public void setTimer() {
        Log.d(TAG, zoneId + ": setTimer() Called.");

        new AdZoneRefreshScheduler(this).schedule(currentAd.getAd());
        timerRunning = true;
    }

    public void acknowledgeDisplay() {
        Log.d(TAG, zoneId + ": acknowledgeDisplay() Called.");

        if(!timerRunning) {
            currentAd.beginAdTracking();
            setTimer();
        }
    }

    public void handleAdAction() {
        Log.d(TAG, zoneId + ": handleAdAction() Called.");

        currentAd.trackInteraction();
        adActionHandler.handleAction(currentAd);

        if(currentAd.isHiddenOnInteraction()) {
            setNextAd();
        }
    }

    public void setListener(Listener listener) {
        Log.d(TAG, zoneId + ": setListener() Called.");

        this.listener = listener;
        displayAd();
    }

    public void removeListener(Listener listener) {
        Log.d(TAG, zoneId + ": removeListener() Called.");

        if(this.listener != null && this.listener.equals(listener)) {
            this.listener = null;
            notifyResetDisplayView();
        }
    }

    private void notifyViewReadyForDisplay(final View v) {
        Log.d(TAG, zoneId + ": notifyViewReadyForDisplay() Called.");

        if(listener != null) {
            listener.onViewReadyForDisplay(v);
        }
    }

    private void notifyResetDisplayView() {
        Log.d(TAG, zoneId + ": notifyResetDisplayView() Called.");

        if(listener != null) {
            listener.onResetDisplayView();
        }
    }

    @Override
    public void onSessionInitialized(final Session session) {
        Log.d(TAG, zoneId + ": onSessionInitialized() Called.");

        sessionId = session.getSessionId();
        zone = session.getZone(zoneId);

        if(!timerRunning) {
            setNextAd();
        }

        displayAd();
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onSessionNotReinitialized() {}

    @Override
    public void onAdsRefreshed(Map<String, Zone> zones) {
        //zone.setAds(zones.get(zoneId).getAds());
    }

    @Override
    public void onAdsNotRefreshed() {}

    @Override
    public void onAdZoneRefreshTimer() {
        Log.d(TAG, zoneId + ": onAdZoneRefreshTimer() Called.");

        timerRunning = false;

        completeCurrentAd();
        setNextAd();
    }

    @Override
    public void onViewLoaded(View v) {
        notifyViewReadyForDisplay(v);
    }
}
