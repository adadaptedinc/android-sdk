package com.adadapted.android.sdk;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AAZoneView extends RelativeLayout
        implements AdAdapted.Listener, AdImageLoader.Listener, AdZoneRefreshScheduler.Listener {
    private static final String TAG = AAZoneView.class.getName();

    private String zoneLabel;

    private boolean isActive;
    private boolean isStoppingForPopup = false;

    private String zoneId;
    private String sessionId;
    private Zone zone;

    private int viewCount;
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

    public String getZoneLabel() {
        return zoneLabel;
    }

    public void setZoneLabel(String zoneLabel) {
        this.zoneLabel = zoneLabel;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void init(String zoneId) {
        Log.d(TAG, getZoneLabel() + " Calling init() with " + zoneId);

        this.zoneId = zoneId;

        this.imageLoader = new AdImageLoader();
        this.imageLoader.addListener(this);

        this.adImageView = new ImageView(getContext());
        this.adImageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zone == null) {
                    return;
                }

                Log.d(TAG, getZoneLabel() + " Ad clicked in Zone " + AAZoneView.this.zoneId);
                AdAdapted.getInstance().getEventTracker().trackInteractionEvent(sessionId, currentAd);
                AdAdapted.getInstance().getEventTracker().trackPopupBeginEvent(sessionId, currentAd);

                isStoppingForPopup = true;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentAd.getActionPath()));
                getContext().startActivity(intent);
            }
        });
    }

    private void displayNextAd() {
        Log.d(TAG, getZoneLabel() + " Calling displayNextAd()");

        if(zone == null) {
            Log.d(TAG, getZoneLabel() + " No ads for zone " + zoneId);
            return;
        }

        completeCurrentAd();

        setNextAd();

        displayAdImageView(adImageView);
    }

    private void setNextAd() {
        int adPosition = (viewCount++) % zone.getAds().size();
        currentAd = zone.getAds().get(adPosition);

        Log.d(TAG, getZoneLabel() + " Displaying " + currentAd.getAdId());
        AdImage adImage = currentAd.getStandardImages();
        imageLoader.getImage(adImage.getOrientation("port"));

        AdAdapted.getInstance().getEventTracker().trackImpressionBeginEvent(sessionId, currentAd);

        scheduleAd();
    }

    private void scheduleAd() {
        refreshScheduler = new AdZoneRefreshScheduler();
        refreshScheduler.addListener(this);
        refreshScheduler.schedule(currentAd);
    }

    private void completeCurrentAd() {
        if(currentAd != null && viewCount > 0) {
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

        if(zone == null) {
            return;
        }

        refreshScheduler.removeListener(this);
        refreshScheduler.cancel();
        refreshScheduler.purge();

        if(!isStoppingForPopup) {
            completeCurrentAd();
        }

        AdAdapted.getInstance().getEventTracker().publishEvents();
    }

    @Override
    public void onSessionLoaded(Session session) {
        Log.d(TAG, getZoneLabel() + " Calling onSessionLoaded()");

        this.sessionId = session.getSessionId();
        this.zone = session.getZone(zoneId);
        viewCount = 0;

        displayNextAd();
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
