package com.adadapted.sdk.addit.core.payload;

import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.sdk.addit.core.content.AddToListItem;
import com.adadapted.sdk.addit.core.content.Content;
import com.adadapted.sdk.addit.ext.management.PayloadDropoffManager;

import java.util.List;

/**
 * Created by chrisweeden on 2/10/17.
 */
public class PayloadContent extends Content implements Parcelable {
    public static final Creator<PayloadContent> CREATOR = new Creator<PayloadContent>() {
        @Override
        public PayloadContent createFromParcel(Parcel in) {
            return new PayloadContent(in);
        }

        @Override
        public PayloadContent[] newArray(int size) {
            return new PayloadContent[size];
        }
    };

    public PayloadContent(final String payloadId,
                          final String message,
                          final String image,
                          final int type,
                          final List<AddToListItem> payload) {
        super(payloadId, message, image, type, payload);
    }

    private PayloadContent(Parcel in) {
        super(in.readString(),
                in.readString(),
                in.readString(),
                in.readInt(),
                in.createTypedArrayList(AddToListItem.CREATOR));
    }

    @Override
    public void acknowledge() {
        super.acknowledge();

        PayloadDropoffManager.trackDelivered(getPayloadId());
    }

    @Override
    public void duplicate() {
        super.duplicate();

        PayloadDropoffManager.trackRejected(getPayloadId());
    }

    @Override
    public void failed(String message) {
        super.failed(message);

        PayloadDropoffManager.trackRejected(getPayloadId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getPayloadId());
        dest.writeString(getMessage());
        dest.writeString(getImage());
        dest.writeInt(getType());
        dest.writeTypedList(getPayload());
    }
}
