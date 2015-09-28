package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.session.SessionListener;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;
import com.adadapted.android.sdk.ui.model.ViewAdWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 7/1/15
 */
class AaZoneViewController implements SessionListener, AdZoneRefreshScheduler.Listener,
        AdViewBuilder.Listener {
    private static final String LOGTAG = AaZoneViewController.class.getName();

    private final Context mContext;
    private final String mZoneId;
    private final int mResourceId;

    private final AdViewBuilder mAdViewBuilder;
    private final AdActionHandler mAdActionHandler;

    private AaZoneViewControllerListener mListener;

    private Session mSession;
    private Zone mZone;
    private ViewAdWrapper mCurrentAd;
    private Set<String> mTimerRunning;

    public AaZoneViewController(final Context context, final String zoneId, final int resourceId) {
        mContext = context;
        mZoneId = zoneId;
        mResourceId = resourceId;

        mAdViewBuilder = new AdViewBuilder(context);
        mAdViewBuilder.setListener(this);

        mAdActionHandler = new AdActionHandler(context);

        mZone = Zone.createEmptyZone(zoneId);
        mCurrentAd = ViewAdWrapper.createEmptyCurrentAd(mSession);
        mTimerRunning = new HashSet<>();
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
            mAdViewBuilder.buildView(mCurrentAd, mResourceId, getZoneWidth(), getZoneHeight());
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
        SessionManagerFactory.addListener(this);

        mCurrentAd.beginAdTracking();
    }

    public void removeListener() {
        mCurrentAd.completeAdTracking();

        mListener = null;
        SessionManagerFactory.removeListener(this);
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
    public void onSessionInitialized(final Session session) {
        mSession = session;
        mZone = session.getZone(mZoneId);

        if(mTimerRunning.size() == 0) {
            setNextAd();
        }
        else {
            displayAd();
        }
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onNewAdsAvailable(final Session session) {
        mZone = session.getZone(mZoneId);
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
