package com.adadapted.android.sdk.core.adapter

import com.adadapted.android.sdk.core.keyword.InterceptTerm
import com.adadapted.android.sdk.core.keyword.Suggestion
import org.junit.Test

class SuggestionTest {

    @Test
    fun suggestionIsPresented() {
        val testSuggestion = getTestSuggestion()
        testSuggestion.presented()
        assert(testSuggestion.presented)
    }

    @Test
    fun suggestionIsSelected() {
        val testSuggestion = getTestSuggestion()
        testSuggestion.selected()
        assert(testSuggestion.selected)
    }

    private fun getTestSuggestion(): Suggestion {
        return Suggestion("searchId", InterceptTerm("testTermId", "testTerm", "testReplacement", 0))
    }
}