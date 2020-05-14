package com.adadapted.android.sdk.core.ad

import org.junit.Test

class AdActionTypeTest {
    @Test
    fun adActionTypeHandlesContent() {
        val contentType = "c"
        assert(AdActionType.handlesContent(contentType))
    }

    @Test
    fun adActionTypeHandlesContentPopup() {
        val contentType = "cp"
        assert(AdActionType.handlesContent(contentType))
    }

    @Test
    fun adActionTypeDoesNotHandleLink() {
        val contentType = "l"
        assert(!AdActionType.handlesContent(contentType))
    }
}