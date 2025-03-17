package com.adadapted.android.sdk.core.keyword

import com.adadapted.android.sdk.core.interfaces.InterceptListener
import com.adadapted.android.sdk.core.session.NewSessionClient

object KeywordInterceptMatcher : InterceptListener {
    private var intercept: Intercept = Intercept()
    private var mLoaded = false

    private fun matchKeyword(constraint: CharSequence): Set<Suggestion> {
        val suggestions: MutableSet<Suggestion> = HashSet()
        val input = constraint.toString()
        if (!isReadyToMatch(input)) {
            return suggestions
        }
        for (interceptTerm in intercept.getTerms()) {
            if (interceptTerm.searchTerm.startsWith(input, ignoreCase = true)) {
                fileTerm(interceptTerm, input, suggestions)
            }
        }
        if (suggestions.isEmpty()) {
            SuggestionTracker.suggestionNotMatched(intercept.searchId, constraint.toString())
        }
        return suggestions
    }

    private fun fileTerm(term: Term?, input: String, suggestions: MutableSet<Suggestion>) {
        if (term != null) {
            suggestions.add(Suggestion(intercept.searchId, term))
            SuggestionTracker.suggestionMatched(
                intercept.searchId,
                term.termId,
                term.searchTerm,
                term.replacement,
                input
            )
        }
    }

    private fun isReadyToMatch(input: String?): Boolean {
        return isLoaded && input != null && input.length >= intercept.minMatchLength
    }

    private val isLoaded: Boolean
        get() {
            return mLoaded
        }

    override fun onKeywordInterceptInitialized(intercept: Intercept) {
        KeywordInterceptMatcher.intercept = intercept
        mLoaded = true
    }

    fun match(constraint: CharSequence): Set<Suggestion> {
        return matchKeyword(constraint)
    }

    init {
        InterceptClient.initialize(NewSessionClient.getSessionId(), this)
    }
}
