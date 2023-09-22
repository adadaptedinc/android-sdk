//package com.adadapted.android.sdk.core.addit
//
//import com.adadapted.android.sdk.constants.EventStrings
//import com.adadapted.android.sdk.core.event.TestAppEventSink
//import com.adadapted.android.sdk.tools.TestDeviceInfoExtractor
//import com.adadapted.android.sdk.tools.TestTransporter
//import com.nhaarman.mockitokotlin2.mock
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.test.TestCoroutineDispatcher
//import kotlinx.coroutines.test.setMain
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//
//@RunWith(RobolectricTestRunner::class)
//class AdAdaptedListManagerTest {
//
//    private var testTransporter = TestCoroutineDispatcher()
//    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
//    private var testAppEventSink = TestAppEventSink()
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testTransporter)
//        DeviceInfoClient.createInstance(mock(),"", false, HashMap(), "", TestDeviceInfoExtractor(), testTransporterScope)
//        SessionClient.createInstance(mock(), mock())
//        AppEventClient.createInstance(testAppEventSink, testTransporterScope)
//    }
//
//    @Test
//    fun itemAddedToListTest() {
//        AdAdaptedListManager.itemAddedToList("TestItem")
//        AppEventClient.getInstance().onPublishEvents()
//        assertEquals(EventStrings.USER_ADDED_TO_LIST, testAppEventSink.testEvents.first().name)
//        assertEquals("TestItem", testAppEventSink.testEvents.first().params.getValue("item_name"))
//    }
//
//    @Test
//    fun itemAddedToListWithListTest() {
//        AdAdaptedListManager.itemAddedToList("TestList", "TestItem")
//        AppEventClient.getInstance().onPublishEvents()
//        assertEquals(EventStrings.USER_ADDED_TO_LIST, testAppEventSink.testEvents.first().name)
//        assertEquals("TestItem", testAppEventSink.testEvents.first().params.getValue("item_name"))
//        assertEquals("TestList", testAppEventSink.testEvents.first().params.getValue("list_name"))
//    }
//
//    @Test
//    fun itemCrossedOffListTest() {
//        AdAdaptedListManager.itemCrossedOffList("TestItem")
//        AppEventClient.getInstance().onPublishEvents()
//        assertEquals(EventStrings.USER_CROSSED_OFF_LIST, testAppEventSink.testEvents.first().name)
//        assertEquals("TestItem", testAppEventSink.testEvents.first().params.getValue("item_name"))
//    }
//
//    @Test
//    fun itemCrossedOffListWithListTest() {
//        AdAdaptedListManager.itemCrossedOffList("TestList", "TestItem")
//        AppEventClient.getInstance().onPublishEvents()
//        assertEquals(EventStrings.USER_CROSSED_OFF_LIST, testAppEventSink.testEvents.first().name)
//        assertEquals("TestItem", testAppEventSink.testEvents.first().params.getValue("item_name"))
//        assertEquals("TestList", testAppEventSink.testEvents.first().params.getValue("list_name"))
//    }
//
//    @Test
//    fun itemDeletedFromListTest() {
//        AdAdaptedListManager.itemDeletedFromList("TestItem")
//        AppEventClient.getInstance().onPublishEvents()
//        assertEquals(EventStrings.USER_DELETED_FROM_LIST, testAppEventSink.testEvents.first().name)
//        assertEquals("TestItem", testAppEventSink.testEvents.first().params.getValue("item_name"))
//    }
//
//    @Test
//    fun itemDeletedFromListWithListTest() {
//        AdAdaptedListManager.itemDeletedFromList("TestList", "TestItem")
//        AppEventClient.getInstance().onPublishEvents()
//        assertEquals(EventStrings.USER_DELETED_FROM_LIST, testAppEventSink.testEvents.first().name)
//        assertEquals("TestItem", testAppEventSink.testEvents.first().params.getValue("item_name"))
//        assertEquals("TestList", testAppEventSink.testEvents.first().params.getValue("list_name"))
//    }
//}