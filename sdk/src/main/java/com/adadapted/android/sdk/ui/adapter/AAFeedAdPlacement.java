package com.adadapted.android.sdk.ui.adapter;

import android.util.Log;

import com.adadapted.android.sdk.ui.model.ViewAd;

/**
 * Created by chrisweeden on 5/28/15.
 */
public class AAFeedAdPlacement {
    private static final String TAG = AAFeedAdPlacement.class.getName();

    private String zoneId;
    private int placement;

    public AAFeedAdPlacement(String zoneId, int placement) {
        this.zoneId = zoneId;
        this.placement = placement;
    }

    public ViewAd getAdFor(int position) {
        if(position == placement) {
            Log.i(TAG, "Would show ad for position #" + position);
        }

        return null;
    }

    public long getModifiedItemId(int position) {
        return (position >= placement) ? position + 1 : position ;
    }
}
