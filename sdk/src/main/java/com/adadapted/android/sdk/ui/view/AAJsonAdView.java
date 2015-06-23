package com.adadapted.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.ui.listener.AdViewListenable;
import com.adadapted.android.sdk.ui.listener.AdViewListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class AAJsonAdView extends View implements AdViewListenable {
    private static final String TAG = AAHtmlAdView.class.getName();

    private Set<AdViewListener> listeners;
    private View view;

    public AAJsonAdView(Context context, int resourceId) {
        super(context);

        listeners = new HashSet<>();
        view = View.inflate(context, resourceId, null);
    }

    public AAJsonAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AAJsonAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AAJsonAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void buildView(Ad ad) {

    }

    @Override
    public void addListener(AdViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(AdViewListener listener) {
        listeners.remove(listener);
    }
}
