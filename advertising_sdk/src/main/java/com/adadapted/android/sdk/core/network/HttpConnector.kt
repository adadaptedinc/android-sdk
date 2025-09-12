package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.core.log.AALogger
import io.ktor.client.engine.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.observer.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val defaultPlatformEngine: HttpClientEngine = Android.create()

object HttpConnector {
    const val API_HEADER = "X-API-KEY"
    const val ENCODING_HEADER = "Accept-Encoding"
    const val ENCODING_FORMATS = "gzip, deflate"

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                useAlternativeNames = false
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }

        install(ResponseObserver) {
            onResponse { response ->
                AALogger.logInfo("HTTP status: ${response.status.value}")
            }
        }
        install(HttpRequestRetry)
    }
}
