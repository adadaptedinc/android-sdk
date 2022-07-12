package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import com.adadapted.android.sdk.ui.adapter.SuggestionTracker
import com.adadapted.android.sdk.ui.model.Suggestion
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class KeywordInterceptMatcher private constructor() : SessionListener(), InterceptClient.Listener {
    private val interceptLock: Lock = ReentrantLock()
    private var intercept: Intercept = Intercept()
    private var mLoaded = false

    private fun matchKeyword(constraint: CharSequence): Set<Suggestion> {
        val suggestions: MutableSet<Suggestion> = HashSet()
        interceptLock.lock()
        try {
            val input = constraint.toString()
            if (!shouldCheckConstraint(input)) {
                return suggestions
            }

            for (interceptTerm in intercept.getTerms()) {
                if (interceptTerm.searchTerm.startsWith(input, ignoreCase = true)) {
                    fileTerm(interceptTerm, constraint.toString(), suggestions)
                }
            }
            if (suggestions.isEmpty()) {
                SuggestionTracker.suggestionNotMatched(intercept.searchId, constraint.toString())
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
                term.searchTerm,
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

    companion object {
        private lateinit var instance: KeywordInterceptMatcher

        fun match(constraint: CharSequence): Set<Suggestion> {
            return if (this::instance.isInitialized) {
                instance.matchKeyword(constraint)
            } else {
                when {
                    SessionClient.hasInstance() -> {
                        instance = KeywordInterceptMatcher()
                        emptySet()
                    }
                    else -> {
                        return emptySet()
                    }
                }
            }
        }
    }

    init {
        SessionClient.getInstance().addListener(this)
    }
}