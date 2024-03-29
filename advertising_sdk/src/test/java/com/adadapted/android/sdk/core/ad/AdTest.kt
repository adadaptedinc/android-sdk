package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.AdEventTypes
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.Payload
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertNotNull
import junit.framework.TestCase.assertEquals

import org.junit.Before
import org.junit.Test

class AdTest {

    @Before
    fun setup() {
        DeviceInfoClient.createInstance("", false, mock(), "", TestDeviceInfoExtractor(), mock())
        SessionClient.createInstance(mock(), mock())
        EventClient.createInstance(mock(), mock())
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

    @Test
    fun defaultAdIsCreated() {
        val mockAd = Ad()

        assert(mockAd.id.isEmpty())
        assert(mockAd.zoneId.isEmpty())
        assert(mockAd.impressionId.isEmpty())
        assert(mockAd.url.isEmpty())
        assert(mockAd.actionType.isEmpty())
        assert(mockAd.actionPath.isEmpty())
        assertNotNull(mockAd.payload)
        assert(mockAd.refreshTime == Config.DEFAULT_AD_REFRESH)
        assert(mockAd.isEmpty)
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
                payload,
                1)
    }
}