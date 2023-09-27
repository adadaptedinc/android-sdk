package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.keyword.InterceptEvent
import org.junit.Test

class InterceptEventTest {
    private val interceptEvent = InterceptEvent("searchId", "event", "inputTest", "termId", "term")

    @Test
    fun supersedes() {
        assert(interceptEvent.supersedes(InterceptEvent("searchId2", "event", "input", "termId", "term2")))
    }
}