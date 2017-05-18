package com.adadapted.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.ui.messaging.AaSdkContentListener;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisher;

/**
 * Created by chrisweeden on 3/30/15
 */
public class AaZoneView extends RelativeLayout
        implements AdInteractionListener, AaZoneViewController.Listener {
    private static final String LOGTAG = AaZoneView.class.getName();

    private Context mContext;

    private AaZoneViewController mViewController;
    private AaZoneViewProperties mZoneProperties;

    private boolean mVisible = true;

    public AaZoneView(Context context) {
        super(context.getApplicationContext());

        mContext = context.getApplicationContext();
    }

    public AaZoneView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);

        mContext = context.getApplicationContext();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);

        mContext = context.getApplicationContext();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context.getApplicationContext(), attrs, defStyleAttr, defStyleRes);

        mContext = context.getApplicationContext();
    }

    public void init(final String zoneId) {
        init(zoneId, 0);
    }

    public void init(final String zoneId,
                     final int layoutResourceId) {
        final ColorDrawable mBackgroundColor = (ColorDrawable) getBackground();
        final int color = (mBackgroundColor != null) ?mBackgroundColor.getColor() : Color.WHITE;

        mZoneProperties = new AaZoneViewProperties(zoneId, layoutResourceId, color);

        setGravity(Gravity.CENTER);
    }

    public void shutdown() {
        this.setVisibility(View.GONE);
    }

    protected void displayAdView(final View view) {
        //Log.i(LOGTAG, "displayAdView called");
        if(view == null) { return; }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Log.i(LOGTAG, String.format("Pushing Ad display to Zone %s", mZoneProperties.getZoneId()));
                final ViewGroup parent = ((ViewGroup) view.getParent());
                if (parent != null) {
                    parent.removeView(view);
                }

                AaZoneView.this.removeAllViews();
                AaZoneView.this.addView(view);
            }
        });
    }

    @Override
    public void onViewReadyForDisplay(final View view) {
        //Log.i(LOGTAG, "onViewReadyForDisplay called");
        if(view == null) { return; }

        if(view instanceof WebView) {
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(final View v, final MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            AaZoneView.this.onAdInteraction();
                            return true;
                    }

                    return false;
                }
            });
        }
        else {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AaZoneView.this.onAdInteraction();
                }
            });
        }

        if(mVisible) {
            displayAdView(view);

            if(mViewController != null) {
                mViewController.acknowledgeDisplay();
            }
        }
    }

    @Override
    public void onResetDisplayView() {
        //Log.i(LOGTAG, "onResetDisplayView called");
        this.post(new Runnable() {
            @Override
            public void run() {
                AaZoneView.this.removeAllViews();
            }
        });
    }

    public void onStart() {
        //Log.i(LOGTAG, "onStart called");

        if(mZoneProperties == null) {
            return;
        }

        if(mViewController == null && mContext != null) {
            mViewController = AaZoneViewControllerFactory.getController(mContext, mZoneProperties);
            mContext = null;
        }

        if(mViewController != null) {
            mViewController.setListener(this);
        }
    }

    public void onStart(final AaSdkContentListener listener) {
        onStart();

        SdkContentPublisher.getInstance().addListener(listener);
    }

    public void onStop() {
        //Log.i(LOGTAG, "onStop called");

        if(mViewController != null) {
            mViewController.removeListener();
        }
    }

    public void onStop(final AaSdkContentListener listener) {
        SdkContentPublisher.getInstance().removeListener(listener);

        onStop();
    }

    @Override
    protected void onVisibilityChanged(@NonNull final View changedView, final int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        //final String zoneId = mZoneProperties != null ? mZoneProperties.getZoneId() : "None";
        //Log.i(LOGTAG, String.format("Zone %s visibility changed to %d", zoneId, visibility));
        //Log.i(LOGTAG, String.format("Visibilities: %d - GONE, %d - INVISIBLE, %d - VISIBLE", View.GONE, View.INVISIBLE, View.VISIBLE));

        switch(visibility) {
            case View.GONE:
            case View.INVISIBLE:
                //Log.i(LOGTAG, String.format("Ad Zone %s NOT viewable. NO ads will display", zoneId));
                mVisible = false;
                onStop();
                break;
            case View.VISIBLE:
                //Log.i(LOGTAG, String.format("Ad Zone %s viewable. Ads will display", zoneId));
                onStart();
                mVisible = true;
                break;
        }
    }

    @Override
    public void onAdInteraction() {
        if (mViewController != null) {
            mViewController.handleAdAction();
        }
    }
}
