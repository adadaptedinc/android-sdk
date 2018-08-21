package com.adadapted.android.sdk.core.ad;

import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.android.sdk.core.atl.AddToListContent;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AdContent implements AddToListContent, Parcelable {
    private static final String LOGTAG = AdContent.class.getName();

    public static final int ADD_TO_LIST = 0;

    private final Ad ad;
    private final int type;
    private final List<AddToListItem> items;

    private boolean handled;
    private final Lock lock = new ReentrantLock();

    protected AdContent(Parcel in) {
        ad = in.readParcelable(Ad.class.getClassLoader());
        type = in.readInt();
        items = in.createTypedArrayList(AddToListItem.CREATOR);
        handled = in.readByte() != 0;
    }

    public static final Creator<AdContent> CREATOR = new Creator<AdContent>() {
        @Override
        public AdContent createFromParcel(Parcel in) {
            return new AdContent(in);
        }

        @Override
        public AdContent[] newArray(int size) {
            return new AdContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(ad, i);
        parcel.writeInt(type);
        parcel.writeTypedList(items);
        parcel.writeByte((byte) (handled ? 1 : 0));
    }

    private AdContent(final Ad ad,
                      final int type,
                      final List<AddToListItem> items) {
        this.ad = ad;
        this.type = type;
        this.items = items;

        handled = false;
    }

    public static AdContent createAddToListContent(final Ad ad) {
        if (ad.getPayload().size() == 0) {
            AppEventClient.trackError(
                "AD_PAYLOAD_IS_EMPTY",
                String.format(Locale.ENGLISH, "Ad %s has empty payload", ad.getId())
            );
        }

        return new AdContent(ad, ADD_TO_LIST, ad.getPayload());
    }

    public synchronized void acknowledge() {
        lock.lock();
        try {
            if(handled) {
                return;
            }

            handled = true;
            AdEventClient.trackInteraction(ad);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void itemAcknowledge(final AddToListItem item) {
        lock.lock();
        try {
            if (!handled) {
                handled = true;
                AdEventClient.trackInteraction(ad);
            }

            trackItem(item.getTitle());
        }
        finally {
            lock.unlock();
        }
    }

    private synchronized void trackItem(final String itemName) {
        final Map<String, String> params = new HashMap<>();
        params.put("ad_id", ad.getId());
        params.put("item_name", itemName);

        AppEventClient.trackSdkEvent("atl_added_to_list", params);
    }

    public synchronized void failed(final String message) {
        lock.lock();
        try {
            if(handled) {
                return;
            }

            handled = true;
            final Map<String, String> params = new HashMap<>();
            params.put("ad_id", ad.getId());
            AppEventClient.trackError("ATL_ADDED_TO_LIST_FAILED",
                    (message == null || message.isEmpty()) ? "Unknown Reason" : message,
                    params);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void itemFailed(final AddToListItem item, final String message) {
        lock.lock();
        try {
            handled = true;
            final Map<String, String> params = new HashMap<>();
            params.put("ad_id", ad.getId());
            params.put("item", item.getTitle());
            AppEventClient.trackError("ATL_ADDED_TO_LIST_ITEM_FAILED",
                    (message == null || message.isEmpty()) ? "Unknown Reason" : message,
                    params);
        }
        finally {
            lock.unlock();
        }
    }

    public String getZoneId() {
        return ad.getZoneId();
    }

    public int getType() {
        return type;
    }

    public List<AddToListItem> getItems() {
        return items;
    }

    public boolean hasItems() {
        return items.size() > 0;
    }

    public boolean hasNoItems() {
        return items.size() == 0;
    }

    public boolean isHandled() {
        return handled;
    }

    public String getSource() {
        return Sources.IN_APP;
    }

    @Override
    public String toString() {
        return "AdContent{" +
            "zone='" + ad.getZoneId() + '\'' +
            "items=" + items +
            '}';
    }
}
