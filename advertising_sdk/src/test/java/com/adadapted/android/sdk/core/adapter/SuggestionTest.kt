package com.adadapted.android.sdk.core.adapter

import android.os.Parcel
import com.adadapted.android.sdk.core.intercept.Term
import com.adadapted.android.sdk.ui.model.Suggestion
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SuggestionTest {

    @Test
    fun suggestionIsCreatedFromParcel() {
        val testSuggestion = getTestSuggestion()
        val parcel = Parcel.obtain()

        testSuggestion.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val suggestionFromParcel = Suggestion.createFromParcel(parcel)

        assertEquals("searchId", suggestionFromParcel.searchId)
        assertEquals("testTermId", suggestionFromParcel.termId)
        assertEquals("testReplacement", suggestionFromParcel.name)
        assertEquals("testIcon", suggestionFromParcel.icon)
        assertEquals("testTagLine", suggestionFromParcel.tagLine)
        assertEquals(false, suggestionFromParcel.presented)
        assertEquals(false, suggestionFromParcel.selected)
    }

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