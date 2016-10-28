package com.adadapted.android.sdk.core.addit;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class AdditContent implements Content<List<AdditAddToListItem>>, Parcelable {
    public static final Creator<AdditContent> CREATOR = new Creator<AdditContent>() {
        @Override
        public AdditContent createFromParcel(Parcel in) {
            return new AdditContent(in);
        }

        @Override
        public AdditContent[] newArray(int size) {
            return new AdditContent[size];
        }
    };

    private Activity activity;
    private final int type;
    private final List<AdditAddToListItem> payload;

    public static final int ADD_TO_LIST_ITEMS = 1;
    public static final int ADD_TO_LIST_ITEM = 2;

    AdditContent(final Activity activity,
                        final int type,
                        List<AdditAddToListItem> payload) {
        this.activity = activity;
        this.type = type;
        this.payload = payload;
    }

    private AdditContent(Parcel in) {
        type = in.readInt();
        payload = in.createTypedArrayList(AdditAddToListItem.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeTypedList(payload);
    }

    @Override
    public void acknowledge() {
        for (AdditAddToListItem item : payload) {
            final Map<String, String> eventParams = new HashMap<>();
            eventParams.put("tracking_id", item.getTrackingId());
            eventParams.put("item_name", item.getTitle());

            AppEventTrackingManager.registerEvent(
                    AppEventSource.SDK,
                    "addit_added_to_list",
                    eventParams);
        }
    }

    @Override
    public void failed(final String message) {
        AppErrorTrackingManager.registerEvent(
                "ADDIT_CONTENT_FAILED",
                message,
                new HashMap<String, String>());
    }

    @Override
    public int getType() {
        return type;
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    public List<AdditAddToListItem> getPayload() {
        return payload;
    }
}
