package com.adadapted.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.R;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;
import com.adadapted.android.sdk.ui.listener.AdViewListener;
import com.adadapted.android.sdk.ui.model.CurrentAd;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AAZoneView extends RelativeLayout
        implements AdAdapted.Listener, AdZoneRefreshScheduler.Listener, AdViewListener {
    private static final String TAG = AAZoneView.class.getName();

    private String zoneLabel;

    private boolean isInitialized = false;
    private boolean isActive = false;
    private boolean isVisible = true;

    private String zoneId;
    private String sessionId;
    private int adCountForZone;

    private CurrentAd currentAd;

    private AdZoneRefreshScheduler refreshScheduler;

    private AAImageAdView adImageView;
    private AAHtmlAdView adWebView;

    private int layoutResourceId;
    private AAJsonAdView aaJsonView;

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

    public boolean zoneHasNoAds() {
        return adCountForZone == 0;
    }

    public void init(String zoneId) {
        this.zoneId = zoneId;

        this.layoutResourceId = R.layout.default_json_ad_zone;
        this.currentAd = CurrentAd.createEmptyCurrentAd(getContext(), sessionId);

        setGravity(Gravity.CENTER);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAdInteraction();
            }
        });

        isInitialized = true;
    }

    public void init(String zoneId, int layoutResourceId) {
        init(zoneId);

        this.layoutResourceId = layoutResourceId;
    }

    private void processAdInteraction() {
        if (zoneHasNoAds() || !currentAd.hasAd()) {
            return;
        }

        currentAd.trackInteraction();

        Intent intent = new Intent(getContext(), WebViewPopupActivity.class);
        intent.putExtra(WebViewPopupActivity.EXTRA_POPUP_URL, currentAd.getActionPath());
        getContext().startActivity(intent);
    }

    private void displayNextAd() {
        if(zoneHasNoAds()) {
            Log.i(TAG, getZoneLabel() + " No ads for zone " + zoneId);
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
        Ad ad = AdAdapted.getInstance().getNextAdForZone(zoneId);
        currentAd = new CurrentAd(getContext(), sessionId, ad);

        if(!currentAd.hasAd()) {
            return;
        }

        loadNextAdAssets();
        scheduleAd();
    }

    private void loadNextAdAssets() {
        switch(currentAd.getAdType()) {
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
        if(adWebView == null) {
            adWebView = new AAHtmlAdView(getContext());
            adWebView.addListener(this);
        }

        adWebView.loadHtml(currentAd.getAd());
        displayAdView(adWebView);
    }

    private void loadImage() {
        if(adImageView == null) {
            adImageView = new AAImageAdView(getContext());
            adImageView.addListener(this);
        }

        adImageView.loadImage(currentAd.getAd());
        displayAdView(adImageView);
    }

    private void loadJson() {
        if(aaJsonView == null) {
            aaJsonView = new AAJsonAdView(getContext(), layoutResourceId);
            aaJsonView.addListener(this);
        }

        aaJsonView.buildView(currentAd.getAd());
        displayAdView(aaJsonView);
    }

    private void displayAdView(View view) {
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
        refreshScheduler.schedule(currentAd.getAd());
    }

    private void completeCurrentAd() {
        if(currentAd != null && currentAd.hasAd()) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    AAZoneView.this.removeAllViews();
                }
            });

            currentAd.completeAdTracking();
            currentAd = CurrentAd.createEmptyCurrentAd(getContext(), sessionId);
        }
    }

    public void purgeScheduler() {
        if(refreshScheduler != null) {
            refreshScheduler.removeListener(this);
            refreshScheduler.cancel();
            refreshScheduler.purge();
        }
    }

    public void onStart() {
        if(!isInitialized) { return; }

        if(!currentAd.hasAd()) {
            currentAd.trackPopupEnd();
        }

        isActive = true;
        AdAdapted.getInstance().addListener(this);
    }

    public void onStop() {
        isActive = false;
        AdAdapted.getInstance().removeListener(this);

        if(!isInitialized || zoneHasNoAds()) {
            return;
        }

        purgeScheduler();

        if(!currentAd.isStoppingForPopup()) {
            completeCurrentAd();
        }

        currentAd.flush();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if(currentAd.isStoppingForPopup()) {
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
        this.sessionId = session.getSessionId();

        Zone zone = session.getZone(zoneId);
        this.adCountForZone = (zone != null) ? zone.getAdCount() : 0;

        displayNextAd();
    }

    @Override
    public void onSessionAdsReloaded(Session session) {
        Zone zone = session.getZone(zoneId);
        this.adCountForZone = (zone != null) ? zone.getAdCount() : 0;

        if(!currentAd.hasAd()) {
            displayNextAd();
        }
    }

    @Override
    public void onAdZoneRefreshTimer() {
        if(isActive) {
            displayNextAd();
        }
    }

    @Override
    public void onViewLoaded() {
        currentAd.beginAdTracking();
    }
}
