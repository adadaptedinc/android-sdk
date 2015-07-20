package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.adadapted.android.sdk.ui.view.AaZoneView;

/**
 * Created by chrisweeden on 5/28/15.
 */
public class AaFeedAdPlacement {
    private static final String TAG = AaFeedAdPlacement.class.getName();

    private final Context context;
    private final String zoneId;
    private final int placement;

    private ViewGroup.LayoutParams layoutParams;
    private int resourceId;

    public AaFeedAdPlacement(Context context, String zoneId, int placement) {
        this.context = context;
        this.zoneId = zoneId;
        this.placement = (placement <= 0) ? 0 : placement-1;
        this.layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public AaFeedAdPlacement(Context context, String zoneId, int placement, int resourceId) {
        this(context, zoneId, placement);
        this.resourceId = resourceId;
    }

    public AaFeedAdPlacement(Context context, String zoneId, int placement,
                             ViewGroup.LayoutParams layoutParams) {
        this(context, zoneId, placement);
        this.layoutParams = layoutParams;
    }

    public AaFeedAdPlacement(Context context, String zoneId, int placement,
                             ViewGroup.LayoutParams layoutParams, int resourceId) {
        this(context, zoneId, placement, layoutParams);
        this.resourceId = resourceId;
    }

    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public AaFeedItem getItem(int position) {
        if(position == placement) {
            return new AaFeedItem();
        }

        return null;
    }

    public int getModifiedCount(int count) {
        if(count < placement) {
            return count;
        }

        return count + 1;
    }

    public int getModifiedPosition(int position) {
        return (position >= placement) ? position - 1 : position ;
    }

    public long getModifiedItemId(int position) {
        return (position >= placement) ? position - 1 : position;
    }

    public int getModifiedViewTypeCount(int count) {
        return count + 1;
    }

    public AaZoneView getView(int position) {
        if(position == placement) {
            AaZoneView view = new AaZoneView(context);
            view.setLayoutParams(layoutParams);
            view.init(zoneId, resourceId);

            return view;
        }

        return null;
    }

    @Override
    public String toString() {
        return "AaFeedAdPlacement{" +
                "zoneId='" + zoneId + '\'' +
                ", placement=" + placement +
                '}';
    }
}
