package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.ext.models.Payload
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Ad : Serializable {
    @SerializedName("ad_id")
    val id: String

    @SerializedName("impression_id")
    val impressionId: String

    @SerializedName("creative_url")
    val url: String

    @SerializedName("action_type")
    val actionType: String

    @SerializedName("action_path")
    val actionPath: String

    val payload: Payload

    @SerializedName("refresh_time")
    val refreshTime: Long

    @SerializedName("tracking_html")
    val trackingHtml: String

    constructor(id: String = "",
                impressionId: String = "",
                url: String = "",
                actionType: String = "",
                actionPath: String = "",
                payload: Payload = Payload(listOf()),
                refreshTime: Long = Config.DEFAULT_AD_REFRESH,
                trackingHtml: String = "") {
        this.id = id
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

    fun getContent(): AdContent {
        return AdContent.createAddToListContent(this)
    }

    val zoneId: String
        get() = impressionId.split(":").first()
}