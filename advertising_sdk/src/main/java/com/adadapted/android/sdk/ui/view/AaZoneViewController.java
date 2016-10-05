package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;
import com.adadapted.android.sdk.ext.management.SessionManager;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 7/1/15
 */
class AaZoneViewController
        implements SessionManager.Callback,
        AdZoneRefreshScheduler.Listener,
        AdViewBuilder.Listener {
    private static final String LOGTAG = AaZoneViewController.class.getName();

    private final Context mContext;
    private final AaZoneViewProperties mZoneProperties;

    private final AdViewBuilder mAdViewBuilder;
    private final AdActionHandler mAdActionHandler;

    private AaZoneViewControllerListener mListener;

    private Session mSession;
    private Zone mZone;
    private ViewAdWrapper mCurrentAd;
    private final Set<String> mTimerRunning;

    public AaZoneViewController(final Context context, final AaZoneViewProperties zoneProperties) {
        mContext = context;
        mZoneProperties = zoneProperties;

        mAdViewBuilder = new AdViewBuilder(context);
        mAdViewBuilder.setListener(this);

        mAdActionHandler = new AdActionHandler(context);

        String zoneId = null;
        if(zoneProperties != null) {
            zoneId = zoneProperties.getZoneId();
        }

        mZone = Zone.createEmptyZone(zoneId);
        mCurrentAd = ViewAdWrapper.createEmptyCurrentAd(mSession);
        mTimerRunning = new HashSet<>();

        Map<String, String> params = new HashMap<>();
        params.put("zone_id", zoneId);
        AppEventTrackingManager.registerEvent(AppEventSource.SDK, "zone_loaded", params);
    }

    private void setNextAd() {
        completeCurrentAd();

        Ad ad = mZone.getNextAd();
        if(ad != null) {
            mCurrentAd = new ViewAdWrapper(mSession, ad);
        }
        else {
            mCurrentAd = ViewAdWrapper.createEmptyCurrentAd(mSession);
        }

        displayAd();
    }

    private void completeCurrentAd() {
        if(mCurrentAd.hasAd()) {
            mCurrentAd.completeAdTracking();
        }

        notifyResetDisplayView();
    }

    private void displayAd() {
        if(mCurrentAd.hasAd()) {
            mAdViewBuilder.buildView(mCurrentAd, mZoneProperties, getZoneWidth(), getZoneHeight());
        }
    }

    public void setTimer() {
        new AdZoneRefreshScheduler(this).schedule(mCurrentAd.getAd());
        mTimerRunning.add(mCurrentAd.getAdId());
    }

    public void acknowledgeDisplay() {
        if(!mTimerRunning.contains(mCurrentAd.getAdId())) {
            mCurrentAd.beginAdTracking();
            setTimer();
        }
    }

    public void handleAdAction() {
        if(mAdActionHandler.handleAction(mCurrentAd)){
            mCurrentAd.trackInteraction();

            if (mCurrentAd.isHiddenOnInteraction()) {
                setNextAd();
            }
        }
    }

    public int getZoneWidth() {
        Dimension dim = mZone.getDimension(getPresentOrientation());
        if(dim != null) {
            return dim.getWidth();
        }

        return -1;
    }

    public int getZoneHeight() {
        Dimension dim = mZone.getDimension(getPresentOrientation());
        if(dim != null) {
            return dim.getHeight();
        }

        return -2;
    }

    private String getPresentOrientation() {
        int orientation = mContext.getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return AdImage.LANDSCAPE;
        }

        return AdImage.PORTRAIT;
    }

    public void setListener(AaZoneViewControllerListener listener) {
        mListener = listener;
        SessionManager.getSession(this);
    }

    public void removeListener() {
        mCurrentAd.completeAdTracking();

        mListener = null;
        SessionManager.removeCallback(this);
    }

    private void notifyViewReadyForDisplay(final View v) {
        if(mListener != null) {
            mListener.onViewReadyForDisplay(v);
        }
    }

    private void notifyResetDisplayView() {
        if(mListener != null) {
            mListener.onResetDisplayView();
        }
    }

    @Override
    public void onSessionAvailable(Session session) {
        if(mZoneProperties == null) {
            return;
        }

        mSession = session;
        mZone = session.getZone(mZoneProperties.getZoneId());

        if(mTimerRunning.size() == 0) {
            setNextAd();
        }
        else {
            displayAd();
        }
    }

    @Override
    public void onNewAdsAvailable(final Session session) {
        if(mZoneProperties == null) {
            return;
        }

        mZone = session.getZone(mZoneProperties.getZoneId());
    }

    @Override
    public void onAdZoneRefreshTimer(Ad ad) {
        if(ad.getAdId().equals(mCurrentAd.getAdId())) {
            mTimerRunning.remove(ad.getAdId());

            completeCurrentAd();
            setNextAd();
        }
    }

    @Override
    public void onViewLoaded(View v) {
        notifyViewReadyForDisplay(v);
    }

    @Override
    public void onViewLoadFailed() {
        mCurrentAd.markAdAsHidden();
        setNextAd();
    }
}
