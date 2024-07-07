package de.thermondo.route

import de.thermondo.logger
import de.thermondo.util.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import java.util.Base64
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Route to handle the /api/evaluate endpoint.
 * This endpoint accepts a multipart/form-data request with a prompt and an image.
 */
fun Route.postEvaluateRoute() {
    post("/api/evaluate") {
        val multipartData = call.receiveMultipart()
        val (prompt, base64File) = processMultipartData(multipartData)

        // Validate the received data
        if (prompt == null) {
            logger.error("No prompt provided.")
            call.respond(HttpStatusCode.BadRequest, "No prompt provided")
            return@post
        }

        if (base64File == null) {
            logger.error("No file uploaded or file is empty.")
            call.respond(HttpStatusCode.BadRequest, "File upload failed")
            return@post
        }

        handleEvaluationRequest(call, prompt, base64File)
    }
}

/**
 * Processes multipart form data to extract the prompt and base64-encoded file.
 *
 * @param multipartData The multipart data received in the request.
 * @return A pair containing the prompt and the base64-encoded file.
 */
suspend fun processMultipartData(multipartData: MultiPartData): Pair<String?, String?> {
    var prompt: String? = null
    var base64File: String? = null

    multipartData.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> {
                if (part.name == "prompt") {
                    prompt = part.value
                    logger.info("Prompt received: $prompt")
                }
            }

            is PartData.FileItem -> {
                if (part.name == "image") {
                    val fileBytes = part.streamProvider().readBytes()
                    base64File = Base64.getEncoder().encodeToString(fileBytes)
                }
            }

            else -> {
                // Ignore other parts
            }
        }
        part.dispose()
    }

    return Pair(prompt, base64File)
}


/**
 * Handles the evaluation request by sending the data to the external API.
 *
 * @param call The current application call.
 * @param prompt The prompt provided by the user.
 * @param base64File The base64-encoded file.
 * this is the response from the ollama model, as you see it is a json object with a response and a score
 *  ```
 *```json
 *  {
 *    ...
 *    "response": " {\n  \"response\": \"The image you provided does not contain any text or objects that match the description in your prompt. The image appears to be a random collection of shapes and colors without any clear subject matter. Therefore, I would give it a score of 0.\",\n  \"score\": 0\n} ",
 *    ...
 * }
 * ```
 * and that's why we are escaping the response to get the inner json object
 *
 *```kotlin
 * .trim()
 *  .drop(1)
 *  .dropLast(1)
 *  .replace("\\n", "")
 *  .replace("\\", "")
 *
 */
suspend fun handleEvaluationRequest(call: ApplicationCall, prompt: String, base64File: String) {
    logger.info("Base64 file and prompt prepared, sending to external API.")

    // Create the JSON payload for the external API request
    val jsonPayload = buildJsonObject {
        put("model", "qm:latest")
        put("prompt", prompt)
        put("images[0]", base64File)
        put("stream", false)
    }

    // Send the payload to the external API and handle the response
    try {
        // val jsonObject = "http://localhost:11434/api/generate".post<JsonObject>(jsonPayload)
        val jsonObject = "http://host.docker.internal:11434/api/generate".post<JsonObject>(jsonPayload)
        val res = jsonObject["response"].toString()

        val innerJsonString = res
            .trim()
            .drop(1)
            .dropLast(1)
            .replace("\\n", "")
            .replace("\\", "")

        call.respondText(innerJsonString, ContentType.Application.Json)

    } catch (e: Exception) {
        logger.error("Error communicating with external API: ${e.message}")
        call.respond(HttpStatusCode.InternalServerError, "Error communicating with external API")
    }
}

