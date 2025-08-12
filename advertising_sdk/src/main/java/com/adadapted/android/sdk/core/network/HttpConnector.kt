package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.core.log.AALogger
import io.ktor.client.engine.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.toByteArray
import kotlinx.serialization.json.Json
import org.brotli.dec.BrotliInputStream

val defaultPlatformEngine: HttpClientEngine = Android.create()

object HttpConnector {
    const val API_HEADER = "X-API-KEY"

    val json = Json {
        useAlternativeNames = false
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }

        defaultRequest {
            header("Accept-Encoding", "br")
        }

        install(ResponseObserver) {
            onResponse { response ->
                val encoding = response.headers["Content-Encoding"] ?: "none"
                AALogger.logInfo("HTTP status: ${response.status.value}, encoding: $encoding")
            }
        }

        install(HttpRequestRetry)
    }
    suspend fun HttpResponse.autoDecompressBr(): ByteArray {
        val rawBytes = bodyAsChannel().toByteArray()
        return BrotliInputStream(rawBytes.inputStream()).use { it.readBytes() }
    }

    suspend inline fun <reified T> HttpResponse.decompressAndDeserialize(
        json: Json = HttpConnector.json
    ): T {
        val decompressedBytes = this.autoDecompressBr()
        val jsonString = decompressedBytes.decodeToString()
        return json.decodeFromString(jsonString)
    }
}
