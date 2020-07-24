package com.adadapted.android.sdk.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class DimensionConverterTest {
    @Test
    fun convertDpToPx() {
        val dc = DimensionConverter(2f)
        val converted = dc.convertDpToPx(5)

        assertEquals(10, converted)
    }

    @Test
    fun convertDpToPxLessThanZero() {
        val dc = DimensionConverter(2f)
        val converted = dc.convertDpToPx(-1)

        assertEquals(-1, converted)
    }

    @Test
    fun dimensionIsCorrect() {
        val dimension = Dimension(2, 3)
        assertEquals(2, dimension.height)
        assertEquals(3, dimension.width)
        assertEquals("land", Dimension.Orientation.LAND)
        assertEquals("port", Dimension.Orientation.PORT)
    }
}