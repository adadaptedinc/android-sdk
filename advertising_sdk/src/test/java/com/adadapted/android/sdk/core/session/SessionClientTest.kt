package com.adadapted.android.sdk.core.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.tools.TestEventAdapter
import com.adadapted.android.sdk.tools.TestTransporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewSessionClientTest {
    private var testTransporter = UnconfinedTestDispatcher()
    private val testTransporterScope: TransporterCoroutineScope = TestTransporter(testTransporter)
    private val mockOwner = object : LifecycleOwner {
        override val lifecycle: Lifecycle
            get() = lifecycle
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testTransporter)
        EventClient.createInstance(TestEventAdapter, testTransporterScope)
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
}
