package com.adadapted.android.sdk.ui.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.adadapted.android.sdk.core.ad.model.Ad;

/**
 * Created by chrisweeden on 5/26/15.
 */
class JsonAdView extends View implements AdView {
    private static final String TAG = HtmlAdView.class.getName();

    private final View view;

    public interface Listener {
        void onJsonViewLoaded();
    }

    private final Listener listener;
    private AdInteractionListener adInteractionListener;

    public JsonAdView(final Context context, final Listener listener, int resourceId) {
        super(context);

        this.listener = listener;

        view = View.inflate(context, resourceId, null);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        notifyOnClick();
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void buildView(Ad ad) {
        listener.onJsonViewLoaded();
    }

    @Override
    public void setAdInteractionListener(AdInteractionListener listener) {
        this.adInteractionListener = listener;
    }

    @Override
    public void removeAdInteractionListener() {
        adInteractionListener = null;
    }

    private void notifyOnClick() {
        if(adInteractionListener != null) {
            adInteractionListener.onClick();
        }
    }
}
