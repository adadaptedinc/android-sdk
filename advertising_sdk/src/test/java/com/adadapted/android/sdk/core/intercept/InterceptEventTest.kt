package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.keyword.InterceptEvent
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Test

class InterceptEventTest {
    private val interceptEvent = InterceptEvent("searchId", "event", "inputTest", "termId", "term")

    @Test
    fun supersedes() {
        assert(interceptEvent.supersedes(InterceptEvent("searchId2", "event", "input", "termId", "term2")))
    }

    //A default-valued property is compared against a freshly evaluated nowInSeconds() at encode
    //time, so without @EncodeDefault an event serialized in the second it was built ships with no
    //created_at at all. See AdEvent.createdAt
    @Test
    fun createdAtSurvivesBeingSerializedInTheSecondTheEventWasBuilt() {
        val event = InterceptEvent("searchId", "event", "inputTest", "termId", "term")

        val json = Json.encodeToString(InterceptEvent.serializer(), event)

        assertTrue(
            "created_at is missing from $json",
            json.contains("\"created_at\":${event.createdAt}")
        )
    }
}
