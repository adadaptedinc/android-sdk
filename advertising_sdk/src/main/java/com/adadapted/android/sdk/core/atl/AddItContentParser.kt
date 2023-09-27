package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.constants.AddToListTypes
import com.adadapted.android.sdk.core.atl.AdditContent.AdditSources.DEEPLINK
import com.adadapted.android.sdk.core.atl.AdditContent.AdditSources.PAYLOAD
import com.adadapted.android.sdk.core.atl.AddToListContent.Sources.OUT_OF_APP
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.payload.PayloadResponse

object AddItContentParser {
    fun generateAddItContentFromPayloads(payloadResponse: PayloadResponse): List<AdditContent> {
        val listOfAdditContentToReturn = payloadResponse.payloads.map {
            AdditContent(
                it.payloadId,
                it.payloadMessage,
                it.payloadImage,
                if (it.detailedListItems.count()>1) AddToListTypes.ADD_TO_LIST_ITEMS else AddToListTypes.ADD_TO_LIST_ITEM,
                OUT_OF_APP,
                PAYLOAD,
                it.detailedListItems
            )
        }
        //track errors
        return listOfAdditContentToReturn
    }

    fun generateAddItContentFromDeeplink(payload: Payload): AdditContent {
        return AdditContent(
            payload.payloadId,
            payload.payloadMessage,
            payload.payloadImage,
            if (payload.detailedListItems.count()>1) AddToListTypes.ADD_TO_LIST_ITEMS else AddToListTypes.ADD_TO_LIST_ITEM,
            OUT_OF_APP,
            DEEPLINK,
            payload.detailedListItems
        )
    }
}