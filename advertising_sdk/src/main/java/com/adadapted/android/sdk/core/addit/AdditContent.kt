package com.adadapted.android.sdk.core.addit

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

class AdditContent : AddToListContent, Parcelable {
    internal object AdditSources {
        const val IN_APP = "in_app"
        const val DEEPLINK = "deeplink"
        const val PAYLOAD = "payload"
    }

    val payloadId: String
    val message: String
    val image: String
    val type: Int
    private val source: String
    val additSource: String
    private val items: List<AddToListItem>
    private var handled: Boolean
    private val lock: Lock = ReentrantLock()
    private var appEventClient: AppEventClient
    private var payloadClient: PayloadClient

    constructor(
            payloadId: String,
            message: String,
            image: String,
            type: Int,
            source: String,
            additSource: String,
            items: List<AddToListItem>,
            appEventClient: AppEventClient = AppEventClient.getInstance(),
            payloadClient: PayloadClient = PayloadClient.getInstance()
    ) {
        if (items.isEmpty()) {
            appEventClient.trackError(EventStrings.ADDIT_PAYLOAD_IS_EMPTY, String.format(Locale.ENGLISH, "Payload %s has empty payload", payloadId))
        }
        this.payloadId = payloadId
        this.message = message
        this.image = image
        this.type = type
        this.source = source
        this.additSource = additSource
        this.items = items
        this.appEventClient = appEventClient
        this.payloadClient = payloadClient
        handled = false
    }

    private constructor(parcel: Parcel, aclient: AppEventClient = AppEventClient.getInstance(), pclient: PayloadClient = PayloadClient.getInstance()) {
        payloadId = parcel.readString()
        message = parcel.readString()
        image = parcel.readString()
        type = parcel.readInt()
        source = parcel.readString()
        additSource = parcel.readString()
        items = parcel.createTypedArrayList(AddToListItem.CREATOR)
        appEventClient = aclient
        payloadClient = pclient
        handled = parcel.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(payloadId)
        parcel.writeString(message)
        parcel.writeString(image)
        parcel.writeInt(type)
        parcel.writeString(source)
        parcel.writeString(additSource)
        parcel.writeTypedList(items)
        parcel.writeByte((if (handled) 1 else 0).toByte())
    }

    @Synchronized
    override fun acknowledge() {
        lock.lock()
        try {
            if (handled) {
                return
            }
            handled = true
            payloadClient.markContentAcknowledged(this)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    override fun itemAcknowledge(item: AddToListItem) {
        lock.lock()
        try {
            if (!handled) {
                handled = true
                payloadClient.markContentAcknowledged(this)
            }
            payloadClient.markContentItemAcknowledged(this, item)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    fun duplicate() {
        lock.lock()
        try {
            if (handled) {
                return
            }
            handled = true
            payloadClient.markContentDuplicate(this)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    override fun failed(message: String) {
        lock.lock()
        try {
            if (handled) {
                return
            }
            handled = true
            payloadClient.markContentFailed(this, message)
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    override fun itemFailed(item: AddToListItem, message: String) {
        lock.lock()
        try {
            payloadClient.markContentItemFailed(this, item, message)
        } finally {
            lock.unlock()
        }
    }

    override fun getSource(): String {
        return source
    }

    val isPayloadSource: Boolean
        get() = additSource == AdditSources.PAYLOAD

    override fun getItems(): List<AddToListItem> {
        return items
    }

    override fun hasNoItems(): Boolean {
        return items.isEmpty()
    }

    companion object CREATOR : Parcelable.Creator<AdditContent> {
        override fun createFromParcel(parcel: Parcel): AdditContent {
            return AdditContent(parcel)
        }

        override fun newArray(size: Int): Array<AdditContent?> {
            return arrayOfNulls(size)
        }

        fun createDeeplinkContent(
                payloadId: String,
                message: String,
                image: String,
                type: Int,
                items: List<AddToListItem>
        ): AdditContent {
            return AdditContent(payloadId, message, image, type, Sources.OUT_OF_APP, AdditSources.DEEPLINK, items)
        }

        fun createInAppContent(
                payloadId: String,
                message: String,
                image: String,
                type: Int,
                items: List<AddToListItem>
        ): AdditContent {
            return AdditContent(payloadId, message, image, type, Sources.IN_APP, AdditSources.IN_APP, items)
        }

        fun createPayloadContent(
                payloadId: String,
                message: String,
                image: String,
                type: Int,
                items: List<AddToListItem>
        ): AdditContent {
            return AdditContent(payloadId, message, image, type, Sources.OUT_OF_APP, AdditSources.PAYLOAD, items)
        }
    }
}