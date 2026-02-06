package com.adadapted.android.sdk.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
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

        install(HttpRequestRetry)
        install(ContentEncoding) {
            gzip()
            deflate()
        }

        defaultRequest {
            header("Accept-Encoding", "gzip, deflate")
        }
    }
}
