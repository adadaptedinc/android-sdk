package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.models.Payload
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertNotNull
import junit.framework.TestCase.assertEquals

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdTest {

    @Before
    fun setup() {
        DeviceInfoClient.createInstance(mock(), "", false, mock(), "", TestDeviceInfoExtractor(), mock())
        SessionClient.createInstance(mock(), mock())
        AdEventClient.createInstance(mock(), mock())
        AppEventClient.createInstance(mock(), mock())
    }

    @Test
    fun verifyAdEventStructure() {
        val adEventTypes = AdEvent.Types
        val adEventImp = adEventTypes.IMPRESSION
        val adEventInv = adEventTypes.INVISIBLE_IMPRESSION
        val adEventInt = adEventTypes.INTERACTION
        val adEventPop = adEventTypes.POPUP_BEGIN

        assertEquals(AdEvent.Types.IMPRESSION, adEventImp)
        assertEquals(AdEvent.Types.INVISIBLE_IMPRESSION, adEventInv)
        assertEquals(AdEvent.Types.INTERACTION, adEventInt)
        assertEquals(AdEvent.Types.POPUP_BEGIN, adEventPop)
    }

    @Test
    fun verifyAdEventCreation() {
        val testAdEvent = AdEvent("adId", "zoneId", "impressionId", AdEvent.Types.IMPRESSION)
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
                Payload(arrayListOf(
                        AddToListItem(
                                "TestTrackingId",
                                "TestTitle",
                                "TestBrand",
                                "TestCategory",
                                "TestUPC",
                                "TestSKU",
                                "TestDiscount",
                                "TestImage"))))

        val addToListContent = testAd.getContent()

        assertEquals(addToListContent.getItems().first().trackingId, "TestTrackingId")
        assertEquals(addToListContent.getItems().first().title, "TestTitle")
        assertEquals(addToListContent.getItems().first().brand, "TestBrand")
        assertEquals(addToListContent.getItems().first().category, "TestCategory")
        assertEquals(addToListContent.getItems().first().productUpc, "TestUPC")
        assertEquals(addToListContent.getItems().first().retailerSku, "TestSKU")
        assertEquals(addToListContent.getItems().first().discount, "") //temp discount swap
        assertEquals(addToListContent.getItems().first().retailerID, "TestDiscount")
        assertEquals(addToListContent.getItems().first().productImage, "TestImage")
    }

    private fun getTestAd(payload: Payload = Payload(arrayListOf())): Ad {
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