package com.adadapted.android.sdk.core.intercept

import org.junit.Assert
import org.junit.Test
import java.util.Date

class InterceptEventTest {
    private val interceptEvent = InterceptEvent("searchId", "event", "inputTest", "termId", "term")

    @Test
    fun supersedes() {
        assert(interceptEvent.supersedes(InterceptEvent("searchId2", "event", "input", "termId", "term2")))
    }

    @Test
    fun interceptEventToString() {
        val expectedString = "InterceptEvent{" +
                "searchId='" + "searchId" + '\'' +
                ", createdAt=" + Date() +
                ", event='" + "event" + '\'' +
                ", userInput='" + "inputTest" + '\'' +
                ", termId='" + "termId" + '\'' +
                ", term='" + "term" + '\'' +
                '}'

        Assert.assertEquals(expectedString, interceptEvent.toString())
    }
}