package com.adadapted.android.sdk.ui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.ui.messaging.AaSdkContentListener;
import com.adadapted.android.sdk.ui.messaging.AdContentListener;
import com.adadapted.android.sdk.ui.messaging.AdContentPublisher;
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

    private boolean isVisible = true;

    private int width;
    private int height;

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
        this.presenter  = new AdZonePresenter(context.getApplicationContext());

        this.webView = new AdWebView(context.getApplicationContext(), this);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                addView(webView);
            }
        });
    }

    public void init(final String zoneId) {
        presenter.init(zoneId);
    }

    @SuppressWarnings("UnusedParameters")
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

    /**
     * onStart()
     */

    public void onStart() {
        if(presenter != null) {
            presenter.onAttach(this);
        }
    }

    public void onStart(final Listener listener) {
        onStart();

        this.listener = listener;
    }

    @Deprecated
    public void onStart(final Listener listener,
                        final AaSdkContentListener contentListener) {
        onStart();

        this.listener = listener;
        SdkContentPublisher.getInstance().addListener(contentListener);
    }

    @Deprecated
    public void onStart(final AaSdkContentListener contentListener) {
        onStart();

        SdkContentPublisher.getInstance().addListener(contentListener);
    }

    public void onStart(final Listener listener,
                        final AdContentListener contentListener) {
        onStart();

        this.listener = listener;
        AdContentPublisher.getInstance().addListener(contentListener);
    }

    public void onStart(final AdContentListener contentListener) {
        onStart();

        AdContentPublisher.getInstance().addListener(contentListener);
    }

    /**
     * onStop()
     */

    public void onStop() {
        if(presenter != null) {
            presenter.onDetach();
        }

        listener = null;
    }

    @Deprecated
    public void onStop(final AaSdkContentListener listener) {
        SdkContentPublisher.getInstance().removeListener(listener);

        onStop();
    }

    public void onStop(final AdContentListener listener) {
        AdContentPublisher.getInstance().removeListener(listener);

        onStop();
    }

    private void notifyAdLoaded() {
        if(listener != null) {
            listener.onAdLoaded();
        }
    }

    private void notifyAdLoadFailed() {
        if(listener != null) {
            listener.onAdLoadFailed();
        }
    }

    /*
     * Overrides from AdZonePresenter.Listener
     */

    @Override
    public void onZoneAvailable(final Zone zone) {
        if(width == 0 || height == 0) {
            final Dimension dimension = zone.getDimensions().get(Dimension.ORIEN.PORT);
            width = (dimension == null) ? LayoutParams.MATCH_PARENT : dimension.getWidth();
            height = (dimension == null) ? LayoutParams.MATCH_PARENT : dimension.getHeight();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                webView.setLayoutParams(new LayoutParams(width, height));
            }
        });
    }

    @Override
    public void onAdAvailable(final Ad ad) {
        if(isVisible) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    webView.loadAd(ad);
                }
            });
        }
    }

    @Override
    public void onNoAdAvailable() {
        notifyAdLoadFailed();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                webView.loadBlank();
            }
        });
    }

    /*
     * Overrides from AdWebView.Listener
     */

    @Override
    public void onAdLoaded(final Ad ad) {
        if(presenter != null) {
            presenter.onAdDisplayed(ad);
            notifyAdLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(final Ad ad) {
        if(presenter != null) {
            presenter.onAdDisplayFailed(ad);
            notifyAdLoadFailed();
        }
    }

    @Override
    public void onAdClicked(final Ad ad) {
        if(presenter != null) {
            presenter.onAdClicked(ad);
        }
    }

    @Override
    public void onBlankLoaded() {
        if(presenter != null) {
            presenter.onBlankDisplayed();
        }
    }

    @SuppressLint("SwitchIntDef")
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        switch (visibility) {
            case View.GONE:
                setInvisible();
                break;
            case View.INVISIBLE:
                setInvisible();
                break;
            case View.VISIBLE:
                setVisible();
                break;
        }
    }

    private void setVisible() {
        isVisible = true;
        if(presenter != null) {
            presenter.onAttach(this);
        }
    }

    private void setInvisible() {
        isVisible = false;
        if(presenter != null) {
            presenter.onDetach();
        }
    }
}
