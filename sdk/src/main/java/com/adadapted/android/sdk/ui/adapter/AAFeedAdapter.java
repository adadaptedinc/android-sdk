package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adadapted.android.sdk.ui.view.AaZoneView;

/**
 * Created by chrisweeden on 5/27/15.
 */
public class AaFeedAdapter extends BaseAdapter {
    private static final String TAG = AaFeedAdapter.class.getName();

    private final BaseAdapter adapter;
    private final AaFeedAdPlacement placement;

    private AaZoneView currentZoneView;

    public AaFeedAdapter(Context context, BaseAdapter adapter, String zoneId, int placement) {
        this.adapter = adapter;
        this.placement = new AaFeedAdPlacement(context, zoneId, placement);
    }

    public AaFeedAdapter(Context context, BaseAdapter adapter, String zoneId, int placement,
                         int resourceId) {
        this.adapter = adapter;
        this.placement = new AaFeedAdPlacement(context, zoneId, placement, resourceId);
    }

    public AaFeedAdapter(Context context, BaseAdapter adapter, String zoneId, int placement,
                         ViewGroup.LayoutParams layoutParams) {
        this.adapter = adapter;
        this.placement = new AaFeedAdPlacement(context, zoneId, placement, layoutParams);
    }

    public AaFeedAdapter(Context context, BaseAdapter adapter, String zoneId, int placement,
                         ViewGroup.LayoutParams layoutParams, int resourceId) {
        this.adapter = adapter;
        this.placement = new AaFeedAdPlacement(context, zoneId, placement, layoutParams, resourceId);
    }

    @Override
    public int getCount() {
        return placement.getModifiedCount(adapter.getCount());
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        AaFeedItem feedItem = placement.getItem(position);

        if(feedItem == null) {
            return placement.getModifiedItemId(position);
        }

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AaZoneView view = placement.getView(position);

        if(view == null) {
            int modPos = placement.getModifiedPosition(position);
            return adapter.getView(modPos, convertView, parent);
        }

        currentZoneView = view;
        onStart();
        return currentZoneView;
    }

    @Override
    public int getViewTypeCount() {
        return placement.getModifiedViewTypeCount(adapter.getViewTypeCount());
    }

    @Override
    public int getItemViewType(int position) {
        AaFeedItem feedItem = placement.getItem(position);
        if(feedItem == null) {
            return adapter.getViewTypeCount();
        }
        else {
            int modPos = placement.getModifiedPosition(position);
            return adapter.getItemViewType(modPos);
        }
    }

    public void onStart() {
        if(currentZoneView != null) {
            currentZoneView.onStart();
        }
    }

    public void onStop() {
        if(currentZoneView != null) {
            currentZoneView.onStop();
        }
    }
}
