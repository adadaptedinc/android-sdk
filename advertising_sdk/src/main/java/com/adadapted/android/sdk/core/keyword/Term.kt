package com.adadapted.android.sdk.core.keyword

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Term(
    @SerialName("term_id")
    val termId: String,
    @SerialName("term")
    val searchTerm: String,
    val replacement: String,
    val icon: String,
    val tagline: String,
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