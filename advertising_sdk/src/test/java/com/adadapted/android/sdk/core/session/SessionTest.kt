package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.zone.Zone
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date

class SessionTest {
    @Test
    fun emptySessionCreated() {
        val deviceInfo = DeviceInfo.empty()
        val session = Session()
        Assert.assertEquals("", session.id)
    }

    @Test
    fun constructSession() {
        val deviceInfo = DeviceInfo.empty()
        val session = Session()
        val constructedSession = Session(session, mapOf())

        assertEquals(constructedSession.id, session.id)
    }

    @Test
    fun sessionHasCampaigns() {
        assert(buildTestSession().hasActiveCampaigns())
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
        session.setZones(zones)

        assert(session.getZone("testZone").id == "zoneId")
    }

    @Test
    fun sessionWillNotServeAds() {
        assertFalse(buildTestSession().willNotServeAds())
    }

    @Test
    fun expirationDateConversionIsCorrect() {
        val expireDate = 12345L
        assert(Session.convertExpirationToDate(expireDate) == Date(expireDate * 1000))
    }

    fun buildTestSession(): Session {
        return Session("testId", willServeAds = true, hasAds = true, refreshTime = 1L, expiration = Instant.now().epochSecond)
    }
}