package com.adadapted.android.sdk.core.common

import android.util.DisplayMetrics
import com.adadapted.android.sdk.core.view.Dimension
import com.adadapted.android.sdk.core.view.DimensionConverter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DimensionConverterTest {
    @Before
    fun setup() {
        val mockDisplayMetrics = DisplayMetrics().apply {
            widthPixels = 1080
            heightPixels = 1920
            density = 3.0f
        }

        DimensionConverter.createInstance(2f, mockDisplayMetrics)
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