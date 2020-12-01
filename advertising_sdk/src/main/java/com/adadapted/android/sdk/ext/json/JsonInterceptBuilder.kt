package com.adadapted.android.sdk.ext.json

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import com.adadapted.android.sdk.core.intercept.Intercept
import com.adadapted.android.sdk.core.intercept.Term
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class JsonInterceptBuilder {
    fun build(json: JSONObject?): Intercept {
        if (json == null) {
            return Intercept()
        }
        try {
            val searchId = if (json.has(SEARCH_ID)) json.getString(SEARCH_ID) else ""
            val refreshTime = if (json.has(REFRESH_TIME)) JsonHelper.tryGetLongFromJson(json, REFRESH_TIME) else 0L
            val minMatchLength = if (json.has(MIN_MATCH_LENGTH)) JsonHelper.tryGetIntFromJson(json, MIN_MATCH_LENGTH, 2) else 2
            val terms = if (json.has(TERMS)) json.getJSONArray(TERMS) else JSONArray()
            val intercepts = sortIntercepts(parseIntercepts(terms))
            return Intercept(searchId, refreshTime, minMatchLength, intercepts)
        } catch (ex: JSONException) {
            Log.w(LOGTAG, "Problem parsing JSON", ex)
            val params: MutableMap<String, String> = HashMap()
            params["error"] = ex.message ?: ""
            params["payload"] = json.toString()
            getInstance().trackError(EventStrings.KI_PAYLOAD_PARSE_FAILED, "Failed to parse KI payload for processing.", params)
        }
        return Intercept()
    }

    @Throws(JSONException::class)
    private fun parseIntercepts(json: JSONArray): List<Term> {
        val terms: MutableList<Term> = ArrayList()
        for (i in 0 until json.length()) {
            val termJson = json.getJSONObject(i)
            val termId = if (termJson.has(TERM_ID)) termJson.getString(TERM_ID) else ""
            val term = if (termJson.has(TERM)) termJson.getString(TERM) else ""
            val replacement = if (termJson.has(REPLACEMENT)) termJson.getString(REPLACEMENT) else ""
            val icon = if (termJson.has(ICON)) termJson.getString(ICON) else ""
            val tagLine = if (termJson.has(TAG_LINE)) termJson.getString(TAG_LINE) else ""
            val priority = if (termJson.has(PRIORITY)) termJson.getInt(PRIORITY) else 0
            terms.add(Term(termId, term, replacement, icon, tagLine, priority))
        }
        return terms
    }

    private fun sortIntercepts(terms: List<Term>): List<Term> {
        return terms.sortedWith(Comparator(Term::compareTo))
    }

    companion object {
        private val LOGTAG = JsonInterceptBuilder::class.java.name
        private const val SEARCH_ID = "search_id"
        private const val REFRESH_TIME = "refresh_time"
        private const val MIN_MATCH_LENGTH = "min_match_length"
        private const val TERMS = "terms"
        private const val TERM_ID = "term_id"
        private const val TERM = "term"
        private const val REPLACEMENT = "replacement"
        private const val ICON = "icon"
        private const val TAG_LINE = "tagline"
        private const val PRIORITY = "priority"
    }
}