package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
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

    private final WebView mTrackingWebView;

    private final AdViewBuilder mAdViewBuilder;
    private final AdActionHandler mAdActionHandler;
    private final AdZoneRefreshScheduler mAdZoneRefreshScheduler;

    private Listener mListener;

    private Session mSession;
    private Zone mZone;
    private ViewAdWrapper mCurrentAd;
    private final Set<String> mTimerRunning;

    public interface Listener {
        void onViewReadyForDisplay(View v);
        void onResetDisplayView();
    }

    AaZoneViewController(final Context context,
                         final AaZoneViewProperties zoneProperties) {
        mContext = context.getApplicationContext();
        mZoneProperties = zoneProperties;

        mTrackingWebView = new WebView(context.getApplicationContext());
        mTrackingWebView.setWebChromeClient(new WebChromeClient());
        mTrackingWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);

                final Map<String, String> params = new HashMap<>();
                params.put("error", error.toString());

                AppErrorTrackingManager.registerEvent(
                        "TRACKING_PIXEL_LOAD_ERROR",
                        "Problem loading tracking pixel for Ad: " + mCurrentAd.getAdId(),
                        params);
            }
        });

        mAdViewBuilder = new AdViewBuilder(context.getApplicationContext());
        mAdViewBuilder.setListener(this);

        mAdActionHandler = new AdActionHandler(context.getApplicationContext());
        mAdZoneRefreshScheduler = new AdZoneRefreshScheduler();

        String zoneId = null;
        if(zoneProperties != null) {
            zoneId = zoneProperties.getZoneId();
        }

        mZone = Zone.createEmptyZone(zoneId);
        mCurrentAd = ViewAdWrapper.createEmptyCurrentAd(mSession);
        mTimerRunning = new HashSet<>();

        final Map<String, String> params = new HashMap<>();
        params.put("zone_id", zoneId);
        AppEventTrackingManager.registerEvent(AppEventSource.SDK, "zone_loaded", params);
    }

    private void setNextAd() {
        completeCurrentAd();

        final Ad ad = mZone.getNextAd();
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

    private void setTimer() {
        mAdZoneRefreshScheduler.schedule(mCurrentAd.getAd());
        mTimerRunning.add(mCurrentAd.getAdId());
    }

    void acknowledgeDisplay() {
        if(!mTimerRunning.contains(mCurrentAd.getAdId())) {
            mCurrentAd.beginAdTracking(mTrackingWebView);
            setTimer();
        }
    }

    void handleAdAction() {
        if(mAdActionHandler.handleAction(mCurrentAd)){
            mCurrentAd.trackInteraction();

            if (mCurrentAd.isHiddenOnInteraction()) {
                setNextAd();
            }
        }
    }

    private int getZoneWidth() {
        final Dimension dim = mZone.getDimension(getPresentOrientation());
        if(dim != null) {
            return dim.getWidth();
        }

        // Match Parent
        return -1;
    }

    private int getZoneHeight() {
        final Dimension dim = mZone.getDimension(getPresentOrientation());
        if(dim != null) {
            return dim.getHeight();
        }

        // Wrap Content
        return -2;
    }

    private String getPresentOrientation() {
        final Configuration configuration = mContext.getResources().getConfiguration();
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return AdImage.LANDSCAPE;
        }

        return AdImage.PORTRAIT;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
        SessionManager.getSession(this);
        mAdZoneRefreshScheduler.setListener(this);
    }

    public void removeListener() {
        mCurrentAd.completeAdTracking();

        mListener = null;
        SessionManager.removeCallback(this);
        mAdZoneRefreshScheduler.removeListener();
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
    public void onSessionAvailable(final Session session) {
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
    public void onAdZoneRefreshTimer(final Ad ad) {
        if(ad.getAdId().equals(mCurrentAd.getAdId())) {
            mTimerRunning.remove(ad.getAdId());

            completeCurrentAd();
            setNextAd();
        }
    }

    @Override
    public void onViewLoaded(final View v) {
        notifyViewReadyForDisplay(v);
    }

    @Override
    public void onViewLoadFailed() {
        mCurrentAd.markAdAsHidden();
        setNextAd();
    }
}
