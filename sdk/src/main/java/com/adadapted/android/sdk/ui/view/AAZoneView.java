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

import com.adadapted.android.sdk.R;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AaZoneView extends RelativeLayout
        implements AdInteractionListener, AaZoneViewController.Listener {
    private static final String TAG = AaZoneView.class.getName();

    private final Context context;

    private String zoneId;
    private int layoutResourceId;

    private AaZoneViewController viewController;
    private boolean visible = true;

    public AaZoneView(Context context) {
        super(context);
        this.context = context;
    }

    public AaZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AaZoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void init(String zoneId) {
        init(zoneId, R.layout.aa_default_json_ad_zone);
    }

    public void init(String zoneId, int layoutResourceId) {
        this.zoneId = zoneId;
        this.layoutResourceId = layoutResourceId;

        setGravity(Gravity.CENTER);
    }

    protected void displayAdView(final View view) {
        Activity activity = (Activity)context;
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

        if(visible) {
            displayAdView(view);
            viewController.acknowledgeDisplay();
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
        viewController = AaZoneViewControllerFactory.getInstance(context).getController(zoneId, layoutResourceId);
        viewController.setListener(this);
    }

    public void onStop() {
        viewController.removeListener();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        switch(visibility) {
            case View.GONE:
            case View.INVISIBLE:
                visible = false;
                onStop();
                break;

            case View.VISIBLE:
                onStart();
                visible = true;
                break;
        }
    }

    @Override
    public void onAdInteraction() {
        if (viewController != null) {
            viewController.handleAdAction();
        }
    }
}
