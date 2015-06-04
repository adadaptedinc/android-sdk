package com.adadapted.android.sdk.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adadapted.android.sdk.ui.view.AAZoneView;

/**
 * Created by chrisweeden on 5/27/15.
 */
public class AAFeedAdapter extends BaseAdapter {
    private static final String TAG = AAFeedAdapter.class.getName();

    private BaseAdapter adapter;
    private AAFeedAdPlacement placement;
    private AAZoneView currentZoneView;

    public AAFeedAdapter(BaseAdapter adapter, AAFeedAdPlacement placement) {
        this.adapter = adapter;
        this.placement = placement;
    }

    @Override
    public int getCount() {
        return placement.getModifiedCount(adapter.getCount());
    }

    @Override
    public Object getItem(int position) {
        AAFeedItem feedItem = placement.getItem(position);

        if(feedItem == null) {
            int modPos = placement.getModifiedPosition(position);
            return adapter.getItem(modPos);
        }

        return feedItem;
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
