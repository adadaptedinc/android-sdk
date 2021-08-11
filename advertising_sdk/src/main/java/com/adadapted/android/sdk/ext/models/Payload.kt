package com.adadapted.android.sdk.ext.models

import com.adadapted.android.sdk.core.atl.AddToListItem
import com.google.gson.annotations.SerializedName

data class Payload(
        @SerializedName("detailed_list_items")
        val detailedListItems: List<AddToListItem>
)