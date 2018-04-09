package com.adadapted.android.sdk.core.ad;

import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.atl.AddToListItem;

import java.util.ArrayList;
import java.util.List;

public class Ad implements Parcelable {
    protected Ad(Parcel in) {
        id = in.readString();
        zoneId = in.readString();
        impressionId = in.readString();
        url = in.readString();
        actionType = in.readString();
        actionPath = in.readString();
        payload = in.createTypedArrayList(AddToListItem.CREATOR);
        refreshTime = in.readLong();
        trackingHtml = in.readString();
    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(zoneId);
        parcel.writeString(impressionId);
        parcel.writeString(url);
        parcel.writeString(actionType);
        parcel.writeString(actionPath);
        parcel.writeTypedList(payload);
        parcel.writeLong(refreshTime);
        parcel.writeString(trackingHtml);
    }

    public static class ActionTypes {
        public static final String CONTENT = "c";
        public static final String LINK = "l";
        public static final String POPUP = "p";
    }

    private final String id;
    private final String zoneId;
    private final String impressionId;
    private final String url;
    private final String actionType;
    private final String actionPath;
    private final List<AddToListItem> payload;
    private final long refreshTime;
    private final String trackingHtml;

    public Ad(final String id,
              final String zoneId,
              final String impressionId,
              final String url,
              final String actionType,
              final String actionPath,
              final List<AddToListItem> payload,
              final long refreshTime,
              final String trackingHtml) {
        this.id = id;
        this.zoneId = zoneId;
        this.impressionId = impressionId;
        this.url = url;
        this.actionType = actionType;
        this.actionPath = actionPath;
        this.payload = payload;
        this.refreshTime = refreshTime;
        this.trackingHtml = trackingHtml;
    }

    public static Ad emptyAd() {
        return new Builder().build();
    }

    public boolean isEmpty() {
        return id.isEmpty();
    }

    public String getId() {
        return id;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getImpressionId() {
        return impressionId;
    }

    public String getUrl() {
        return url;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionPath() {
        return actionPath;
    }

    public List<AddToListItem> getPayload() {
        return payload;
    }

    public List<String> getPayloadNames() {
        final List<String> names = new ArrayList<>();

        for(AddToListItem item : payload) {
            names.add(item.getTitle());
        }

        return names;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public String getTrackingHtml() {
        return trackingHtml;
    }

    public static class Builder {
        private String adId;
        private String zoneId;
        private String impressionId;
        private String url;
        private String actionType;
        private String actionPath;
        private List<AddToListItem> payload;
        private long refreshTime;
        private String trackingHtml;

        public Builder() {
            adId = "";
            zoneId = "";
            impressionId = "";
            url = "";
            actionType = "";
            actionPath = "";
            payload = new ArrayList<>();
            refreshTime = Config.DEFAULT_AD_REFRESH;
            trackingHtml = "";
        }

        public String getAdId() {
            return adId;
        }

        public void setAdId(final String id) {
            this.adId = id;
        }

        public String getZoneId() {
            return zoneId;
        }

        public void setZoneId(final String zoneId) {
            this.zoneId = zoneId;
        }

        public String getImpressionId() {
            return impressionId;
        }

        public void setImpressionId(final String impressionId) {
            this.impressionId = impressionId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(final String actionType) {
            this.actionType = actionType;
        }

        public String getActionPath() {
            return actionPath;
        }

        public void setActionPath(final String actionPath) {
            this.actionPath = actionPath;
        }

        public List<AddToListItem> getPayload() {
            return payload;
        }

        public void setPayload(final List<AddToListItem> payload) {
            this.payload = payload;
        }

        public long getRefreshTime() {
            return refreshTime;
        }

        public void setRefreshTime(final long refreshTime) {
            this.refreshTime = refreshTime;
        }

        public String getTrackingHtml() {
            return trackingHtml;
        }

        public void setTrackingHtml(final String trackingHtml) {
            this.trackingHtml = trackingHtml;
        }

        public Ad build() {
            return new Ad(
                getAdId(),
                getZoneId(),
                getImpressionId(),
                getUrl(), getActionType(),
                getActionPath(),
                getPayload(),
                getRefreshTime(),
                getTrackingHtml()
            );
        }
    }
}
