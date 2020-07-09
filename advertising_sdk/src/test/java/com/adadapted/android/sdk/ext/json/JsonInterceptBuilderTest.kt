package com.adadapted.android.sdk.ext.json

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JsonInterceptBuilderTest {
    private var testJsonInterceptBuilder =JsonInterceptBuilder()
    private var testJsonObject = JSONObject()

    @Before
    fun setup() {
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
    fun buildAndParseIntercepts() {
        val intercept = testJsonInterceptBuilder.build(testJsonObject)
        assertEquals("term4", intercept.terms[0].termId)
        assertEquals("term2", intercept.terms[1].termId)
        assertEquals("term3", intercept.terms[2].termId)
    }
}