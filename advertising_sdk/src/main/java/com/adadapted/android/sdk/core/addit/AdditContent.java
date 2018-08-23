package com.adadapted.android.sdk.core.addit;

import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.android.sdk.core.atl.AddToListContent;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.transform.Source;

public class AdditContent implements AddToListContent, Parcelable {
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

    final class AdditSources {
        public static final String IN_APP = "in_app";
        private final static String DEEPLINK = "deeplink";
        private final static String PAYLOAD = "payload";
    }

    private final String payloadId;
    private final String message;
    private final String image;
    private final int type;
    private final String source;
    private final String additSource;
    private final List<AddToListItem> items;

    private boolean handled;
    private final Lock lock = new ReentrantLock();

    public AdditContent(final String payloadId,
                        final String message,
                        final String image,
                        final int type,
                        final String source,
                        final String additSource,
                        final List<AddToListItem> items) {
        if (items.size() == 0) {
            AppEventClient.trackError(
                "ADDIT_PAYLOAD_IS_EMPTY",
                String.format(Locale.ENGLISH, "Payload %s has empty payload", payloadId)
            );
        }

        this.payloadId = payloadId;
        this.message = message;
        this.image = image;
        this.type = type;
        this.source = source;
        this.additSource = additSource;
        this.items = items;

        this.handled = false;
    }

    private AdditContent(Parcel in) {
        payloadId = in.readString();
        message = in.readString();
        image = in.readString();
        type = in.readInt();
        source = in.readString();
        additSource = in.readString();
        items = in.createTypedArrayList(AddToListItem.CREATOR);
        handled = in.readByte() != 0;
    }

    static AdditContent createDeeplinkContent(final String payloadId,
                                              final String message,
                                              final String image,
                                              final int type,
                                              final List<AddToListItem> items) {
        return new AdditContent(payloadId, message, image, type, Sources.OUT_OF_APP, AdditSources.DEEPLINK, items);
    }

    public static AdditContent createInAppContent(final String payloadId,
                                                  final String message,
                                                  final String image,
                                                  final int type,
                                                  final List<AddToListItem> items) {
        return new AdditContent(payloadId, message, image, type, Sources.IN_APP, AdditSources.IN_APP, items);
    }

    static AdditContent createPayloadContent(final String payloadId,
                                             final String message,
                                             final String image,
                                             final int type,
                                             final List<AddToListItem> items) {
        return new AdditContent(payloadId, message, image, type, Sources.OUT_OF_APP, AdditSources.PAYLOAD, items);
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
        dest.writeString(additSource);
        dest.writeTypedList(items);
        dest.writeByte((byte) (handled ? 1 : 0));
    }

    public void acknowledge() {
        lock.lock();
        try {
            if (handled) {
                return;
            }

            handled = true;
            PayloadClient.markContentAcknowledged(this);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void itemAcknowledge(final AddToListItem item) {
        lock.lock();
        try {
            if (!handled) {
                handled = true;
                PayloadClient.markContentAcknowledged(this);
            }

            PayloadClient.markContentItemAcknowledged(this, item);
        }
        finally {
            lock.unlock();
        }
    }

    public void duplicate() {
        lock.lock();
        try {
            if (handled) {
                return;
            }

            handled = true;
            PayloadClient.markContentDuplicate(this);
        }
        finally {
            lock.unlock();
        }
    }

    public void failed(final String message) {
        lock.lock();
        try {
            if (handled) {
                return;
            }

            handled = true;
            PayloadClient.markContentFailed(this, message);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void itemFailed(final AddToListItem item, final String message) {
        lock.lock();
        try {
            PayloadClient.markContentItemFailed(this, item, message);
        }
        finally {
            lock.unlock();
        }
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
        return source.equals(AdditSources.DEEPLINK);
    }

    public boolean isInAppSource() {
        return source.equals(AdditSources.IN_APP);
    }

    public boolean isPayloadSource() {
        return source.equals(AdditSources.PAYLOAD);
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
}
