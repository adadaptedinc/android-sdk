package com.adadapted.android.sdk.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.HtmlAdType;
import com.adadapted.android.sdk.core.ad.ImageAdType;
import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.ext.http.AdImageLoader;
import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdImage;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.ext.scheduler.AdZoneRefreshScheduler;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AAZoneView extends RelativeLayout
        implements AdAdapted.Listener, AdImageLoader.Listener, AdZoneRefreshScheduler.Listener {
    private static final String TAG = AAZoneView.class.getName();

    private String zoneLabel;

    private boolean isActive;
    private boolean isVisible = true;
    private boolean isStoppingForPopup = false;

    private String zoneId;
    private String sessionId;
    private int adCountForZone;

    private Ad currentAd;

    private AdImageLoader imageLoader;
    private AdZoneRefreshScheduler refreshScheduler;
    private ImageView adImageView;
    private Bitmap adImage;

    private final Runnable buildAdRunnable = new Runnable() {
        public void run() {
        Log.d(TAG, "Setting image view bitmap.");
        adImageView.setImageBitmap(adImage);
        }
    };

    private final Handler buildAdHandler = new Handler();

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

        imageLoader = new AdImageLoader();
        imageLoader.addListener(this);

        adImageView = new ImageView(getContext());
        adImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        adImageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        setGravity(Gravity.CENTER);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoneHasNoAds() || currentAd == null) {
                    return;
                }

                Log.d(TAG, getZoneLabel() + " Ad " + currentAd.getAdId() + " clicked in Zone " + AAZoneView.this.zoneId);
                AdAdapted.getInstance().getEventTracker().trackInteractionEvent(sessionId, currentAd);
                AdAdapted.getInstance().getEventTracker().trackPopupBeginEvent(sessionId, currentAd);

                isStoppingForPopup = true;

                Intent intent = new Intent(getContext(), WebViewPopupActivity.class);
                intent.putExtra(WebViewPopupActivity.EXTRA_POPUP_URL, currentAd.getAdAction().getActionPath());
                getContext().startActivity(intent);
            }
        });
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
        AdAdapted.getInstance().getEventTracker().trackImpressionBeginEvent(sessionId, currentAd);
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
        HtmlAdType adType = (HtmlAdType) currentAd.getAdType();
    }

    private void loadImage() {
        ImageAdType adType = (ImageAdType) currentAd.getAdType();

        String imageUrl = adType.getImageUrlFor(ImageAdType.STANDARD_IMAGE, AdImage.PORTRAIT);
        imageLoader.getImage(imageUrl);

        displayAdImageView(adImageView);
    }

    private void loadJson() {

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

            AdAdapted.getInstance().getEventTracker().trackImpressionEndEvent(sessionId, currentAd);
            currentAd = null;
        }
    }

    private void displayAdImageView(View view) {
        Log.d(TAG, "Calling onDisplayAd()");

        final View updatedView = view;

        this.post(new Runnable() {
            @Override
            public void run() {
                AAZoneView.this.removeAllViews();

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                AAZoneView.this.addView(updatedView);
            }
        });
    }

    public void onStart() {
        Log.d(TAG, getZoneLabel() + " Calling onStart()");

        if(isStoppingForPopup) {
            AdAdapted.getInstance().getEventTracker().trackPopupEndEvent(sessionId, currentAd);
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

        AdAdapted.getInstance().getEventTracker().publishEvents();
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
    public void onAdImageLoaded(Bitmap bitmap) {
        Log.d(TAG, getZoneLabel() + " Calling onAdImageLoaded()");

        adImage = bitmap;
        buildAdHandler.post(buildAdRunnable);
    }

    @Override
    public void onAdZoneRefreshTimer() {
        Log.d(TAG, getZoneLabel() + " Calling onAdZoneRefreshTimer()");

        if(isActive) {
            displayNextAd();
        }
    }
}
