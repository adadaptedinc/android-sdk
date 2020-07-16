package com.adadapted.android.sdk.ui.adapter

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.intercept.Intercept
import com.adadapted.android.sdk.core.intercept.InterceptClient
import com.adadapted.android.sdk.core.intercept.Term
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import com.adadapted.android.sdk.ui.model.Suggestion
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class KeywordInterceptMatcher : SessionListener(), InterceptClient.Listener {
    private val interceptLock: Lock = ReentrantLock()
    private var intercept: Intercept = Intercept()
    private var mLoaded = false

    private class Matcher internal constructor() {
        var found = false
        var input = ""
        var term: Term? = null
    }

    fun match(constraint: CharSequence): Set<Suggestion> {
        val suggestions: MutableSet<Suggestion> = HashSet()
        interceptLock.lock()
        try {
            val input = constraint.toString()
            if (!shouldCheckConstraint(input)) {
                return suggestions
            }
            val matcher = Matcher()
            for (term in intercept.terms) {
                if (term.term.startsWith(input, ignoreCase = true)) {
                    fileTerm(term, constraint.toString(), suggestions)
                    break
                } else if (term.term.contains(input, ignoreCase = true)) {
                    matcher.found = true
                    matcher.input = input
                    matcher.term = term
                }
            }
            if (suggestions.isEmpty()) {
                if (matcher.found) {
                    fileTerm(matcher.term, matcher.input, suggestions)
                } else {
                    SuggestionTracker.suggestionNotMatched(intercept.searchId, constraint.toString())
                }
            }
        } finally {
            interceptLock.unlock()
        }
        return suggestions
    }

    private fun fileTerm(term: Term?, input: String, suggestions: MutableSet<Suggestion>) {
        if (term != null) {
            suggestions.add(Suggestion(intercept.searchId, term))
            SuggestionTracker.suggestionMatched(
                    intercept.searchId,
                    term.termId,
                    term.term,
                    term.replacement,
                    input
            )
        }
    }

    private fun shouldCheckConstraint(input: String?): Boolean {
        return isLoaded && input != null && input.length >= intercept.minMatchLength
    }

    private val isLoaded: Boolean
        get() {
            interceptLock.lock()
            return try {
                mLoaded
            } finally {
                interceptLock.unlock()
            }
        }

    override fun onKeywordInterceptInitialized(intercept: Intercept) {
        AppEventClient.getInstance().trackSdkEvent(EventStrings.KI_INITIALIZED)
        interceptLock.lock()
        try {
            this.intercept = intercept
            mLoaded = true
        } finally {
            interceptLock.unlock()
        }
    }

    override fun onSessionAvailable(session: Session) {
        if (session.id.isNotEmpty()) {
            InterceptClient.getInstance().initialize(session, this)
        }
    }

    override fun onAdsAvailable(session: Session) {
        if (session.id.isNotEmpty()) {
            InterceptClient.getInstance().initialize(session, this)
        }
    }

    init {
        SessionClient.getInstance().addListener(this)
    }
}