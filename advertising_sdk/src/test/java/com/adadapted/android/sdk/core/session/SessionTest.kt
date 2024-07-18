package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.view.Zone
import junit.framework.Assert.assertFalse
import org.junit.Assert
import org.junit.Test
import java.util.Date

class SessionTest {
    @Test
    fun emptySessionCreated() {
        DeviceInfo.empty()
        val session = Session()
        Assert.assertEquals("", session.id)
    }

    @Test
    fun sessionHasCampaigns() {
        assert(buildTestSession().hasActiveCampaigns())
    }

    @Test
    fun sessionDoesNotHaveZoneAds() {
        assert(buildTestSession().getZonesWithAds().isEmpty())
    }

    @Test
    fun sessionHasZoneAds() {
        val session = buildTestSession()
        val zones = mapOf<String, Zone>().plus(
            Pair(
                "testZone",
                Zone(
                    "zoneId",
                    listOf(
                        Ad(
                            "testAdId",
                            "impId",
                            "url",
                            "action",
                            "actionPath",
                            Payload(detailedListItems = listOf())
                        )
                    )
                )
            )
        )
        session.updateZones(zones)
        assert(session.getZonesWithAds().isNotEmpty())
    }

    @Test
    fun sessionIsExpired() {
        assert(buildTestSession().hasExpired())
    }

    @Test
    fun sessionSetsAndRetrievesZones() {
        val session = buildTestSession()
        assert(session.getZone("testZone").id == "")

        val zones = mapOf<String, Zone>().plus(Pair("testZone", Zone("zoneId", listOf())))
        session.updateZones(zones)

        assert(session.getZone("testZone").id == "zoneId")
    }

    @Test
    fun sessionWillNotServeAds() {
        assertFalse(buildTestSession().willNotServeAds())
    }

    fun buildTestSession(): Session {
        return Session("testId", willServeAds = true, hasAds = true, refreshTime = 1L, expiration = Date().time / 1000 - 1)
    }
}