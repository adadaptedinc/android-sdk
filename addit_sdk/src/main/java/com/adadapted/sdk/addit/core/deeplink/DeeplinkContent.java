package com.adadapted.sdk.addit.core.deeplink;

import android.os.Parcel;
import android.os.Parcelable;

import com.adadapted.sdk.addit.core.content.AddToListItem;
import com.adadapted.sdk.addit.core.content.Content;

import java.util.List;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class DeeplinkContent extends Content implements Parcelable {
    public static final Creator<DeeplinkContent> CREATOR = new Creator<DeeplinkContent>() {
        @Override
        public DeeplinkContent createFromParcel(Parcel in) {
            return new DeeplinkContent(in);
        }

        @Override
        public DeeplinkContent[] newArray(int size) {
            return new DeeplinkContent[size];
        }
    };


    DeeplinkContent(final String payloadId,
                    final String message,
                    final String image,
                    final int type,
                    final List<AddToListItem> payload) {
        super(payloadId, message, image, type, payload);
    }

    private DeeplinkContent(Parcel in) {
        super(in.readString(),
                in.readString(),
                in.readString(),
                in.readInt(),
                in.createTypedArrayList(AddToListItem.CREATOR));
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
