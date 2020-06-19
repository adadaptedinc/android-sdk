package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.LinkedList

class AdditContentTest {
    @Before
    fun setup() {
        DeviceInfoClient.createInstance(mock(), "", false, mock(), mock(), mock())
        SessionClient.createInstance(mock(), mock())
        AppEventClient.createInstance(mock(),mock())
        PayloadClient.createInstance(mock(), AppEventClient.getInstance(), mock())
    }

    @Test
    fun createDeeplinkContent() {
        val content = AdditContent.createDeeplinkContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                LinkedList()
        )
        Assert.assertEquals("payloadId", content.payloadId)
        Assert.assertEquals("message", content.message)
        Assert.assertEquals("image", content.image)
        Assert.assertEquals(AdditContent.AdditSources.DEEPLINK, content.additSource)
        Assert.assertEquals(AddToListContent.Sources.OUT_OF_APP, content.source)
        Assert.assertEquals(0, content.items.size.toLong())
    }

    @Test
    fun createInAppContent() {
        val content = AdditContent.createInAppContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                LinkedList()
        )
        Assert.assertEquals("payloadId", content.payloadId)
        Assert.assertEquals("message", content.message)
        Assert.assertEquals("image", content.image)
        Assert.assertEquals(AdditContent.AdditSources.IN_APP, content.additSource)
        Assert.assertEquals(AddToListContent.Sources.IN_APP, content.source)
        Assert.assertEquals(0, content.items.size.toLong())
    }

    @Test
    fun createPayloadContent() {
        val content = AdditContent.createPayloadContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                LinkedList()
        )
        Assert.assertEquals("payloadId", content.payloadId)
        Assert.assertEquals("message", content.message)
        Assert.assertEquals("image", content.image)
        Assert.assertEquals(AdditContent.AdditSources.PAYLOAD, content.additSource)
        Assert.assertEquals(AddToListContent.Sources.OUT_OF_APP, content.source)
        Assert.assertEquals(0, content.items.size.toLong())
    }

    @Test
    fun acknowledge() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.acknowledge()
        Assert.assertTrue(true)
    }

    @Test
    fun itemAcknowledge() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.itemAcknowledge(AddToListItem(
                "trackingId",
                "title",
                "brand",
                "category",
                "productUpc",
                "retailerSku",
                "discount",
                "productImage"
        ))
        Assert.assertTrue(true)
    }

    @Test
    fun duplicate() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.duplicate()
        Assert.assertTrue(true)
    }

    @Test
    fun failed() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.failed("test failed message")
        Assert.assertTrue(true)
    }

    @Test
    fun itemFailed() {
        val content = AdditContent(
                "payloadId",
                "message",
                "image",
                ContentTypes.ADD_TO_LIST_ITEMS,
                AddToListContent.Sources.IN_APP,
                AdditContent.AdditSources.IN_APP,
                LinkedList()
        )
        content.itemFailed(AddToListItem(
                "trackingId",
                "title",
                "brand",
                "category",
                "productUpc",
                "retailerSku",
                "discount",
                "productImage"
        ), "test failed message")
        Assert.assertTrue(true)
    }
}