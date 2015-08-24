package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;

import com.adadapted.android.sdk.ui.view.AaZoneView;

/**
 * Created by chrisweeden on 5/28/15.
 */
public class AaFeedAdPlacement {
    private static final String LOGTAG = AaFeedAdPlacement.class.getName();

    private final Context mContext;
    private final String mZoneId;
    private final int mPlacement;
    private final int mResourceId;

    public AaFeedAdPlacement(Context context, String zoneId, int placement) {
        this(context, zoneId, placement, 0);
    }

    public AaFeedAdPlacement(Context context, String zoneId, int placement, int resourceId) {
        mContext = context;
        mZoneId = zoneId;
        mPlacement = (placement <= 0) ? 0 : placement-1;
        mResourceId = resourceId;
    }

    public AaFeedItem getItem(int position) {
        if(position == mPlacement) {
            return new AaFeedItem();
        }

        return null;
    }

    public int getModifiedCount(int count) {
        if(count < mPlacement) {
            return count;
        }

        return count + 1;
    }

    public int getModifiedPosition(int position) {
        return (position >= mPlacement) ? position - 1 : position ;
    }

    public long getModifiedItemId(int position) {
        return (position >= mPlacement) ? position - 1 : position;
    }

    public int getModifiedViewTypeCount(int count) {
        return count + 1;
    }

    public AaZoneView getView(int position) {
        if(position == mPlacement) {
            AaZoneView view = new AaZoneView(mContext);
            view.init(mZoneId, mResourceId);

            return view;
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
