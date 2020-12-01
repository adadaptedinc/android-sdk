package com.adadapted.android.sdk.core.zone

import com.adadapted.android.sdk.core.common.Dimension
import com.adadapted.android.sdk.core.common.DimensionConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class ZoneTest {
    @Before
    fun setup() {
        DimensionConverter.createInstance(scale = 1f)
    }


    @Test
    fun emptyZone() {
        val zone = Zone()
        assertEquals("", zone.id)
        assertEquals(2, zone.dimensions.size.toLong())
        assertEquals(0, zone.ads.size.toLong())
        assertFalse(zone.hasAds())
    }

    @Test
    fun dimensions() {
        val zone = Zone("test_zone")
        zone.portHeight = 1
        zone.portWidth = 2
        zone.landHeight = 3
        zone.landWidth = 4
        zone.ads = listOf()
        assertEquals(1, zone.dimensions[Dimension.Orientation.PORT]?.height)
        assertEquals(2, zone.dimensions[Dimension.Orientation.PORT]?.width)
        assertEquals(3, zone.dimensions[Dimension.Orientation.LAND]?.height)
        assertEquals(4, zone.dimensions[Dimension.Orientation.LAND]?.width)
        assertEquals("test_zone", zone.id)
    }
}