package com.adadapted.android.sdk.ui.messaging

import com.adadapted.android.sdk.core.atl.AddToListContent

interface AdContentListener {
    fun onContentAvailable(zoneId: String, content: AddToListContent)
}