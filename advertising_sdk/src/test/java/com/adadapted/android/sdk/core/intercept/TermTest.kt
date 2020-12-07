package com.adadapted.android.sdk.core.intercept

import org.junit.Assert
import org.junit.Test

class TermTest {
    private val testTerm = Term("termId", "term", "replacement", "icon", "tagLine", 1)

    @Test
    fun compareToPriority() {
        Assert.assertEquals(1, testTerm.compareTo(Term("termId2", "newTerm", "replacement2", "icon", "tagLane", 0)))
    }
}