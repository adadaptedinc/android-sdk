package com.adadapted.android.sdk.core.session

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewSessionClientTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private val mockOwner = object : LifecycleOwner {
        override val lifecycle = LifecycleRegistry(this)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        SessionClient.reset()
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
        EventClient.onPublishEvents()
        TestEventAdapter.cleanupEvents()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onStart creates new session when session is empty`() {
        SessionClient.onStart(mockOwner)
        assertTrue(SessionClient.getSessionId().isNotEmpty())
    }

    @Test
    fun `onStart resumes session if within time limit`() {
        SessionClient.onStart(mockOwner)
        val firstSessionId = SessionClient.getSessionId()

        SessionClient.onStop(mockOwner)
        SessionClient.onStart(mockOwner)
        EventClient.onPublishEvents()

        assertEquals(firstSessionId, SessionClient.getSessionId()) // Should be the same session
        assert(TestEventAdapter.testSdkEvents.any {e -> e.name == EventStrings.SESSION_RESUMED})
    }

    @Test
    fun `onStop updates backgroundTime and tracks event`() {
        SessionClient.onStart(mockOwner)
        val initialSessionId = SessionClient.getSessionId()
        SessionClient.onStop(mockOwner)
        EventClient.onPublishEvents()
        assert(TestEventAdapter.testSdkEvents.any {e -> e.name == EventStrings.SESSION_BACKGROUNDED})
        assertEquals(initialSessionId, SessionClient.getSessionId()) // Session ID should remain unchanged
    }

    @Test
    fun `generateId produces correct format`() {
        val generateIdMethod = SessionClient::class.java.getDeclaredMethod("generateId")
        generateIdMethod.isAccessible = true
        val generatedId = generateIdMethod.invoke(SessionClient) as String

        assertTrue(generatedId.startsWith("ANDROID"))
        assertEquals(39, generatedId.length) // "ANDROID" (7) + 32 random characters
    }

    @Test
    fun `generateId contains only valid characters`() {
        val generateIdMethod = SessionClient::class.java.getDeclaredMethod("generateId")
        generateIdMethod.isAccessible = true
        val generatedId = generateIdMethod.invoke(SessionClient) as String

        val randomPart = generatedId.removePrefix("ANDROID")
        assertTrue(randomPart.all { it in 'A'..'Z' || it in '0'..'9' })
    }

    @Test
    fun `onStart tracks a session lifecycle event`() {
        SessionClient.onStart(mockOwner)
        EventClient.onPublishEvents()

        val hasSessionEvent = TestEventAdapter.testSdkEvents.any { e ->
            e.name == EventStrings.SESSION_CREATED || e.name == EventStrings.SESSION_RESUMED
        }
        assertTrue(hasSessionEvent)
    }

    @Test
    fun `tracked session events include sessionId param`() {
        SessionClient.onStart(mockOwner)
        EventClient.onPublishEvents()

        val sessionEvent = TestEventAdapter.testSdkEvents.first { e ->
            e.name == EventStrings.SESSION_CREATED || e.name == EventStrings.SESSION_RESUMED
        }
        assertTrue(sessionEvent.params.containsKey("sessionId"))
        assertEquals(SessionClient.getSessionId(), sessionEvent.params["sessionId"])
    }

    @Test
    fun `multiple start stop cycles maintain same session`() {
        SessionClient.onStart(mockOwner)
        val sessionId = SessionClient.getSessionId()

        repeat(5) {
            SessionClient.onStop(mockOwner)
            SessionClient.onStart(mockOwner)
        }

        assertEquals(sessionId, SessionClient.getSessionId())
    }
}
