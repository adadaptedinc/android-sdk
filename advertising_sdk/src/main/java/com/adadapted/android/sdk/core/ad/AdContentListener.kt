package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.atl.AddToListContent

interface AdContentListener {
    fun onContentAvailable(zoneId: String, content: AddToListContent)
}