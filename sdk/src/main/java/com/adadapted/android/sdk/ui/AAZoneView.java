package com.adadapted.android.sdk.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AAZoneView extends RelativeLayout
        implements AdAdapted.Listener, AdZoneRefreshScheduler.Listener, AdViewListener {
    private static final String TAG = AAZoneView.class.getName();

    private String zoneLabel;

    private boolean isActive;
    private boolean isVisible = true;
    private boolean isStoppingForPopup = false;

    private String zoneId;
    private String sessionId;
    private int adCountForZone;

    private Ad currentAd;

    private AdZoneRefreshScheduler refreshScheduler;

    private AAImageAdView adImageView;
    private AAHtmlAdView adWebView;

    private EventTracker eventTracker;

    public AAZoneView(Context context) {
        super(context);
    }

    public AAZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AAZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AAZoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private String getZoneLabel() {
        return zoneLabel;
    }

    public void setZoneLabel(String zoneLabel) {
        this.zoneLabel = zoneLabel;
    }

    public String getZoneId() {
        return zoneId;
    }

    public boolean zoneHasNoAds() {
        return adCountForZone == 0;
    }

    public void init(String zoneId) {
        Log.d(TAG, getZoneLabel() + " Calling init() with " + zoneId);

        this.zoneId = zoneId;

        adImageView = new AAImageAdView(getContext());
        adImageView.addListener(this);

        adWebView = new AAHtmlAdView(getContext());
        adWebView.addListener(this);

        eventTracker = AdAdapted.getInstance().getEventTracker();

        setGravity(Gravity.CENTER);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAdInteraction();
            }
        });
    }

    private void processAdInteraction() {
        if (zoneHasNoAds() || currentAd == null) {
            return;
        }

        isStoppingForPopup = true;

        Log.d(TAG, getZoneLabel() + " Ad " + currentAd.getAdId() + " clicked in Zone " + zoneId);
        eventTracker.trackInteractionEvent(sessionId, currentAd);
        eventTracker.trackPopupBeginEvent(sessionId, currentAd);

        Intent intent = new Intent(getContext(), WebViewPopupActivity.class);
        intent.putExtra(WebViewPopupActivity.EXTRA_POPUP_URL,
                currentAd.getAdAction().getActionPath());
        getContext().startActivity(intent);
    }

    private void displayNextAd() {
        Log.d(TAG, getZoneLabel() + " Calling displayNextAd()");

        if(zoneHasNoAds()) {
            Log.d(TAG, getZoneLabel() + " No ads for zone " + zoneId);
            return;
        }

        if(isVisible) {
            completeCurrentAd();
            setNextAd();
        }
        else {
            Log.i(TAG, getZoneLabel() + " is not Visible. Not displaying ad.");
        }
    }

    private void setNextAd() {
        currentAd = AdAdapted.getInstance().getNextAdForZone(zoneId);

        if(currentAd == null) {
            return;
        }

        loadNextAdAssets();
        scheduleAd();
    }

    private void loadNextAdAssets() {
        Log.d(TAG, getZoneLabel() + " Displaying " + currentAd.getAdId());

        switch(currentAd.getAdType().getAdType()) {
            case HTML:
                loadHtml();
                break;

            case IMAGE:
                loadImage();
                break;

            case JSON:
                loadJson();
                break;
        }
    }

    private void loadHtml() {
        adWebView.loadHtml(currentAd);
        displayAdView(adWebView);
    }

    private void loadImage() {
        adImageView.loadImage(currentAd);
        displayAdView(adImageView);
    }

    private void loadJson() {

    }

    private void displayAdView(View view) {
        Log.d(TAG, "Calling displayAdView()");

        final View updatedView = view;

        this.post(new Runnable() {
            @Override
            public void run() {
                AAZoneView.this.removeAllViews();
                AAZoneView.this.addView(updatedView);
            }
        });
    }

    private void scheduleAd() {
        refreshScheduler = new AdZoneRefreshScheduler();
        refreshScheduler.addListener(this);
        refreshScheduler.schedule(currentAd);
    }

    private void completeCurrentAd() {
        if(currentAd != null) {
            this.post(new Runnable() {
                @Override
                public void run() {
                  AAZoneView.this.removeAllViews();
                }
            });

            eventTracker.trackImpressionEndEvent(sessionId, currentAd);
            currentAd = null;
        }
    }

    public void onStart() {
        Log.d(TAG, getZoneLabel() + " Calling onStart()");

        if(isStoppingForPopup) {
            eventTracker.trackPopupEndEvent(sessionId, currentAd);
            completeCurrentAd();
            isStoppingForPopup = false;
        }

        isActive = true;
        AdAdapted.getInstance().addListener(this);
    }

    public void onStop() {
        Log.d(TAG, getZoneLabel() + " Calling onStop()");

        isActive = false;
        AdAdapted.getInstance().removeListener(this);

        if(zoneHasNoAds()) {
            return;
        }

        if(refreshScheduler != null) {
            refreshScheduler.removeListener(this);
            refreshScheduler.cancel();
            refreshScheduler.purge();
        }

        if(!isStoppingForPopup) {
            completeCurrentAd();
        }

        eventTracker.publishEvents();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if(isStoppingForPopup) {
            return;
        }

        switch(visibility) {
            case View.GONE:
            case View.INVISIBLE:
                Log.d(TAG, getZoneLabel() + " No Longer Visible");
                if(isVisible && isActive) {
                    isVisible = false;
                    onStop();
                }
                break;

            case View.VISIBLE:
                Log.d(TAG, getZoneLabel() + " Is Visible");
                if(!isVisible) {
                    isVisible = true;
                    onStart();
                }
                break;
        }
    }

    @Override
    public void onSessionLoaded(Session session) {
        Log.d(TAG, getZoneLabel() + " Calling onSessionLoaded()");

        this.sessionId = session.getSessionId();

        Zone zone = session.getZone(zoneId);
        this.adCountForZone = (zone != null) ? zone.getAdCount() : 0;

        displayNextAd();
    }

    @Override
    public void onSessionAdsReloaded(Session session) {
        Log.d(TAG, getZoneLabel() + " Calling onSessionAdsReloaded()");

        Zone zone = session.getZone(zoneId);
        this.adCountForZone = (zone != null) ? zone.getAdCount() : 0;

        if(currentAd == null) {
            displayNextAd();
        }
    }

    @Override
    public void onAdZoneRefreshTimer() {
        Log.d(TAG, getZoneLabel() + " Calling onAdZoneRefreshTimer()");

        if(isActive) {
            displayNextAd();
        }
    }

    @Override
    public void onViewLoaded() {
        eventTracker.trackImpressionBeginEvent(sessionId, currentAd);
    }
}
