package de.thermondo.util

import de.thermondo.logger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

val json = Json {
    ignoreUnknownKeys = true
}

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(json)
    }
}


suspend inline fun <reified T> String.post(payload: JsonObject): T {
    return try {
        val response: HttpResponse = client.post(this) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        val responseBody = response.bodyAsText()
        logger.info("Response from external API: $responseBody")
        Json.decodeFromString<T>(responseBody)
    } catch (e: Exception) {
        logger.error("Failed to send data to external API: ${e.message}")
        throw e
    }
}