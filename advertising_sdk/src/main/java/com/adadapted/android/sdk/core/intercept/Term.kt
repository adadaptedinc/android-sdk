package com.adadapted.android.sdk.core.intercept

import com.google.gson.annotations.SerializedName

class Term(
        @SerializedName("term_id")
        val termId: String,
        @SerializedName("term")
        val searchTerm: String,
        val replacement: String,
        val icon: String,
        val tagLine: String,
        private val priority: Int
) {

    operator fun compareTo(a2: Term): Int {
        if (priority == a2.priority) {
            return searchTerm.compareTo(a2.searchTerm)
        } else if (priority < a2.priority) {
            return -1
        }
        return 1
    }
}