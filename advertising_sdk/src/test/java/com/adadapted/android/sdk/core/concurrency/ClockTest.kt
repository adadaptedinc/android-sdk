package com.adadapted.android.sdk.core.concurrency

import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.SdkEvent
import com.adadapted.android.sdk.core.keyword.InterceptEvent
import com.adadapted.android.sdk.core.payload.PayloadEvent
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockTest {
    //Any epoch value inside this window is seconds. Milliseconds would be ~1000x above the ceiling,
    //so these bounds fail loudly if a timestamp ever goes back to milliseconds
    private val earliestPlausibleSeconds = 1_700_000_000L //Nov 2023
    private val latestPlausibleSeconds = 4_000_000_000L //Oct 2096

    private fun assertIsEpochSeconds(label: String, timestamp: Long) {
        assertTrue(
            "$label should be epoch seconds but was $timestamp",
            timestamp in earliestPlausibleSeconds..latestPlausibleSeconds
        )
    }

    @Test
    fun nowInSecondsReturnsEpochSecondsNotMilliseconds() {
        assertIsEpochSeconds("nowInSeconds()", nowInSeconds())
    }

    @Test
    fun eventTimestampsSentToTheServerAreInSeconds() {
        //These defaults are the values the servers receive, so the unit is part of the wire format
        assertIsEpochSeconds("SdkEvent.timeStamp", SdkEvent("type", "name", params = emptyMap()).timeStamp)
        assertIsEpochSeconds("AdEvent.createdAt", AdEvent("adId", "zoneId", "impressionId", "type").createdAt)
        assertIsEpochSeconds("InterceptEvent.createdAt", InterceptEvent("searchId", "type", "input", "termId", "term").createdAt)
        assertIsEpochSeconds("PayloadEvent.timestamp", PayloadEvent("payloadId", "status").timestamp)
    }
}
