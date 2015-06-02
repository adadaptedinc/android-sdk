package com.adadapted.android.sdk.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adadapted.android.sdk.ui.model.ViewAd;

/**
 * Created by chrisweeden on 5/27/15.
 */
public class AAFeedAdapter extends BaseAdapter {
    private static final String TAG = AAFeedAdapter.class.getName();

    private BaseAdapter adapter;
    private AAFeedAdPlacement placement;

    public AAFeedAdapter(BaseAdapter adapter, AAFeedAdPlacement placement) {
        this.adapter = adapter;
        this.placement = placement;
    }

    @Override
    public int getCount() {
        return adapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        ViewAd currentAd = placement.getAdFor(position);

        if(currentAd == null) {
            return adapter.getItem(position);
        }

        return currentAd;
    }

    @Override
    public long getItemId(int position) {
        ViewAd currentAd = placement.getAdFor(position);

        if(currentAd == null) {
            return placement.getModifiedItemId(position);
        }

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return adapter.getView(position, convertView, parent);
    }
}
