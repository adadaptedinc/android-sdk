package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.constants.AddToListTypes
import com.adadapted.android.sdk.core.atl.AddItContent.AddItSources.DEEPLINK
import com.adadapted.android.sdk.core.atl.AddItContent.AddItSources.PAYLOAD
import com.adadapted.android.sdk.core.atl.AddToListContent.Sources.OUT_OF_APP
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.payload.PayloadResponse

object AddItContentParser {
    fun generateAddItContentFromPayloads(payloadResponse: PayloadResponse): List<AddItContent> {
        val listOfAddItContentToReturn = payloadResponse.payloads.map {
            AddItContent(
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
        return listOfAddItContentToReturn
    }

    fun generateAddItContentFromDeeplink(payload: Payload): AddItContent {
        return AddItContent(
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