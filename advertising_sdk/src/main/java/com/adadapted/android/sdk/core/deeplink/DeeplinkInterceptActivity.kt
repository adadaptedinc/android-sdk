package com.adadapted.android.sdk.core.deeplink

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.atl.AddItContentPublisher
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.payload.PayloadClient

class DeeplinkInterceptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AALogger.logInfo("Deeplink Intercept Activity Launched.")
        PayloadClient.deeplinkInProgress()
        EventClient.trackSdkEvent(EventStrings.ADDIT_APP_OPENED)

        try {
            val content = DeeplinkContentParser().parse(intent.data)
            AALogger.logInfo("AddIt content dispatched to App.")
            AddItContentPublisher.publishAddItContent(content)
        } catch (ex: Exception) {
            AALogger.logError("Problem dealing with AddIt content from DeeplinkInterceptActivity. Recovering. " + ex.message)
            val errorParams: MutableMap<String, String> = HashMap()
            ex.message?.let { errorParams.put(EventStrings.EXCEPTION_MESSAGE, it) }
            EventClient.trackSdkError(
                EventStrings.ADDIT_DEEPLINK_HANDLING_ERROR,
                "Problem handling deeplink.",
                errorParams
            )

        } finally {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
        }
        PayloadClient.deeplinkCompleted()
        finish()
    }
}