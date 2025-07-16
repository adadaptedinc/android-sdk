package com.adadapted.android.sdk.core.keyword

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class KeywordResponse(
    val data: InterceptData,
    val success: Boolean
)

@Serializable
data class InterceptData(
    @SerialName("search_id") val searchId: String = "",
    val terms: List<InterceptTerm> = listOf()
) {
    fun getSortedTerms(): List<InterceptTerm> {
        return terms.sortedWith(Comparator(InterceptTerm::compareTo))
    }
}

@Serializable
data class InterceptTerm(
    @SerialName("term_id") val termId: String,
    val term: String,
    val replacement: String,
    val priority: Int
) {
    operator fun compareTo(a2: InterceptTerm): Int {
        if (priority == a2.priority) {
            return term.compareTo(a2.term)
        } else if (priority < a2.priority) {
            return -1
        }
        return 1
    }
}