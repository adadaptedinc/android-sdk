package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.AdEventTypes
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.network.HttpConnector
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertNotNull
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json

import org.junit.Before
import org.junit.Test

class AdTest {
    var testTransporter = UnconfinedTestDispatcher()
    val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, mock(), "", TestDeviceInfoExtractor(), mock())
        SessionClient.createOrResumeSession()
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onPublishEvents()
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun verifyAdEventStructure() {
        val adEventTypes = AdEventTypes
        val adEventImp = adEventTypes.IMPRESSION
        val adEventInv = adEventTypes.INVISIBLE_IMPRESSION
        val adEventInt = adEventTypes.INTERACTION
        val adEventPop = adEventTypes.POPUP_BEGIN

        assertEquals(AdEventTypes.IMPRESSION, adEventImp)
        assertEquals(AdEventTypes.INVISIBLE_IMPRESSION, adEventInv)
        assertEquals(AdEventTypes.INTERACTION, adEventInt)
        assertEquals(AdEventTypes.POPUP_BEGIN, adEventPop)
    }

    @Test
    fun verifyAdEventCreation() {
        val testAdEvent = AdEvent("adId", "zoneId", "impressionId", AdEventTypes.IMPRESSION)
        assertEquals("impressionId", testAdEvent.impressionId)
    }

    //The server reads snake_case keys. It resolves an ad event's zone from the impression id, so a
    //camelCase zone key went unnoticed until zone events started shipping without an impression id
    @Test
    fun adEventIsSerializedWithTheKeysTheServerReads() {
        val json = Json.encodeToString(
            AdEvent.serializer(),
            AdEvent.forZone("102691", AdEventTypes.ZONE_MOUNTED).copy(createdAt = 1)
        )

        assertEquals(
            """{"ad_id":"","zone_id":"102691","impression_id":"","event_type":"zone_mounted","created_at":1}""",
            json
        )
    }

    @Test
    fun defaultAdIsCreated() {
        val mockAd = Ad()

        assert(mockAd.id.isEmpty())
        assert(mockAd.zoneId.isEmpty())
        assert(mockAd.impressionId.isEmpty())
        assert(mockAd.url.isEmpty())
        assert(mockAd.actionType.isEmpty())
        assert(mockAd.actionPath?.isEmpty() == true)
        assertNotNull(mockAd.payload)
        assert(mockAd.isEmpty)
    }

    @Test
    fun theRefreshFloorIsFifteenSeconds() {
        //Pinned so a change to the floor is a deliberate edit here, not a silent one
        assertEquals(15L, Ad.MINIMUM_REFRESH_TIME_SECONDS)
        assertEquals(15L, Ad(refreshTime = 15).refreshTimeOrDefault)
    }

    @Test
    fun aServedRefreshTimeBelowTheFloorClampsUpToTheFloor() {
        assertEquals(15L, Ad(refreshTime = 1).refreshTimeOrDefault)
        assertEquals(15L, Ad(refreshTime = 10).refreshTimeOrDefault)
        assertEquals(15L, Ad(refreshTime = 14).refreshTimeOrDefault)
        assertEquals(15L, Ad(refreshTime = Ad.MINIMUM_REFRESH_TIME_SECONDS - 1).refreshTimeOrDefault)
    }

    @Test
    fun theServerSuppliedRefreshTimeInSecondsIsUsedWhenItMeetsTheFloor() {
        //No upper bound, so a refresh slower than the default is not capped back down to it
        assertEquals(15L, Ad(refreshTime = Ad.MINIMUM_REFRESH_TIME_SECONDS).refreshTimeOrDefault)
        assertEquals(30L, Ad(refreshTime = 30).refreshTimeOrDefault)
        assertEquals(70L, Ad(refreshTime = 70).refreshTimeOrDefault)
        assertEquals(300L, Ad(refreshTime = 300).refreshTimeOrDefault)
    }

    @Test
    fun defaultRefreshTimeIsUsedWhenTheServerRefreshTimeIsUnusable() {
        //Absent or zero means the server said nothing; a negative cannot be a real instruction
        assertEquals(Config.DEFAULT_AD_REFRESH_SECONDS, Ad(refreshTime = 0).refreshTimeOrDefault)
        assertEquals(Config.DEFAULT_AD_REFRESH_SECONDS, Ad(refreshTime = -1).refreshTimeOrDefault)
        assertEquals(Config.DEFAULT_AD_REFRESH_SECONDS, Ad(refreshTime = -30).refreshTimeOrDefault)
        assertEquals(Config.DEFAULT_AD_REFRESH_SECONDS, Ad().refreshTimeOrDefault) //None supplied
    }

    @Test
    fun onlyAServedRefreshTimeTheSdkWillNotHonorCountsAsRejected() {
        assertTrue(Ad(refreshTime = 1).refreshTimeWasRejected) //Clamped up to the floor
        assertTrue(Ad(refreshTime = -30).refreshTimeWasRejected) //Fell back to the default
        assertFalse(Ad(refreshTime = Ad.MINIMUM_REFRESH_TIME_SECONDS).refreshTimeWasRejected)
        assertFalse(Ad(refreshTime = 70).refreshTimeWasRejected)
        assertFalse(Ad().refreshTimeWasRejected) //None supplied is expected, not a rejection
    }

    @Test
    fun refreshTimeIsParsedFromTheServerResponse() {
        val parsedAd = HttpConnector.jsonParser
            .decodeFromString<Ad>("""{"id":"TestAdId","refresh_time":90}""")

        assertEquals(90L, parsedAd.refreshTime)
        assertEquals(90L, parsedAd.refreshTimeOrDefault)
    }

    @Test
    fun aQuotedOrDecimalRefreshTimeIsStillHonored() {
        val quoted = HttpConnector.jsonParser
            .decodeFromString<Ad>("""{"id":"TestAdId","refresh_time":"90"}""")
        val decimal = HttpConnector.jsonParser
            .decodeFromString<Ad>("""{"id":"TestAdId","refresh_time":90.0}""")

        assertEquals(90L, quoted.refreshTimeOrDefault)
        assertEquals(90L, decimal.refreshTimeOrDefault)
    }

    @Test
    fun defaultRefreshTimeIsUsedWhenTheServerSendsNoUsableRefreshTime() {
        listOf(
            """{"id":"TestAdId"}""", //Omitted entirely
            """{"id":"TestAdId","refresh_time":null}""",
            """{"id":"TestAdId","refresh_time":""}""",
            """{"id":"TestAdId","refresh_time":" "}""",
            """{"id":"TestAdId","refresh_time":"abc"}""",
            """{"id":"TestAdId","refresh_time":{}}""",
            """{"id":"TestAdId","refresh_time":[]}""",
            """{"id":"TestAdId","refresh_time":true}""",
        ).forEach { json ->
            val parsedAd = HttpConnector.jsonParser.decodeFromString<Ad>(json)

            assertEquals(
                "$json should fall back to the default refresh",
                Config.DEFAULT_AD_REFRESH_SECONDS,
                parsedAd.refreshTimeOrDefault
            )
        }
    }

    //A no-fill may carry only the backoff, so a sparse Ad has to decode rather than fail the response
    @Test
    fun aSparseAdCarryingOnlyARefreshTimeStillDecodes() {
        val parsedAd = HttpConnector.jsonParser.decodeFromString<Ad>("""{"refresh_time":300}""")

        assertEquals(300L, parsedAd.refreshTimeOrDefault)
        assertTrue(parsedAd.isEmpty)
        assertEquals("", parsedAd.id)
        assertEquals("", parsedAd.impressionId)
    }

    @Test
    fun aSparseNoFillResponseStillDecodes() {
        val parsedResponse = HttpConnector.jsonParser.decodeFromString<AdZoneResponse>(
            """{"success":true,"data":{"port_height":50,"port_width":320,"ad":{"refresh_time":300}}}"""
        )

        assertEquals(true, parsedResponse.success)
        assertEquals(50, parsedResponse.data.portHeight)
        assertTrue(parsedResponse.data.ad.isEmpty)
        assertEquals(300L, parsedResponse.data.ad.refreshTimeOrDefault)
    }

    @Test
    fun aBadRefreshTimeDoesNotFailTheRestOfTheAdResponse() {
        val parsedResponse = HttpConnector.jsonParser.decodeFromString<AdZoneResponse>(
            """{"success":true,"data":{"port_height":50,"ad":{"id":"TestAdId","refresh_time":""}}}"""
        )

        assertEquals(true, parsedResponse.success)
        assertEquals(50, parsedResponse.data.portHeight)
        assertEquals("TestAdId", parsedResponse.data.ad.id)
        assertEquals(Config.DEFAULT_AD_REFRESH_SECONDS, parsedResponse.data.ad.refreshTimeOrDefault)
    }

    @Test
    fun addToListContentIsCreated() {
        val testAd = getTestAd(
                Payload(detailedListItems = arrayListOf(
                        AddToListItem(
                                "TestTrackingId",
                                "TestTitle",
                                "TestBrand",
                                "TestCategory",
                                "TestUPC",
                                "TestSKU",
                                "TestDiscount",
                                "TestImage")
                ))
        )

        val addToListContent = testAd.getContent()

        assertEquals(addToListContent.getItems().first().trackingId, "TestTrackingId")
        assertEquals(addToListContent.getItems().first().title, "TestTitle")
        assertEquals(addToListContent.getItems().first().brand, "TestBrand")
        assertEquals(addToListContent.getItems().first().category, "TestCategory")
        assertEquals(addToListContent.getItems().first().productUpc, "TestUPC")
        assertEquals(addToListContent.getItems().first().retailerSku, "TestSKU")
        assertEquals(addToListContent.getItems().first().retailerID, "TestDiscount")
        assertEquals(addToListContent.getItems().first().productImage, "TestImage")
    }

    private fun getTestAd(payload: Payload = Payload(detailedListItems = arrayListOf())): Ad {
        return Ad(
                "TestId",
                "TestImpressionId",
                "TestUrl",
                "TestActionType",
                "TestActionPath",
                payload)
    }
}