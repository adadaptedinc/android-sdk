package com.adadapted.android.sdk.core.deeplink

import android.net.Uri
import android.util.Base64
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.atl.AddItContent
import com.adadapted.android.sdk.core.atl.AddItContentParser
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.payload.Payload
import io.ktor.utils.io.core.String
import kotlinx.serialization.decodeFromString
import org.json.JSONException

class DeeplinkContentParser {
    @Throws(Exception::class)
    fun parse(uri: Uri?): AddItContent {
        if (uri == null) {
            EventClient.trackSdkError(EventStrings.ADDIT_NO_DEEPLINK_RECEIVED, NO_DEEPLINK_URL)
            throw Exception(NO_DEEPLINK_URL)
        }
        val data = uri.getQueryParameter("data")
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val jsonString = String(decodedData)

        try {
            val payload = kotlinx.serialization.json.Json.decodeFromString<Payload>(jsonString)
            return AddItContentParser.generateAddItContentFromDeeplink(payload)

        } catch (ex: JSONException) {
            val errorParams: MutableMap<String, String> = HashMap()
            errorParams["payload"] = "{\"raw\":\"$data\", \"parsed\":\"$jsonString\"}"
            ex.message?.let { errorParams.put(EventStrings.EXCEPTION_MESSAGE, it) }
            EventClient.trackSdkError(EventStrings.ADDIT_PAYLOAD_PARSE_FAILED, "Problem parsing Deeplink JSON input", errorParams)
            throw Exception(PAYLOAD_PARSE_ERROR)
        }
    }

    companion object {
        private const val NO_DEEPLINK_URL = "Did not receive a deeplink url."
        private const val PAYLOAD_PARSE_ERROR = "Problem parsing content payload."
    }
}