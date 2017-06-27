package com.adadapted.android.sdk.core.addit;

import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.android.sdk.ext.management.ContentTrackingManager;

import java.util.List;

public class Content implements Parcelable {
    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    private static final class Sources {
        static final String DEEPLINK = "deeplink";
        static final String PAYLOAD = "payload";
    }

    private final String payloadId;
    private final String message;
    private final String image;
    private final int type;
    private final String source;
    private final List<AddToListItem> payload;

    public Content(final String payloadId,
                   final String message,
                   final String image,
                   final int type,
                   final String source,
                   final List<AddToListItem> payload) {
        this.payloadId = payloadId;
        this.message = message;
        this.image = image;
        this.type = type;
        this.source = source;
        this.payload = payload;
    }

    private Content(Parcel in) {
        payloadId = in.readString();
        message = in.readString();
        image = in.readString();
        type = in.readInt();
        source = in.readString();
        payload = in.createTypedArrayList(AddToListItem.CREATOR);
    }

    public static Content createDeeplinkContent(final String payloadId,
                                                final String message,
                                                final String image,
                                                final int type,
                                                final List<AddToListItem> payload) {
        return new Content(payloadId, message, image, type, Sources.DEEPLINK, payload);
    }

    public static Content createPayloadContent(final String payloadId,
                                               final String message,
                                               final String image,
                                               final int type,
                                               final List<AddToListItem> payload) {
        return new Content(payloadId, message, image, type, Sources.PAYLOAD, payload);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(payloadId);
        dest.writeString(message);
        dest.writeString(image);
        dest.writeInt(type);
        dest.writeString(source);
        dest.writeTypedList(payload);
    }

    public void acknowledge() {
        ContentTrackingManager.markContentAcknowledged(this);
    }

    public void duplicate() {
        ContentTrackingManager.markContentDuplicate(this);
    }

    public void failed(String message) {
        ContentTrackingManager.markContentFailed(this, message);
    }

    public String getPayloadId() {
        return payloadId;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public int getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public boolean isDeeplinkSource() {
        return source.equals(Sources.DEEPLINK);
    }

    public boolean isPayloadSource() {
        return source.equals(Sources.PAYLOAD);
    }

    public List<AddToListItem> getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Content{" +
                "payloadId='" + payloadId + '\'' +
                ", message='" + message + '\'' +
                ", image='" + image + '\'' +
                ", type=" + type +
                ", source='" + source + '\'' +
                ", payload=" + payload +
                '}';
    }
}
