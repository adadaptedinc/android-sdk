package com.adadapted.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.ui.messaging.AaSdkContentListener;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisher;

public class AaZoneView extends RelativeLayout implements AdZonePresenter.Listener, AdWebView.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaZoneView.class.getName();

    public interface Listener {
        void onAdLoaded();
        void onAdLoadFailed();
    }

    private AdWebView webView;
    private AdZonePresenter presenter;

    private PixelWebView pixelWebView;

    private boolean isVisible = true;

    private Listener listener;

    public AaZoneView(Context context) {
        super(context.getApplicationContext());

        setup(context);
    }

    public AaZoneView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);

        setup(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);

        setup(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context.getApplicationContext(), attrs, defStyleAttr, defStyleRes);

        setup(context);
    }

    private void setup(final Context context) {
        Log.d(LOGTAG, "setup called");

        this.presenter  = new AdZonePresenter(context.getApplicationContext());

        this.webView = new AdWebView(context.getApplicationContext(), this);
        addView(webView);

        this.pixelWebView = new PixelWebView(context.getApplicationContext());
    }

    public void init(final String zoneId) {
        presenter.init(zoneId);
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

    public void onStart() {
        if(presenter != null) {
            presenter.onAttach(this);
        }
    }

    public void onStart(final Listener listener) {
        onStart();

        this.listener = listener;
    }

    public void onStart(final Listener listener,
                        final AaSdkContentListener contentListener) {
        onStart();

        this.listener = listener;
        SdkContentPublisher.getInstance().addListener(contentListener);
    }

    public void onStart(final AaSdkContentListener contentListener) {
        onStart();

        SdkContentPublisher.getInstance().addListener(contentListener);
    }

    public void onStop() {
        if(presenter != null) {
            presenter.onDetach();
        }

        listener = null;
    }

    public void onStop(final AaSdkContentListener listener) {
        SdkContentPublisher.getInstance().removeListener(listener);

        onStop();
    }

    @Override
    public void onZoneAvailable(final Zone zone) {
        Log.d(LOGTAG, "onZoneAvailable called");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                webView.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ));
            }
        });
    }

    @Override
    public void onAdAvailable(final Ad ad) {
        Log.d(LOGTAG, "onAdAvailable called");

        if(isVisible) {
            Log.i(LOGTAG, "Displaying Ad URL: " + ad.getUrl());

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(ad.getUrl());
                    pixelWebView.loadData(ad.getTrackingHtml(), "text/html", null);
                }
            });
        }
    }

    @Override
    public void onAdLoaded() {
        if(listener != null) {
            listener.onAdLoaded();
        }

        if(presenter != null) {
            presenter.onAdDisplayed();
        }
    }

    @Override
    public void onAdLoadFailed() {
        if(listener != null) {
            listener.onAdLoadFailed();
        }

    }

    @Override
    public void onAdClicked() {
        if(presenter != null) {
            presenter.onAdClicked();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        Log.d(LOGTAG, "onVisibilityChanged called");

        switch (visibility) {
            case View.GONE:
                Log.i(LOGTAG, "Visibility changed to GONE");
                setInvisible();
                break;
            case View.INVISIBLE:
                Log.i(LOGTAG, "Visibility changed to INVISIBLE");
                setInvisible();
                break;
            case View.VISIBLE:
                Log.i(LOGTAG, "Visibility changed to VISIBLE");
                setVisible();
                break;
        }
    }

    private void setVisible() {
        Log.d(LOGTAG, "setVisible called");

        isVisible = true;
        if(presenter != null) {
            presenter.onAttach(this);
        }
    }

    private void setInvisible() {
        Log.d(LOGTAG, "setInvisible called");

        isVisible = false;
        if(presenter != null) {
            presenter.onDetach();
        }
    }
}
