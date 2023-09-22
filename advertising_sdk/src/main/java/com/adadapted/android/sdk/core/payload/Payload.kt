package com.adadapted.android.sdk.core.payload

import com.adadapted.android.sdk.core.atl.AddToListItem
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Payload(
    @SerialName("payload_id")
    val payloadId: String = "",
    @SerialName("payload_message")
    val payloadMessage: String = "",
    @SerialName("payload_image")
    val payloadImage: String = "",
    @SerialName("campaign_id")
    val campaignId: String = "",
    @SerialName("app_id")
    val appId: String = "",
    @SerialName("expire_seconds")
    val expireSeconds: Int = 0,
    @SerialName("detailed_list_items")
    val detailedListItems: List<AddToListItem> = listOf()
)