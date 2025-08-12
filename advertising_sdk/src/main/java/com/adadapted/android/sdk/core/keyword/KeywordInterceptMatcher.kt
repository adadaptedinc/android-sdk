package com.adadapted.android.sdk.core.keyword

import com.adadapted.android.sdk.core.interfaces.InterceptListener
import com.adadapted.android.sdk.core.session.SessionClient

object KeywordInterceptMatcher : InterceptListener {
    private var intercept: InterceptData = InterceptData()
    private var mLoaded = false
    private const val MIN_MATCH_LENGTH = 3

    private fun matchKeyword(constraint: CharSequence): Set<Suggestion> {
        val suggestions: MutableSet<Suggestion> = HashSet()
        val input = constraint.toString()
        if (!isReadyToMatch(input)) {
            return suggestions
        }
        for (interceptTerm in intercept.getSortedTerms()) {
            if (interceptTerm.term.startsWith(input, ignoreCase = true)) {
                fileTerm(interceptTerm, input, suggestions)
            }
        }
        if (suggestions.isEmpty()) {
            SuggestionTracker.suggestionNotMatched(intercept.searchId, constraint.toString())
        }
        return suggestions
    }

    private fun fileTerm(interceptTerm: InterceptTerm?, input: String, suggestions: MutableSet<Suggestion>) {
        if (interceptTerm != null) {
            suggestions.add(Suggestion(intercept.searchId, interceptTerm))
            SuggestionTracker.suggestionMatched(
                intercept.searchId,
                interceptTerm.termId,
                interceptTerm.term,
                interceptTerm.replacement,
                input
            )
        }
    }

    private fun isReadyToMatch(input: String?): Boolean {
        return isLoaded && input != null && input.length >= MIN_MATCH_LENGTH
    }

    private val isLoaded: Boolean
        get() {
            return mLoaded
        }

    override fun onKeywordInterceptInitialized(intercept: InterceptData) {
        KeywordInterceptMatcher.intercept = intercept
        mLoaded = true
    }

    fun match(constraint: CharSequence): Set<Suggestion> {
        return matchKeyword(constraint)
    }

    init {
        InterceptClient.initialize(SessionClient.getSessionId(), this)
    }
}
