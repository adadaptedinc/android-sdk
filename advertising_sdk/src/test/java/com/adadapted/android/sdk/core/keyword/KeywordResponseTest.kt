package com.adadapted.android.sdk.core.keyword

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InterceptDataTest {

    @Test
    fun `getSortedTerms sorts by priority then term name`() {
        val terms = listOf(
            InterceptTerm("id3", "cherry", "r3", 2),
            InterceptTerm("id1", "banana", "r1", 1),
            InterceptTerm("id2", "apple", "r2", 1)
        )
        val data = InterceptData("search1", terms)

        val sorted = data.getSortedTerms()

        assertEquals("apple", sorted[0].term)
        assertEquals("banana", sorted[1].term)
        assertEquals("cherry", sorted[2].term)
    }

    @Test
    fun `getSortedTerms returns empty list when no terms`() {
        val data = InterceptData()
        assertTrue(data.getSortedTerms().isEmpty())
    }

    @Test
    fun `default InterceptData has empty searchId`() {
        val data = InterceptData()
        assertEquals("", data.searchId)
        assertTrue(data.terms.isEmpty())
    }
}

class InterceptTermTest {

    @Test
    fun `compareTo returns negative when lower priority`() {
        val t1 = InterceptTerm("id1", "a", "r1", 1)
        val t2 = InterceptTerm("id2", "b", "r2", 2)

        assertTrue(t1.compareTo(t2) < 0)
    }

    @Test
    fun `compareTo returns positive when higher priority`() {
        val t1 = InterceptTerm("id1", "a", "r1", 3)
        val t2 = InterceptTerm("id2", "b", "r2", 1)

        assertTrue(t1.compareTo(t2) > 0)
    }

    @Test
    fun `compareTo compares term name when same priority`() {
        val t1 = InterceptTerm("id1", "apple", "r1", 1)
        val t2 = InterceptTerm("id2", "banana", "r2", 1)

        assertTrue(t1.compareTo(t2) < 0)
        assertTrue(t2.compareTo(t1) > 0)
    }

    @Test
    fun `compareTo returns zero for identical terms`() {
        val t1 = InterceptTerm("id1", "same", "r1", 1)
        val t2 = InterceptTerm("id2", "same", "r2", 1)

        assertEquals(0, t1.compareTo(t2))
    }
}
