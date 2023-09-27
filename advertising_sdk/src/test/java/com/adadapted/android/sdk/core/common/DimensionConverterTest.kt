package com.adadapted.android.sdk.core.common

import com.adadapted.android.sdk.core.view.Dimension
import com.adadapted.android.sdk.core.view.DimensionConverter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DimensionConverterTest {
    @Before
    fun setup() {
        DimensionConverter.createInstance(2f)
    }

    @Test
    fun convertDpToPx() {
        val converted = DimensionConverter.convertDpToPx(5)
        assertEquals(10, converted)
    }

    @Test
    fun convertDpToPxLessThanZero() {
        val converted = DimensionConverter.convertDpToPx(-1)
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

    @Test
    fun defaultDimensionIsCorrect() {
        val dimension = Dimension()
        assertEquals(0, dimension.height)
        assertEquals(0, dimension.width)
        assertEquals("land", Dimension.Orientation.LAND)
        assertEquals("port", Dimension.Orientation.PORT)
    }
}