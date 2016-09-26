package com.adadapted.android.sdk.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adadapted.android.sdk.ui.messaging.AaSdkContentListener;
import com.adadapted.android.sdk.ui.messaging.SdkContentPublisherFactory;
import com.adadapted.android.sdk.ui.view.AaZoneView;

/**
 * Created by chrisweeden on 5/27/15
 */
public class AaFeedAdapter extends BaseAdapter {
    private static final String LOGTAG = AaFeedAdapter.class.getName();

    private final BaseAdapter mAdapter;
    private final AaFeedAdPlacement mPlacement;

    private AaZoneView currentZoneView;

    public AaFeedAdapter(final Context context,
                         final BaseAdapter adapter,
                         final String zoneId,
                         final int placement) {
        this(context, adapter, zoneId, placement, 0);
    }

    public AaFeedAdapter(final Context context,
                         final BaseAdapter adapter,
                         final String zoneId,
                         final int placement,
                         final int resourceId) {
        mAdapter = adapter;
        mPlacement = new AaFeedAdPlacement(context, zoneId, placement, resourceId);
    }

    @Override
    public int getCount() {
        if(mPlacement != null && mAdapter != null) {
            return mPlacement.getModifiedCount(mAdapter.getCount());
        }

        return 0;
    }

    @Override
    public Object getItem(final int position) {
        return position;
    }

    @Override
    public long getItemId(final int position) {
        AaFeedItem feedItem = mPlacement.getItem(position);

        if(feedItem == null) {
            return mPlacement.getModifiedItemId(position);
        }

        return position;
    }

    @Override
    public View getView(final int position,
                        final View convertView,
                        final ViewGroup parent) {
        final AaZoneView view = mPlacement.getView(position);

        if(view == null) {
            int modPos = mPlacement.getModifiedPosition(position);
            return mAdapter.getView(modPos, convertView, parent);
        }
        else {
            currentZoneView = view;
            notifyDataSetChanged();

            onStart();

            return currentZoneView;
        }
    }

    @Override
    public int getViewTypeCount() {
        return mPlacement.getModifiedViewTypeCount(mAdapter.getViewTypeCount());
    }

    @Override
    public int getItemViewType(final int position) {
        AaFeedItem feedItem = mPlacement.getItem(position);
        if(feedItem == null) {
            return mAdapter.getViewTypeCount();
        }
        else {
            int modPos = mPlacement.getModifiedPosition(position);

            return mAdapter.getItemViewType(modPos);
        }
    }

    public void onStart() {
        if(currentZoneView != null) {
            currentZoneView.onStart();
        }
    }

    public void onStart(final AaSdkContentListener listener) {
        if(currentZoneView != null) {
            currentZoneView.onStart();
        }

        SdkContentPublisherFactory.getContentPublisher().addListener(listener);
    }

    public void onStop() {
        if(currentZoneView != null) {
            currentZoneView.onStop();
        }
    }

    public void onStop(final AaSdkContentListener listener) {
        if(currentZoneView != null) {
            currentZoneView.onStop();
        }

        SdkContentPublisherFactory.getContentPublisher().removeListener(listener);
    }
}
