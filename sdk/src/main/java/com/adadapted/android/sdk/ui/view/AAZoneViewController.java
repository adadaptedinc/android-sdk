package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.AdFetcher;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.core.ad.model.ContentAdAction;
import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.core.session.SessionManager;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.model.ManagedZone;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.factory.AdFetcherFactory;
import com.adadapted.android.sdk.ext.factory.SessionManagerFactory;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;
import com.adadapted.android.sdk.ui.listener.AdViewListener;
import com.adadapted.android.sdk.ui.model.ViewAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 6/2/15.
 */
public class AAZoneViewController implements SessionManager.Listener, AdFetcher.Listener,
        AdZoneRefreshScheduler.Listener, AdViewListener {
    private static final String TAG = AAZoneViewController.class.getName();

    public ManagedZone getZone() {
        return zone;
    }

    public interface Listener {
        void onViewReadyForDisplay(View view);
        void onResetDisplayView();
    }

    private Listener listener;

    private final Context context;
    private final String zoneId;
    private final int resourceId;

    private AdZoneRefreshScheduler refreshScheduler;

    private String sessionId;
    private ManagedZone zone;
    private ViewAd currentAd;

    private AAImageAdView adImageView;
    private AAHtmlAdView adWebView;
    private AAJsonAdView aaJsonView;

    private boolean active;
    private boolean visibility = true;
    private boolean stoppingForPopup = false;

    public AAZoneViewController(final Context context, final String zoneId, final int resourceId) {
        this.context = context;
        this.zoneId = zoneId;
        this.resourceId = resourceId;

        this.currentAd = ViewAd.createEmptyCurrentAd(context, sessionId);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVisible() {
        return visibility;
    }

    public boolean isNotVisible() {
        return !isVisible();
    }

    public boolean isStoppingForPopup() {
        return stoppingForPopup;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    private void displayNextAd() {
        if(zone == null || zone.hasNoAds()) {
            Log.i(TAG, "No ads for zone " + zoneId);
            return;
        }

        if(isVisible()) {
            completeCurrentAd();
            setNextAd();
        }
        else {
            Log.i(TAG, "Is not Visible. Not displaying ad.");
        }
    }

    public void processAdInteraction() {
        if (zone.hasNoAds() || !currentAd.hasAd()) {
            return;
        }

        currentAd.trackInteraction();
        AdAdapted.getInstance().publishAdClick(zoneId);

        if(currentAd.actionIs(AdAction.CONTENT)) {
            ContentAdAction action = (ContentAdAction)currentAd.getAd().getAdAction();
            try {
                JSONArray jsonArray = new JSONArray(action.getItems());
                ContentPayload payload = new ContentPayload(ContentPayload.ADD_TO_LIST,
                        new JSONObject().put("list-items", jsonArray));
                AdAdapted.getInstance().publishContent(zoneId, payload);
            }
            catch(JSONException ex) {
                Log.w(TAG, "Problem parsing JSON.");
            }

            Log.i(TAG, "Would handle CONTENT interaction here.");
        }
        else if(currentAd.actionIs(AdAction.DELEGATE)) {
            Log.i(TAG, "Would handle DELEGATE interaction here.");
        }
        else if(currentAd.actionIs(AdAction.POPUP)) {
            stoppingForPopup = true;

            Intent intent = new Intent(context, WebViewPopupActivity.class);
            intent.putExtra(WebViewPopupActivity.EXTRA_POPUP_AD, currentAd.getAd());
            context.startActivity(intent);
        }
    }

    private void setNextAd() {
        Ad ad = zone.getNextAd();
        currentAd = new ViewAd(context, sessionId, ad);

        if(!currentAd.hasAd()) {
            return;
        }

        loadNextAdAssets();
        scheduleAd();
    }

    private void loadNextAdAssets() {
        switch(currentAd.getAdType()) {
            case HTML:
                loadHtmlView();
                break;

            case IMAGE:
                loadImageView();
                break;

            case JSON:
                loadJsonView();
                break;
        }
    }

    private void loadHtmlView() {
        if(adWebView == null) {
            adWebView = new AAHtmlAdView(context);
            adWebView.addListener(this);
        }

        adWebView.loadHtml(currentAd.getAd());
        notifyViewReadyForDisplay(adWebView);
    }

    private void loadImageView() {
        if(adImageView == null) {
            adImageView = new AAImageAdView(context);
            adImageView.addListener(this);
        }

        adImageView.loadImage(currentAd.getAd());
        notifyViewReadyForDisplay(adImageView);
    }

    private void loadJsonView() {
        if(aaJsonView == null) {
            aaJsonView = new AAJsonAdView(context, resourceId);
            aaJsonView.addListener(this);
        }

        aaJsonView.buildView(currentAd.getAd());
        notifyViewReadyForDisplay(aaJsonView);
    }

    private void scheduleAd() {
        refreshScheduler = new AdZoneRefreshScheduler();
        refreshScheduler.addListener(this);
        refreshScheduler.schedule(currentAd.getAd());
    }

    private void completeCurrentAd() {
        if(currentAd != null && currentAd.hasAd()) {
            notifyResetDisplayView();

            currentAd.completeAdTracking();
            currentAd = ViewAd.createEmptyCurrentAd(context, sessionId);
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
        if(!currentAd.hasAd() && isStoppingForPopup()) {
            currentAd.trackPopupEnd();
            stoppingForPopup = false;
        }

        active = true;

        SessionManager sessionManager = SessionManagerFactory.getInstance(context).createSessionManager();
        sessionManager.addListener(this);
    }

    public void onStop() {
        active = false;

        if(zone.hasNoAds()) {
            return;
        }

        purgeScheduler();

        if(!currentAd.isStoppingForPopup()) {
            completeCurrentAd();
        }

        currentAd.flush();

        SessionManager sessionManager = SessionManagerFactory.getInstance(context).createSessionManager();
        sessionManager.removeListener(this);
    }

    @Override
    public void onSessionInitialized(Session session) {
        AdFetcher adFetcher = AdFetcherFactory.getInstance(context).createAdFetcher();
        adFetcher.addListener(this);

        sessionId = session.getSessionId();
        zone = new ManagedZone(session.getZone(zoneId));

        if(!currentAd.hasAd()) {
            displayNextAd();
        }
    }

    @Override
    public void onSessionInitFailed() {}

    @Override
    public void onSessionNotReinitialized() {}

    @Override
    public void onAdsRefreshed(Map<String, Zone> zones) {
        zone.setAds(zones.get(zoneId).getAds());

        if(!currentAd.hasAd()) {
            displayNextAd();
        }
    }

    @Override
    public void onAdsNotRefreshed() { }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void notifyViewReadyForDisplay(View view) {
        listener.onViewReadyForDisplay(view);
    }

    private void notifyResetDisplayView() {
        listener.onResetDisplayView();
    }

    @Override
    public void onAdZoneRefreshTimer() {
        if(isActive()) {
            displayNextAd();
        }
    }

    @Override
    public void onViewLoaded() {
        currentAd.beginAdTracking();
        AdAdapted.getInstance().publishAdImpression(zoneId);
    }
}
