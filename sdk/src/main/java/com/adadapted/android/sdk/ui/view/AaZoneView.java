package com.adadapted.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.ui.messaging.AaSdkContentListener;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisherFactory;

/**
 * Created by chrisweeden on 3/30/15
 */
public class AaZoneView extends RelativeLayout
        implements AdInteractionListener, AaZoneViewControllerListener {
    private static final String LOGTAG = AaZoneView.class.getName();

    private final Context mContext;

    private String mZoneId;
    private int mLayoutResourceId;

    private AaZoneViewController mViewController;
    private boolean mVisible = true;

    public AaZoneView(Context context) {
        super(context);
        mContext = context;
    }

    public AaZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public void init(String zoneId) {
        init(zoneId, 0);
    }

    public void init(String zoneId, int layoutResourceId) {
        mZoneId = zoneId;
        mLayoutResourceId = layoutResourceId;

        setGravity(Gravity.CENTER);
    }

    protected void displayAdView(final View view) {
        if(view == null) { return; }

        Activity activity = (Activity)mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = ((ViewGroup) view.getParent());
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
        if(view instanceof WebView) {
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
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
                public void onClick(View v) {
                    AaZoneView.this.onAdInteraction();
                }
            });
        }

        if(mVisible) {
            displayAdView(view);
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

    public void onStart() {
        mViewController = AaZoneViewControllerFactory.getController(mContext, mZoneId, mLayoutResourceId);
        mViewController.setListener(this);
    }

    public void onStart(AaSdkContentListener listener) {
        onStart();

        SdkContentPublisherFactory.getContentPublisher().addListener(listener);
    }

    public void onStop() {
        mViewController.removeListener();
    }

    public void onStop(AaSdkContentListener listener) {
        SdkContentPublisherFactory.getContentPublisher().removeListener(listener);

        onStop();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
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
