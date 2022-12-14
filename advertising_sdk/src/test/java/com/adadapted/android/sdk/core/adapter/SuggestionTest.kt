package com.adadapted.android.sdk.core.adapter

import com.adadapted.android.sdk.core.intercept.Term
import com.adadapted.android.sdk.ui.model.Suggestion
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
        return Suggestion("searchId", Term("testTermId", "testTerm", "testReplacement", "testIcon", "testTagLine", 0))
    }
}