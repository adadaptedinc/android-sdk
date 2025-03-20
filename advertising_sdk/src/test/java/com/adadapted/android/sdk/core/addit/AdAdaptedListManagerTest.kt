package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.AdAdaptedListManager
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.AfterTest

@OptIn(ExperimentalCoroutinesApi::class)
class AdAdaptedListManagerTest {

    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        DeviceInfoClient.createInstance("", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
        SessionClient.onStart(mock())
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
    }

    @AfterTest
    fun cleanup() {
        TestEventAdapter.cleanupEvents()
    }

    @Test
    fun itemAddedToListTest() {
        AdAdaptedListManager.itemAddedToList(item = "TestItem")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.USER_ADDED_TO_LIST })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == EventStrings.USER_ADDED_TO_LIST }
        assertEquals("TestItem", event.params.getValue("item_name"))
    }

    @Test
    fun itemAddedToListWithListTest() {
        AdAdaptedListManager.itemAddedToList(item = "TestItem", list = "TestList")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.USER_ADDED_TO_LIST })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == EventStrings.USER_ADDED_TO_LIST }
        assertEquals("TestList", event.params.getValue("list_name"))
        assertEquals("TestItem", event.params.getValue("item_name"))
    }

    @Test
    fun itemCrossedOffListTest() {
        AdAdaptedListManager.itemCrossedOffList(item = "TestItem")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.USER_CROSSED_OFF_LIST })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == EventStrings.USER_CROSSED_OFF_LIST }
        assertEquals("TestItem", event.params.getValue("item_name"))
    }

    @Test
    fun itemCrossedOffListWithListTest() {
        AdAdaptedListManager.itemCrossedOffList(item = "TestItem", list = "TestList")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.USER_CROSSED_OFF_LIST })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == EventStrings.USER_CROSSED_OFF_LIST }
        assertEquals("TestList", event.params.getValue("list_name"))
        assertEquals("TestItem", event.params.getValue("item_name"))
    }

    @Test
    fun itemDeletedFromListTest() {
        AdAdaptedListManager.itemDeletedFromList(item = "TestItem")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.USER_DELETED_FROM_LIST })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == EventStrings.USER_DELETED_FROM_LIST }
        assertEquals("TestItem", event.params.getValue("item_name"))
    }

    @Test
    fun itemDeletedFromListWithListTest() {
        AdAdaptedListManager.itemDeletedFromList(item = "TestItem", list = "TestList")
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any { event -> event.name == EventStrings.USER_DELETED_FROM_LIST })
        var event = TestEventAdapter.testSdkEvents.first { event -> event.name == EventStrings.USER_DELETED_FROM_LIST }
        assertEquals("TestList", event.params.getValue("list_name"))
        assertEquals("TestItem", event.params.getValue("item_name"))
    }
}