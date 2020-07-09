package com.adadapted.android.sdk.core.zone

import com.adadapted.android.sdk.core.common.Dimension
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ZoneTest {
    @Test
    fun emptyZone() {
        val zone = Zone()
        assertEquals("", zone.id)
        assertEquals(0, zone.dimensions.size.toLong())
        assertEquals(0, zone.ads.size.toLong())
        assertFalse(zone.hasAds())
    }

    @Test
    fun dimensions() {
        val zone = Zone("test_zone")
        zone.setDimension(Dimension.Orientation.PORT, Dimension(1, 2))
        zone.setDimension(Dimension.Orientation.LAND, Dimension(3, 4))
        zone.ads = listOf()
        assertEquals(1, zone.dimensions[Dimension.Orientation.PORT]?.height)
        assertEquals(2, zone.dimensions[Dimension.Orientation.PORT]?.width)
        assertEquals(3, zone.dimensions[Dimension.Orientation.LAND]?.height)
        assertEquals(4, zone.dimensions[Dimension.Orientation.LAND]?.width)
        assertEquals("test_zone", zone.id)
    }
}