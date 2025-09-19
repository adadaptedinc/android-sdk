package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.core.log.AALogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.observer.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpConnector {
    const val API_HEADER = "X-API-KEY"

    val client = HttpClient(Android.create()) {
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

        install(ContentEncoding) {
            gzip()
            deflate()
        }
    }
}
