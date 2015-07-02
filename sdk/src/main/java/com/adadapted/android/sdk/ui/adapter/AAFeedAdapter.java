package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adadapted.android.sdk.ui.view.AAZoneView;

/**
 * Created by chrisweeden on 5/27/15.
 */
public class AAFeedAdapter extends BaseAdapter {
    private static final String TAG = AAFeedAdapter.class.getName();

    private final BaseAdapter adapter;
    private final AAFeedAdPlacement placement;

    private AAZoneView currentZoneView;

    public AAFeedAdapter(Context context, BaseAdapter adapter, String zoneId, int placement, int height, int padding) {
        this.adapter = adapter;
        this.placement = new AAFeedAdPlacement(context, zoneId, placement, height, padding);
    }

    public AAFeedAdapter(Context context, BaseAdapter adapter, String zoneId, int placement, int height) {
        this(context, adapter, zoneId, placement, height, 0);
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
        AAFeedItem feedItem = placement.getItem(position);

        if(feedItem == null) {
            return placement.getModifiedItemId(position);
        }

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AAZoneView view = placement.getView(position);

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
        AAFeedItem feedItem = placement.getItem(position);
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

    @Override
    public String toString() {
        return "AAFeedAdapter{" +
                "adapter=" + adapter +
                ", placement=" + placement +
                '}';
    }
}
