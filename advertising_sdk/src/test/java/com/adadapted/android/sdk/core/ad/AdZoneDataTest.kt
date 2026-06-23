package com.adadapted.android.sdk.core.ad

import android.util.DisplayMetrics
import com.adadapted.android.sdk.core.view.DimensionConverter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AdZoneDataTest {

    @Before
    fun setup() {
        val mockDisplayMetrics = DisplayMetrics().apply {
            widthPixels = 1080
            heightPixels = 1920
            density = 3.0f
        }
        DimensionConverter.createInstance(0f, mockDisplayMetrics)
    }

    @Test
    fun `hasAd returns true when ad id is not empty`() {
        val data = AdZoneData(ad = Ad(id = "ad123"))
        assertTrue(data.hasAd())
    }

    @Test
    fun `hasAd returns false when ad id is empty`() {
        val data = AdZoneData()
        assertFalse(data.hasAd())
    }

    @Test
    fun `default AdZoneData has empty type`() {
        val data = AdZoneData()
        assertTrue(data.type.isEmpty())
    }

    @Test
    fun `AdZoneResponse defaults to not successful`() {
        val response = AdZoneResponse()
        assertFalse(response.success)
        assertFalse(response.data.hasAd())
    }

    @Test
    fun `AdZoneResponse with success true`() {
        val response = AdZoneResponse(
            data = AdZoneData(ad = Ad(id = "ad1")),
            success = true
        )
        assertTrue(response.success)
        assertTrue(response.data.hasAd())
    }
}
