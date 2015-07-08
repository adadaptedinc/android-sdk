package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.widget.AbsListView;

import com.adadapted.android.sdk.ui.view.AaZoneView;

/**
 * Created by chrisweeden on 5/28/15.
 */
public class AaFeedAdPlacement {
    private static final String TAG = AaFeedAdPlacement.class.getName();

    private final Context context;
    private final String zoneId;
    private final int placement;
    private final int width;
    private final int height;
    private final int padding;

    public AaFeedAdPlacement(Context context, String zoneId, int placement, int height, int padding) {
        this.context = context;
        this.zoneId = zoneId;
        this.placement = (placement <= 0) ? 0 : placement-1;

        this.width = AbsListView.LayoutParams.MATCH_PARENT;
        this.height = height < 1 ? convertToDp(120) : convertToDp(height);
        this.padding = padding < 1 ? convertToDp(0) : convertToDp(padding);
    }

    public AaFeedAdPlacement(Context context, String zoneId, int placement, int height) {
        this(context, zoneId, placement, height, 0);
    }

    private int convertToDp(int value) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
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
            view.setLayoutParams(new AbsListView.LayoutParams(width, height));
            view.setPadding(padding, padding, padding, padding);
            view.init(zoneId);

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
