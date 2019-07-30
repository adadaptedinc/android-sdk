package com.adadapted.android.sdk.core.addit;

import com.adadapted.android.sdk.core.atl.AddToListContent;
import com.adadapted.android.sdk.core.atl.AddToListItem;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class AdditContentTest {
    @Test
    public void createDeeplinkContent() {
        final AdditContent content = AdditContent.createDeeplinkContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            new LinkedList<AddToListItem>()
        );

        assertEquals("payloadId", content.getPayloadId());
        assertEquals("message", content.getMessage());
        assertEquals("image", content.getImage());
        assertEquals(AdditContent.AdditSources.DEEPLINK, content.getAdditSource());
        assertEquals(AddToListContent.Sources.OUT_OF_APP, content.getSource());
        assertEquals(0, content.getItems().size());
    }

    @Test
    public void createInAppContent() {
        final AdditContent content = AdditContent.createInAppContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            new LinkedList<AddToListItem>()
        );

        assertEquals("payloadId", content.getPayloadId());
        assertEquals("message", content.getMessage());
        assertEquals("image", content.getImage());
        assertEquals(AdditContent.AdditSources.IN_APP, content.getAdditSource());
        assertEquals(AddToListContent.Sources.IN_APP, content.getSource());
        assertEquals(0, content.getItems().size());
    }

    @Test
    public void createPayloadContent() {
        final AdditContent content = AdditContent.createPayloadContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            new LinkedList<AddToListItem>()
        );

        assertEquals("payloadId", content.getPayloadId());
        assertEquals("message", content.getMessage());
        assertEquals("image", content.getImage());
        assertEquals(AdditContent.AdditSources.PAYLOAD, content.getAdditSource());
        assertEquals(AddToListContent.Sources.OUT_OF_APP, content.getSource());
        assertEquals(0, content.getItems().size());
    }

    @Test
    public void acknowledge() {
        final AdditContent content = new AdditContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AdditContent.AdditSources.IN_APP,
            new LinkedList<AddToListItem>()
        );
        content.acknowledge();

        assertTrue(true);
    }

    @Test
    public void itemAcknowledge() {
        final AdditContent content = new AdditContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AdditContent.AdditSources.IN_APP,
            new LinkedList<AddToListItem>()
        );
        content.itemAcknowledge(new AddToListItem(
            "trackingId",
            "title",
            "brand",
            "category",
            "productUpc",
            "retailerSku",
            "discount",
            "productImage"
        ));

        assertTrue(true);
    }

    @Test
    public void duplicate() {
        final AdditContent content = new AdditContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AdditContent.AdditSources.IN_APP,
            new LinkedList<AddToListItem>()
        );
        content.duplicate();

        assertTrue(true);
    }

    @Test
    public void failed() {
        final AdditContent content = new AdditContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AdditContent.AdditSources.IN_APP,
            new LinkedList<AddToListItem>()
        );
        content.failed("test failed message");

        assertTrue(true);
    }

    @Test
    public void itemFailed() {
        final AdditContent content = new AdditContent(
            "payloadId",
            "message",
            "image",
            ContentTypes.ADD_TO_LIST_ITEMS,
            AddToListContent.Sources.IN_APP,
            AdditContent.AdditSources.IN_APP,
            new LinkedList<AddToListItem>()
        );
        content.itemFailed(new AddToListItem(
            "trackingId",
            "title",
            "brand",
            "category",
            "productUpc",
            "retailerSku",
            "discount",
            "productImage"
        ), "test failed message");

        assertTrue(true);
    }
}