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

public class AaZoneView extends RelativeLayout
        implements AdInteractionListener, AaZoneViewController.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaZoneView.class.getName();

    public interface Listener {
        void onAdLoaded();
        void onAdLoadFailed();
    }

    private Context mContext;
    private Listener mListener;

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
        final ColorDrawable mBackgroundColor = (ColorDrawable) getBackground();
        final int color = (mBackgroundColor != null) ? mBackgroundColor.getColor() : Color.WHITE;

        mZoneProperties = new AaZoneViewProperties(zoneId, color);

        setGravity(Gravity.CENTER);
    }

    @Deprecated
    public void init(final String zoneId,
                     final int layoutResourceId) {
        init(zoneId);
    }

    public void shutdown() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AaZoneView.this.setVisibility(View.GONE);
            }
        });
    }

    protected void displayAdView(final View view) {
        if(view == null) { return; }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
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
        if(view == null || !mVisible) { return; }

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

        displayAdView(view);

        if(mViewController != null) {
            mViewController.acknowledgeDisplay();
        }
    }

    @Override
    public void onResetDisplayView() {
        this.post(new Runnable() {
            @Override
            public void run() {
                AaZoneView.this.removeAllViews();
            }
        });
    }

    @Override
    public void onAdDisplayed() {
        if(mListener != null) {
            mListener.onAdLoaded();
        }
    }

    @Override
    public void onZoneEmpty() {
        if(mListener != null) {
            mListener.onAdLoadFailed();
        }
    }

    public void onStart() {
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

    public void onStart(final Listener listener) {
        onStart();

        mListener = listener;
    }

    public void onStart(final Listener listener,
                        final AaSdkContentListener contentListener) {
        onStart();

        mListener = listener;
        SdkContentPublisher.getInstance().addListener(contentListener);
    }

    public void onStart(final AaSdkContentListener contentListener) {
        onStart();

        SdkContentPublisher.getInstance().addListener(contentListener);
    }

    public void onStop() {
        if(mViewController != null) {
            mViewController.removeListener();
        }

        mListener = null;
    }

    public void onStop(final AaSdkContentListener listener) {
        SdkContentPublisher.getInstance().removeListener(listener);

        onStop();
    }

    @Override
    protected void onVisibilityChanged(@NonNull final View changedView,
                                       final int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        switch(visibility) {
            case View.GONE:
            case View.INVISIBLE:
                mVisible = false;
                onStop();
                break;
            case View.VISIBLE:
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
