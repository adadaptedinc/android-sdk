package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 7/1/15.
 */
class AaZoneViewController implements SessionManager.Listener, AdZoneRefreshScheduler.Listener,
        AdViewBuilder.Listener {
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
    private ViewAdWrapper currentAd;
    private Set<String> timerRunning;

    public AaZoneViewController(final Context context, final String zoneId, final int resourceId) {
        this.context = context;
        this.zoneId = zoneId;
        this.resourceId = resourceId;

        adViewBuilder = new AdViewBuilder(context);
        adViewBuilder.setListener(this);

        adActionHandler = new AdActionHandler(context);

        this.zone = Zone.createEmptyZone(zoneId);
        this.currentAd = ViewAdWrapper.createEmptyCurrentAd(context, sessionId);
        this.timerRunning = new HashSet<>();
    }

    private void setNextAd() {
        completeCurrentAd();

        Ad ad = zone.getNextAd();
        if(ad != null) {
            currentAd = new ViewAdWrapper(context, sessionId, ad);
        }
        else {
            currentAd = ViewAdWrapper.createEmptyCurrentAd(context, sessionId);
        }

        displayAd();
    }

    private void completeCurrentAd() {
        if(currentAd.hasAd()) {
            currentAd.completeAdTracking();
        }

        notifyResetDisplayView();
    }

    private void displayAd() {
        if(currentAd.hasAd()) {
            adViewBuilder.buildView(currentAd, resourceId, getZoneWidth(), getZoneHeight());
        }
    }

    public void setTimer() {
        new AdZoneRefreshScheduler(this).schedule(currentAd.getAd());
        timerRunning.add(currentAd.getAdId());
    }

    public void acknowledgeDisplay() {
        if(!timerRunning.contains(currentAd.getAdId())) {
            currentAd.beginAdTracking();
            setTimer();
        }
    }

    public void handleAdAction() {
        currentAd.trackInteraction();
        adActionHandler.handleAction(currentAd);

        if(currentAd.isHiddenOnInteraction()) {
            setNextAd();
        }
    }

    public int getZoneWidth() {
        Dimension dim = zone.getDimension(getPresentOrientation());
        if(dim != null) {
            return dim.getWidth();
        }

        return -2;
    }

    public int getZoneHeight() {
        Dimension dim = zone.getDimension(getPresentOrientation());
        if(dim != null) {
            return dim.getHeight();
        }

        return -2;
    }

    private String getPresentOrientation() {
        int orientation = context.getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return AdImage.LANDSCAPE;
        }

        return AdImage.PORTRAIT;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        SessionManagerFactory.getInstance(context).createSessionManager().addListener(this);

        currentAd.beginAdTracking();
    }

    public void removeListener() {
        currentAd.completeAdTracking();

        this.listener = null;
        SessionManagerFactory.getInstance(context).createSessionManager().removeListener(this);
    }

    private void notifyViewReadyForDisplay(final View v) {
        if(listener != null) {
            listener.onViewReadyForDisplay(v);
        }
    }

    private void notifyResetDisplayView() {
        if(listener != null) {
            listener.onResetDisplayView();
        }
    }

    @Override
    public void onSessionInitialized(final Session session) {
        sessionId = session.getSessionId();
        zone = session.getZone(zoneId);

        if(timerRunning.size() == 0) {
            setNextAd();
        }
        else {
            displayAd();
        }
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onSessionNotReinitialized() {}

    @Override
    public void onNewAdsAvailable(final Session session) {
        zone = session.getZone(zoneId);
    }

    @Override
    public void onAdZoneRefreshTimer(Ad ad) {
        if(ad.getAdId().equals(currentAd.getAdId())) {
            timerRunning.remove(ad.getAdId());

            completeCurrentAd();
            setNextAd();
        }
    }

    @Override
    public void onViewLoaded(View v) {
        notifyViewReadyForDisplay(v);
    }
}
