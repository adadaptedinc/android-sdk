package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;

import com.adadapted.android.sdk.ui.view.AaZoneView;

/**
 * Created by chrisweeden on 5/28/15
 */
public class AaFeedAdPlacement {
    private static final String LOGTAG = AaFeedAdPlacement.class.getName();

    private final Context mContext;
    private final String mZoneId;
    private final int mPlacement;
    private final int mResourceId;

    public AaFeedAdPlacement(final Context context,
                             final String zoneId,
                             final int placement) {
        this(context, zoneId, placement, 0);
    }

    public AaFeedAdPlacement(final Context context,
                             final String zoneId,
                             final int placement,
                             final int resourceId) {
        mContext = context;
        mZoneId = zoneId;
        mPlacement = (placement <= 0) ? 0 : placement-1;
        mResourceId = resourceId;
    }

    public AaFeedItem getItem(final int position) {
        if(position == mPlacement) {
            return new AaFeedItem();
        }

        return null;
    }

    public int getModifiedCount(final int count) {
        if(count < mPlacement) {
            return count;
        }

        return count + 1;
    }

    public int getModifiedPosition(final int position) {
        return (position >= mPlacement) ? position - 1 : position ;
    }

    public long getModifiedItemId(final int position) {
        return (position >= mPlacement) ? position - 1 : position;
    }

    public int getModifiedViewTypeCount(final int count) {
        return count + 1;
    }

    public AaZoneView getView(int position) {
        if(position == mPlacement) {
            AaZoneView zoneView = new AaZoneView(mContext);
            zoneView.init(mZoneId, mResourceId);

            return zoneView;
        }

        return null;
    }

    @Override
    public String toString() {
        return "AaFeedAdPlacement{" +
                "zoneId='" + mZoneId + '\'' +
                ", placement=" + mPlacement +
                '}';
    }
}
