package com.adadapted.android.sdk.core.intercept

class Intercept(val searchId: String = SEARCH_ID,
                val refreshTime: Long = REFRESH_TIME,
                val minMatchLength: Int = MIN_MATCH_LENGTH,
                val terms: List<Term> = ArrayList()) {

    companion object {
        private const val SEARCH_ID = "empty"
        private const val REFRESH_TIME: Long = 300
        private const val MIN_MATCH_LENGTH = 3
    }
}