package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.keyword.InterceptTerm
import org.junit.Assert
import org.junit.Test

class TermTest {
    private val testTerm = InterceptTerm("termId", "term", "replacement", 1)

    @Test
    fun compareToPriority() {
        Assert.assertEquals(1, testTerm.compareTo(InterceptTerm("termId2", "newTerm", "replacement2", 0)))
    }
}