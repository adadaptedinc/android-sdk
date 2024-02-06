package com.adadapted.android.sdk.core.zone

import com.adadapted.android.sdk.core.view.DimensionConverter
import com.adadapted.android.sdk.core.view.Zone
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
}