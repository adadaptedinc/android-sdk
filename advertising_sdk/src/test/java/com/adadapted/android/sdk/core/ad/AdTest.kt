package com.adadapted.android.sdk.core.ad

import android.os.Parcel
import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.event.AdAdaptedEventClient
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdTest {

    @Before
    fun setup() {
        AdAdaptedEventClient.createInstance(mock(), mock())
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
        assert(mockAd.trackingHtml.isEmpty())
        assert(mockAd.isEmpty)
    }

    @Test
    fun adIsCreatedFromParcel() {
        val testAd = getTestAd()
        val parcel = Parcel.obtain()

        testAd.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val adFromParcel = Ad.createFromParcel(parcel)

        assertEquals("TestId", adFromParcel.id)
        assertEquals("TestZoneId", adFromParcel.zoneId)
        assertEquals("TestImpressionId", adFromParcel.impressionId)
        assertEquals("TestUrl", adFromParcel.url)
        assertEquals("TestActionType", adFromParcel.actionType)
        assertEquals("TestActionPath", adFromParcel.actionPath)
        assertEquals(arrayListOf<AddToListItem>(), adFromParcel.payload)
        assertEquals(1, adFromParcel.refreshTime)
        assertEquals("TestTrackingHtml", adFromParcel.trackingHtml)
    }

    @Test
    fun addToListContentIsCreated() {
        val testAd = getTestAd(
                arrayListOf(
                        AddToListItem(
                                "TestTrackingId",
                                "TestTitle",
                                "TestBrand",
                                "TestCategory",
                                "TestUPC",
                                "TestSKU",
                                "TestDiscount",
                                "TestImage")))

        val addToListContent = testAd.content

        assertEquals(addToListContent.items.first().trackingId, "TestTrackingId")
        assertEquals(addToListContent.items.first().title, "TestTitle")
        assertEquals(addToListContent.items.first().brand, "TestBrand")
        assertEquals(addToListContent.items.first().category, "TestCategory")
        assertEquals(addToListContent.items.first().productUpc, "TestUPC")
        assertEquals(addToListContent.items.first().retailerSku, "TestSKU")
        assertEquals(addToListContent.items.first().discount, "TestDiscount")
        assertEquals(addToListContent.items.first().productImage, "TestImage")
    }

    private fun getTestAd(payload: List<AddToListItem> = arrayListOf()): Ad {
        return Ad(
                "TestId",
                "TestZoneId",
                "TestImpressionId",
                "TestUrl",
                "TestActionType",
                "TestActionPath",
                payload,
                1,
                "TestTrackingHtml")
    }
}