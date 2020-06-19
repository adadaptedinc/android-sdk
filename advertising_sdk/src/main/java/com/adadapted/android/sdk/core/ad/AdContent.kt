package com.adadapted.android.sdk.core.ad

import android.os.Parcel
import android.os.Parcelable
import com.adadapted.android.sdk.config.EventStrings
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
    private var adEventClient: AdEventClient
    private var appEventClient: AppEventClient

    private constructor(parcel: Parcel, adClient: AdEventClient = AdEventClient.getInstance(), appClient: AppEventClient = AppEventClient.getInstance()) {
        ad = parcel.readParcelable(Ad::class.java.classLoader)
        type = parcel.readInt()
        items = parcel.createTypedArrayList(AddToListItem.CREATOR)
        isHandled = parcel.readByte().toInt() != 0
        adEventClient = adClient
        appEventClient = appClient
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

    private constructor(ad: Ad, type: Int, items: List<AddToListItem>, adClient: AdEventClient = AdEventClient.getInstance(), appClient: AppEventClient = AppEventClient.getInstance()) {
        if (ad.payload.isEmpty()) {
            appClient.trackError(EventStrings.AD_PAYLOAD_IS_EMPTY, String.format(Locale.ENGLISH, "Ad %s has empty payload", ad.id))
        }
        this.ad = ad
        this.type = type
        this.items = items
        this.adEventClient = adClient
        this.appEventClient = appClient
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
            adEventClient.trackInteraction(ad)
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
                adEventClient.trackInteraction(ad)
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
        appEventClient.trackSdkEvent(EventStrings.ATL_ITEM_ADDED_TO_LIST, params)
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
            appEventClient.trackError(EventStrings.ATL_ADDED_TO_LIST_FAILED,
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
            appEventClient.trackError(EventStrings.ATL_ADDED_TO_LIST_ITEM_FAILED,
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
            return AdContent(ad, ADD_TO_LIST, ad.payload)
        }
    }
}