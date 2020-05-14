package com.adadapted.android.sdk.core.atl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PopupContent implements AddToListContent, Parcelable {
    private final String payloadId;
    private final List<AddToListItem> items;

    private boolean handled;
    private final Lock lock = new ReentrantLock();

    public static PopupContent createPopupContent(final String payloadId,
                                                  final List<AddToListItem> items) {
        return new PopupContent(payloadId, items);
    }

    public PopupContent(final String payloadId, final List<AddToListItem> items) {
        this.payloadId = payloadId;
        this.items = items;
    }

    protected PopupContent(Parcel in) {
        payloadId = in.readString();
        items = in.createTypedArrayList(AddToListItem.CREATOR);
        handled = in.readByte() != 0;
    }

    public static final Creator<PopupContent> CREATOR = new Creator<PopupContent>() {
        @Override
        public PopupContent createFromParcel(Parcel in) {
            return new PopupContent(in);
        }

        @Override
        public PopupContent[] newArray(int size) {
            return new PopupContent[size];
        }
    };

    @Override
    public void acknowledge() {
        lock.lock();
        try {
            if (!handled) {
                handled = true;
                PopupClient.markPopupContentAcknowledged(this);
            }
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
                PopupClient.markPopupContentAcknowledged(this);
            }

            PopupClient.markPopupContentItemAcknowledged(this, item);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void failed(final String message) {
        lock.lock();
        try {
            if (!handled) {
                handled = true;
                PopupClient.markPopupContentFailed(this, message);
            }
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void itemFailed(final AddToListItem item, final String message) {
        lock.lock();
        try {
            PopupClient.markPopupContentItemFailed(this, item, message);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public String getSource() {
        return Sources.IN_APP;
    }

    public String getPayloadId() {
        return payloadId;
    }

    @Override
    public List<AddToListItem> getItems() {
        return items;
    }

    @Override
    public boolean hasNoItems() {
        return items.size() == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(payloadId);
        parcel.writeTypedList(items);
        parcel.writeByte((byte) (handled ? 1 : 0));
    }
}
