package com.adadapted.android.sdk.core.keyword

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Intercept(
    @SerialName("search_id")
    val searchId: String = SEARCH_ID,

    @SerialName("refresh_time")
    val refreshTime: Long = REFRESH_TIME,

    @SerialName("min_match_length")
    val minMatchLength: Int = MIN_MATCH_LENGTH,

    private val terms: List<Term> = ArrayList()
) {
    fun getTerms(): List<Term> {
        return terms.sortedWith(Comparator(Term::compareTo))
    }

    companion object {
        private const val SEARCH_ID = "empty"
        private const val REFRESH_TIME: Long = 300
        private const val MIN_MATCH_LENGTH = 3
    }
}
