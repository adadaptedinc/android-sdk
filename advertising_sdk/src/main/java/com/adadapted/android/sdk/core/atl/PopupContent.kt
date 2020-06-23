package com.adadapted.android.sdk.core.atl

import android.os.Parcel
import android.os.Parcelable
import com.adadapted.android.sdk.core.atl.AddToListContent.Sources
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentAcknowledged
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentFailed
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentItemAcknowledged
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentItemFailed
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class PopupContent : AddToListContent, Parcelable {
    val payloadId: String
    private val items: List<AddToListItem>
    private var handled = false
    private val lock: Lock = ReentrantLock()

    constructor(payloadId: String, items: List<AddToListItem>) {
        this.payloadId = payloadId
        this.items = items
    }

    private constructor(parcel: Parcel) {
        payloadId = parcel.readString()
        items = parcel.createTypedArrayList(AddToListItem)
        handled = parcel.readByte().toInt() != 0
    }

    override fun acknowledge() {
        lock.lock()
        try {
            if (!handled) {
                handled = true
                markPopupContentAcknowledged(this)
            }
        } finally {
            lock.unlock()
        }
    }

    override fun itemAcknowledge(item: AddToListItem) {
        lock.lock()
        try {
            if (!handled) {
                handled = true
                markPopupContentAcknowledged(this)
            }
            markPopupContentItemAcknowledged(this, item)
        } finally {
            lock.unlock()
        }
    }

    override fun failed(message: String) {
        lock.lock()
        try {
            if (!handled) {
                handled = true
                markPopupContentFailed(this, message)
            }
        } finally {
            lock.unlock()
        }
    }

    override fun itemFailed(item: AddToListItem, message: String) {
        lock.lock()
        try {
            markPopupContentItemFailed(this, item, message)
        } finally {
            lock.unlock()
        }
    }

    override fun getSource(): String {
        return Sources.IN_APP
    }

    override fun getItems(): List<AddToListItem> {
        return items
    }

    override fun hasNoItems(): Boolean {
        return items.isEmpty()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(payloadId)
        parcel.writeTypedList(items)
        parcel.writeByte((if (handled) 1 else 0).toByte())
    }

    companion object CREATOR : Parcelable.Creator<PopupContent> {
        fun createPopupContent(payloadId: String, items: List<AddToListItem>): PopupContent {
            return PopupContent(payloadId, items)
        }

        override fun createFromParcel(parcel: Parcel): PopupContent {
            return PopupContent(parcel)
        }

        override fun newArray(size: Int): Array<PopupContent?> {
            return arrayOfNulls(size)
        }
    }
}