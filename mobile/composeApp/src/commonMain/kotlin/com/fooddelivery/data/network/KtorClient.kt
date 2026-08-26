package com.fooddelivery.data.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiConfig {
    // Live VIP Server REST API Base URL
    var BASE_URL = "https://insof-kampot.uz/api/v1"
    var AUTH_TOKEN: String? = null

    const val CONNECT_TIMEOUT_MS = 15_000L
    const val REQUEST_TIMEOUT_MS = 30_000L
    const val SOCKET_TIMEOUT_MS = 30_000L
}

/** Butun ilova uchun yagona JSON konfiguratsiyasi */
val AppJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
    explicitNulls = false
    encodeDefaults = true
}

fun createHttpClient(): HttpClient {
    return HttpClient {
        // Server 4xx/5xx qaytarsa ham javob tanasini o'qiy olamiz (xato matnini ko'rsatish uchun)
        expectSuccess = false

        install(ContentNegotiation) {
            json(AppJson)
        }

        // Tarmoq uzilib qolganda ilova cheksiz kutib turmasligi uchun
        install(HttpTimeout) {
            connectTimeoutMillis = ApiConfig.CONNECT_TIMEOUT_MS
            requestTimeoutMillis = ApiConfig.REQUEST_TIMEOUT_MS
            socketTimeoutMillis = ApiConfig.SOCKET_TIMEOUT_MS
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 2)
            retryOnExceptionIf(maxRetries = 2) { _, cause -> cause is HttpRequestTimeoutException }
            exponentialDelay()
        }

        // Har bir so'rovga Accept va Authorization sarlavhalari avtomatik qo'shiladi
        install(DefaultRequest) {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            ApiConfig.AUTH_TOKEN?.takeIf { it.isNotBlank() }?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE // Tokenlar va shaxsiy ma'lumotlar logga tushmasligi uchun
        }
    }
}
