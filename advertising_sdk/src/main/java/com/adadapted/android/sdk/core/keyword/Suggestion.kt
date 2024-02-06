package com.adadapted.android.sdk.core.keyword

import com.adadapted.android.sdk.core.keyword.SuggestionTracker.suggestionPresented
import com.adadapted.android.sdk.core.keyword.SuggestionTracker.suggestionSelected

data class Suggestion(val searchId: String, private val term: Term) {
    val termId: String = term.termId
    val name: String = term.replacement
    val icon: String = term.icon
    val tagline: String = term.tagline
    var presented: Boolean
    var selected: Boolean

    init {
        presented = false
        selected = false
    }

    fun presented() {
        if (!presented) {
            presented = true
            suggestionPresented(searchId, termId, name)
        }
    }

    fun selected() {
        if (!selected) {
            selected = true
            suggestionSelected(searchId, termId, name)
        }
    }
}