package com.adadapted.android.sdk.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.addit.DeeplinkContentParser
import com.adadapted.android.sdk.core.addit.PayloadClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher

class AdditInterceptActivity : AppCompatActivity() {
    private val LOGTAG = AdditInterceptActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOGTAG, "Addit Intercept Activity Launched.")
        PayloadClient.getInstance().deeplinkInProgress()
        AppEventClient.getInstance().trackSdkEvent(EventStrings.ADDIT_APP_OPENED)

        try {
            val content = DeeplinkContentParser().parse(intent.data)
            Log.i(LOGTAG, "Addit content dispatched to App.")
            AdditContentPublisher.getInstance().publishAdditContent(content)
        } catch (ex: Exception) {
            Log.w(LOGTAG, "Problem dealing with Addit content. Recovering. " + ex.message)
            val errorParams: MutableMap<String, String> = HashMap()
            ex.message?.let { errorParams.put(EventStrings.EXCEPTION_MESSAGE, it) }
            AppEventClient.getInstance().trackError(
                EventStrings.ADDIT_DEEPLINK_HANDLING_ERROR,
                "Problem handling deeplink",
                errorParams
            )
        } finally {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
        }
        PayloadClient.getInstance().deeplinkCompleted()
        finish()
    }
}