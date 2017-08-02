package com.adadapted.android.sdk.ui.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdEventClient;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdContent implements Parcelable {
    private static final String LOGTAG = AdContentPayload.class.getName();

    public static final int ADD_TO_LIST = 0;
    public static final int RECIPE_FAVORITE = 1;

    private final Ad ad;
    private final int type;
    private final List<AddToListItem> items;

    private boolean handled;

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
        return new AdContent(ad, ADD_TO_LIST, ad.getPayload());
    }

    public synchronized void acknowledge() {
        if(handled) {
            Log.w(LOGTAG, "Content Payload acknowledged multiple times.");
            return;
        }

        handled = true;
        Log.d(LOGTAG, "Content Payload acknowledged.");

        for(AddToListItem item : items) {
            trackItem(ad.getId(), item.getTitle());
        }

        AdEventClient.trackInteraction(ad);
    }

    private void trackItem(final String adId, final String itemName) {
        final Map<String, String> params = new HashMap<>();
        params.put("ad_id", adId);
        params.put("item_name", itemName);

        AppEventClient.trackSdkEvent("atl_added_to_list", params);
    }

    public synchronized void failed(final String message) {
        if(handled) {
            Log.w(LOGTAG, "Content Payload acknowledged/failed multiple times.");
            return;
        }

        handled = true;
        Log.w(LOGTAG, "Content Payload failed.");

        final Map<String, String> params = new HashMap<>();
        params.put("ad_id", ad.getId());
        AppEventClient.trackError("ATL_ADDED_TO_LIST_FAILED",
                (message == null || message.isEmpty()) ? "Unknown Reason" : message,
                params);
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

    public boolean isHandled() {
        return handled;
    }

    @Override
    public String toString() {
        return "AdContent{" +
                "zone='" + ad.getZoneId() + '\'' +
                "items=" + items +
                '}';
    }
}
