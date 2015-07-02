package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;

/**
 * Created by chrisweeden on 5/26/15.
 */
class AAJsonAdView extends View {
    private static final String TAG = AAHtmlAdView.class.getName();

    private final View view;

    public interface Listener {
        void onJsonViewLoaded();
    }

    private Listener listener;

    public AAJsonAdView(Listener listener, Context context, int resourceId) {
        super(context);

        this.listener = listener;

        view = View.inflate(context, resourceId, null);
    }

    public void buildView(Ad ad) {
        listener.onJsonViewLoaded();
    }
}
