package com.adadapted.android.sdk.core.ad

import android.os.Parcel
import android.os.Parcelable
import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.core.atl.AddToListItem

class Ad : Parcelable {
    val id: String
    val zoneId: String
    val impressionId: String
    val url: String
    val actionType: String
    val actionPath: String
    val payload: List<AddToListItem>
    val refreshTime: Long
    val trackingHtml: String

    private constructor(parcel: Parcel) {
        id = parcel.readString()
        zoneId = parcel.readString()
        impressionId = parcel.readString()
        url = parcel.readString()
        actionType = parcel.readString()
        actionPath = parcel.readString()
        payload = parcel.createTypedArrayList(AddToListItem.CREATOR)
        refreshTime = parcel.readLong()
        trackingHtml = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(id)
        parcel.writeString(zoneId)
        parcel.writeString(impressionId)
        parcel.writeString(url)
        parcel.writeString(actionType)
        parcel.writeString(actionPath)
        parcel.writeTypedList(payload)
        parcel.writeLong(refreshTime)
        parcel.writeString(trackingHtml)
    }

    @JvmOverloads
    constructor(id: String = "",
                zoneId: String = "",
                impressionId: String = "",
                url: String = "",
                actionType: String = "",
                actionPath: String = "",
                payload: List<AddToListItem> = arrayListOf(),
                refreshTime: Long = Config.DEFAULT_AD_REFRESH,
                trackingHtml: String = "") {
        this.id = id
        this.zoneId = zoneId
        this.impressionId = impressionId
        this.url = url
        this.actionType = actionType
        this.actionPath = actionPath
        this.payload = payload
        this.refreshTime = refreshTime
        this.trackingHtml = trackingHtml
    }

    val isEmpty: Boolean
        get() = id.isEmpty()

    val content: AdContent by lazy {
        AdContent.createAddToListContent(this)
    }

    companion object CREATOR : Parcelable.Creator<Ad> {
        override fun createFromParcel(parcel: Parcel): Ad {
            return Ad(parcel)
        }

        override fun newArray(size: Int): Array<Ad?> {
            return arrayOfNulls(size)
        }
    }
}