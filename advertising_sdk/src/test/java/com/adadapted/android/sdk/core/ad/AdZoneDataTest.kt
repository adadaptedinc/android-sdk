package com.adadapted.android.sdk.core.ad

import android.util.DisplayMetrics
import com.adadapted.android.sdk.core.network.HttpConnector
import com.adadapted.android.sdk.core.view.DimensionConverter
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
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

    //The shape the server actually serves a no-fill as: every ad field present but blank, with the
    //refresh time still set. Blank has to read as "no ad" while the backoff survives intact, since
    //that pairing is the whole point of a no-fill.
    @Test
    fun `no fill decodes as an empty ad carrying the served refresh`() {
        val json = """
        {
            "data": {
                "ad": {
                    "id": "",
                    "impression_id": "",
                    "creative_url": "",
                    "action_type": "",
                    "action_path": "",
                    "payload": { "detailed_list_items": [] },
                    "refresh_time": 300
                },
                "port_height": 0,
                "port_width": 0
            },
            "success": true
        }
        """

        val response = HttpConnector.jsonParser.decodeFromString<AdZoneResponse>(json)

        assertTrue("A no-fill is a successful response", response.success)
        assertFalse(
            "A blank ad should read as no-fill, so the zone reports no ads",
            response.data.hasAd()
        )
        assertEquals(
            "The served backoff should survive an otherwise empty ad",
            300L,
            response.data.ad.refreshTime
        )
        assertEquals(
            "The zone timer should arm on 300s rather than the 60s default",
            300L,
            response.data.ad.refreshTimeOrDefault
        )
    }

    //A no-fill carries nothing, so the server can leave the zone dimensions off it. Decoding has to
    //survive that: a throw here is indistinguishable from a failed request, so the zone would
    //report no ads at all and lose the backoff the no-fill was carrying.
    @Test
    fun `no fill decodes when the dimensions are omitted`() {
        val json = """{"data": {"ad": {"refresh_time": 300}}, "success": true}"""

        val response = HttpConnector.jsonParser.decodeFromString<AdZoneResponse>(json)

        assertTrue(response.success)
        assertFalse("An ad with no id is a no-fill", response.data.hasAd())
        assertEquals(
            "The served backoff should survive decoding",
            300L,
            response.data.ad.refreshTime
        )
    }

    @Test
    fun `no fill decodes when the ad is omitted or null`() {
        listOf(
            """{"data": {"port_height": 0, "port_width": 0}, "success": true}""",
            """{"data": {"ad": null, "port_height": 0, "port_width": 0}, "success": true}""",
            """{"data": {"ad": null, "port_height": null, "port_width": null}, "success": true}""",
            """{"data": {}, "success": true}""",
            """{"data": null, "success": true}""",
            """{"success": true}""",
        ).forEach { json ->
            val response = HttpConnector.jsonParser.decodeFromString<AdZoneResponse>(json)

            assertTrue("Should decode as a valid response: $json", response.success)
            assertFalse("Should read as a no-fill rather than throwing: $json", response.data.hasAd())
        }
    }
}
