package com.adadapted.android.sdk.ui.view;

import android.content.Context;
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

    public AAJsonAdView(Context context, int resourceId) {
        super(context);

        listeners = new HashSet<>();

        View view = View.inflate(context, resourceId, null);
    }

    public void buildView(Ad ad) {

    }

    @Override
    public void addListener(AdViewListener listener) {

    }

    @Override
    public void removeListener(AdViewListener listener) {

    }
}
