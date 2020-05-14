package com.adadapted.android.sdk.core.ad

import android.os.Parcel
import android.os.Parcelable
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListContent.Sources
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.event.AppEventClient
import java.util.Locale
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashMap

class AdContent : AddToListContent, Parcelable {
    val type: Int
    private val ad: Ad
    private val items: List<AddToListItem>
    private var isHandled: Boolean
    private val lock: Lock = ReentrantLock()

    private constructor(parcel: Parcel) {
        ad = parcel.readParcelable(Ad::class.java.classLoader)
        type = parcel.readInt()
        items = parcel.createTypedArrayList(AddToListItem.CREATOR)
        isHandled = parcel.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeParcelable(ad, i)
        parcel.writeInt(type)
        parcel.writeTypedList(items)
        parcel.writeByte((if (isHandled) 1 else 0).toByte())
    }

    private constructor(ad: Ad, type: Int, items: List<AddToListItem>) {
        this.ad = ad
        this.type = type
        this.items = items
        isHandled = false
    }

    @Synchronized
    override fun acknowledge() {
        lock.lock()
        try {
            if (isHandled) {
                return
            }
            isHandled = true
            AdEventClient.trackInteraction(ad)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    override fun itemAcknowledge(item: AddToListItem) {
        lock.lock()
        try {
            if (!isHandled) {
                isHandled = true
                AdEventClient.trackInteraction(ad)
            }
            trackItem(item.title)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    private fun trackItem(itemName: String) {
        val params: MutableMap<String, String> = HashMap()
        params["ad_id"] = ad.id
        params["item_name"] = itemName
        AppEventClient.trackSdkEvent("atl_item_added_to_list", params)
    }

    @Synchronized
    override fun failed(message: String) {
        lock.lock()
        try {
            if (isHandled) {
                return
            }
            isHandled = true
            val params: MutableMap<String, String> = HashMap()
            params["ad_id"] = ad.id
            AppEventClient.trackError("ATL_ADDED_TO_LIST_FAILED",
                    if (message.isEmpty()) "Unknown Reason" else message,
                    params)
        } finally {
            lock.unlock()
        }
    }

    override fun itemFailed(item: AddToListItem, message: String) {
        lock.lock()
        try {
            isHandled = true
            val params: MutableMap<String, String> = HashMap()
            params["ad_id"] = ad.id
            params["item"] = item.title
            AppEventClient.trackError("ATL_ADDED_TO_LIST_ITEM_FAILED",
                    if (message.isEmpty()) "Unknown Reason" else message,
                    params)
        } finally {
            lock.unlock()
        }
    }

    val zoneId: String
        get() = ad.zoneId

    override fun getItems(): List<AddToListItem> {
        return items
    }

    override fun hasNoItems(): Boolean {
        return items.isEmpty()
    }

    override fun getSource(): String {
        return Sources.IN_APP
    }

    companion object CREATOR : Parcelable.Creator<AdContent> {
        private const val ADD_TO_LIST = 0

        override fun createFromParcel(parcel: Parcel): AdContent {
            return AdContent(parcel)
        }

        override fun newArray(size: Int): Array<AdContent?> {
            return arrayOfNulls(size)
        }

        fun createAddToListContent(ad: Ad): AdContent {
            if (ad.payload.isEmpty()) {
                AppEventClient.trackError(
                        "AD_PAYLOAD_IS_EMPTY", String.format(Locale.ENGLISH, "Ad %s has empty payload", ad.id))
            }
            return AdContent(ad, ADD_TO_LIST, ad.payload)
        }
    }
}