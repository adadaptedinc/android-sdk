package com.adadapted.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.adadapted.android.sdk.R;
import com.adadapted.android.sdk.core.common.Dimension;

/**
 * Created by chrisweeden on 3/30/15.
 */
public class AAZoneView extends RelativeLayout implements AAZoneViewController.Listener {
    private static final String TAG = AAZoneView.class.getName();

    private final Context context;
    private boolean initialized = false;

    private AAZoneViewController viewController;

    public AAZoneView(Context context) {
        super(context);
        this.context = context;
    }

    public AAZoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AAZoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AAZoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void init(String zoneId) {
        init(zoneId, R.layout.default_json_ad_zone);
    }

    public void init(String zoneId, int layoutResourceId) {
        //viewController = AAZoneViewControllerFactory.getInstance().getController(context, zoneId, layoutResourceId);
        viewController = new AAZoneViewController(context, zoneId, layoutResourceId);
        viewController.setListener(this);

        setGravity(Gravity.CENTER);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewController.processAdInteraction();
            }
        });

        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isNotInitialized() {
        return !isInitialized();
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

    @Override
    public void onViewReadyForDisplay(View view) {
        displayAdView(view);
    }

    @Override
    public void onResetDisplayView() {
        this.post(new Runnable() {
            @Override
            public void run() {
                AAZoneView.this.removeAllViews();
            }
        });
    }

    public void onStart() {
        if(isNotInitialized()) { return; }

        viewController.onStart();
    }

    public void onStop() {
        if (isNotInitialized()) { return; }

        viewController.onStop();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if(viewController.isStoppingForPopup()) {
            return;
        }

        switch(visibility) {
            case View.GONE:
            case View.INVISIBLE:
                Log.d(TAG, "No Longer Visible");
                if(viewController.isVisible() && viewController.isActive()) {
                    viewController.setVisibility(false);
                    onStop();
                }
                break;

            case View.VISIBLE:
                Log.d(TAG, "Is Visible");
                if(viewController.isNotVisible()) {
                    viewController.setVisibility(true);
                    onStart();
                }
                break;
        }
    }
}
