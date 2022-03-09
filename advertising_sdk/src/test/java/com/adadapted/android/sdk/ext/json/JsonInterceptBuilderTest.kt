package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.event.TestAppEventSink
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class JsonInterceptBuilderTest {
    private var testJsonInterceptBuilder = JsonInterceptBuilder()
    private var testJsonObject = JSONObject()
    private var testAppEventSink = TestAppEventSink()
    private var testTransporter = TestCoroutineDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        DeviceInfoClient.createInstance(mock(), "", false, mock(), "", mock(), mock())
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(testAppEventSink, testTransporterScope)

        testJsonObject.put("search_id", "testSearchId")
        testJsonObject.put("refresh_time", "5")
        testJsonObject.put("min_match_length", "3")
        val testJsonArray = JSONArray()
        testJsonArray.put(JSONObject().put("term_id", "term1").put("term", "milk").put("replacement", "fancy milk").put("icon", "icon").put("tagline", "tag").put("priority", "1"))
        testJsonArray.put(JSONObject().put("term_id", "term2").put("term", "egg").put("replacement", "fancy eggs").put("icon", "icon").put("tagline", "tag").put("priority", "1"))
        testJsonArray.put(JSONObject().put("term_id", "term3").put("term", "eggs").put("replacement", "fake eggs").put("icon", "icon").put("tagline", "tag").put("priority", "1"))
        testJsonArray.put(JSONObject().put("term_id", "term4").put("term", "eggs").put("replacement", "rotten eggs").put("icon", "icon").put("tagline", "tag").put("priority", "0"))
        testJsonObject.put("terms", testJsonArray)
    }

    @Test
    fun buildAndParseBadJson() {
        val badJson = JSONObject()
        badJson.put("search_id", "badSearchId")
        badJson.put("refresh_time", 80)

        val intercept = testJsonInterceptBuilder.build(badJson)
        assertEquals(80L, intercept.refreshTime)
    }

    @Test
    fun buildParseInterceptWillFail() {
        val badJson = JSONObject()
        badJson.put("search_id", "badSearchId")
        badJson.put("refresh_time", Date())

        testJsonInterceptBuilder.build(badJson)

        AppEventClient.getInstance().onPublishEvents()
        assertEquals(EventStrings.KI_PAYLOAD_PARSE_FAILED, testAppEventSink.testErrors.first().code)
    }

    @Test
    fun buildAndParseIntercepts() {
        val intercept = testJsonInterceptBuilder.build(testJsonObject)
        assertEquals("term4", intercept.getTerms()[0].termId)
        assertEquals("term2", intercept.getTerms()[1].termId)
        assertEquals("term3", intercept.getTerms()[2].termId)
    }
}