package gr.zimagegen.api

import android.util.Log
import gr.zimagegen.model.ImageGenerationResult
import gr.zimagegen.security.ApiKeyProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.time.Duration.Companion.milliseconds

class BflApi(
    private val apiKeyProvider: ApiKeyProvider
) {

    companion object {
        private const val TAG = "BflApi"

        const val BASE_URL = "https://api.bfl.ai"

        const val DEFAULT_GENERATION_ENDPOINT =
            "$BASE_URL/v1/flux-2-klein-9b"

        private const val CREDITS_ENDPOINT = "$BASE_URL/v1/credits"

        private const val POLL_INTERVAL_MS = 500L
        private const val MAX_POLL_ATTEMPTS = 120
    }

    private val client = OkHttpClient()

    suspend fun generateImage(
        prompt: String,
        width: Int,
        height: Int,
        endpoint: String = DEFAULT_GENERATION_ENDPOINT,
        customApiKey: String? = null,
        modelName: String = "FLUX.2 Klein 9B",
        onLog: (String) -> Unit = {}
    ): ImageGenerationResult = withContext(Dispatchers.IO) {

        fun debug(message: String) {
            Log.d(TAG, message)
            onLog(message)
        }

        fun warning(message: String) {
            Log.w(TAG, message)
            onLog("WARNING: $message")
        }

        debug("Starting image generation")
        debug("Model: $modelName")
        debug("Resolution: ${width}x${height}")

        val apiKey = if (!customApiKey.isNullOrBlank()) {
            customApiKey
        } else {
            apiKeyProvider.getBflApiKey()
        }

        if (apiKey.isBlank()) {
            Log.e(TAG, "BFL API key is empty")
            onLog("ERROR: BFL API key is empty")

            throw BflApiException(
                "BFL API key is not configured"
            )
        }

        debug("API key obtained")

        val requestBody = JSONObject().apply {
            put("prompt", prompt)
            put("width", width)
            put("height", height)
        }

        debug("Preparing request to $endpoint")

        val request = Request.Builder()
            .url(endpoint)
            .addHeader("accept", "application/json")
            .addHeader("x-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(
                requestBody
                    .toString()
                    .toRequestBody(
                        "application/json".toMediaType()
                    )
            )
            .build()

        debug("Sending request to BFL")

        client.newCall(request).execute().use { response ->

            debug("BFL responded with HTTP ${response.code}")

            if (!response.isSuccessful) {
                val errorBody = response.body?.string()

                Log.e(
                    TAG,
                    "Generation request failed: HTTP ${response.code}"
                )

                onLog(
                    "ERROR: Generation request failed (HTTP ${response.code})"
                )

                if (!errorBody.isNullOrBlank()) {
                    warning("BFL error response received")
                    Log.w(TAG, "BFL error response: $errorBody")
                }

                throw BflApiException(
                    "Generation request failed: HTTP ${response.code}"
                )
            }

            debug("Generation request accepted")

            val responseBody = response.body?.string()
                ?: run {
                    Log.e(TAG, "BFL returned an empty response")
                    onLog("ERROR: Empty response from BFL")

                    throw BflApiException(
                        "Empty response from BFL"
                    )
                }

            val responseJson = JSONObject(responseBody)

            val pollingUrl = responseJson.optString("polling_url")
            val generationCost = responseJson.optDouble("cost")

            if (pollingUrl.isBlank()) {
                Log.e(TAG, "BFL did not return a polling URL")
                onLog("ERROR: BFL did not return a polling URL")

                throw BflApiException(
                    "BFL did not return a polling URL"
                )
            }

            debug("Polling URL received")
            debug("Starting generation polling")

            return@withContext ImageGenerationResult(
                imageUrl = pollForResult(
                    pollingUrl = pollingUrl,
                    onLog = onLog
                ),
                cost = generationCost
            )
        }
    }

    private suspend fun pollForResult(
        pollingUrl: String,
        onLog: (String) -> Unit
    ): String {

        fun debug(message: String) {
            Log.d(TAG, message)
            onLog(message)
        }

        repeat(MAX_POLL_ATTEMPTS) { attempt ->

            val request = Request.Builder()
                .url(pollingUrl)
                .addHeader("accept", "application/json")
                .build()

            val responseBody = withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->

                    if (!response.isSuccessful) {
                        Log.e(
                            TAG,
                            "Polling failed: HTTP ${response.code}"
                        )

                        onLog(
                            "ERROR: Polling failed (HTTP ${response.code})"
                        )

                        throw BflApiException(
                            "Polling failed: HTTP ${response.code}"
                        )
                    }

                    response.body?.string()
                        ?: run {
                            Log.e(
                                TAG,
                                "BFL polling returned an empty response"
                            )

                            onLog(
                                "ERROR: Empty polling response"
                            )

                            throw BflApiException(
                                "Empty polling response"
                            )
                        }
                }
            }

            val json = JSONObject(responseBody)
            val status = json.optString("status")

            when (status) {

                "Ready" -> {
                    debug("Generation complete")

                    val result = json.optJSONObject("result")
                        ?: run {
                            Log.e(
                                TAG,
                                "BFL returned Ready without a result"
                            )

                            onLog(
                                "ERROR: Ready response has no result"
                            )

                            throw BflApiException(
                                "BFL returned Ready without a result"
                            )
                        }

                    val imageUrl = result.optString("sample")

                    if (imageUrl.isBlank()) {
                        Log.e(
                            TAG,
                            "BFL returned Ready without an image URL"
                        )

                        onLog(
                            "ERROR: No image URL returned"
                        )

                        throw BflApiException(
                            "BFL returned Ready without an image URL"
                        )
                    }

                    debug("Image URL received")

                    return imageUrl
                }

                "Error",
                "Failed",
                "Request Moderated",
                "Content Moderated" -> {

                    Log.e(
                        TAG,
                        "Image generation failed: $status"
                    )

                    onLog(
                        "ERROR: Image generation failed — $status"
                    )

                    throw BflApiException(
                        "Image generation failed: $status"
                    )
                }

                else -> {
                    debug(
                        "Generation status: $status " +
                                "(poll ${attempt + 1}/$MAX_POLL_ATTEMPTS)"
                    )
                }
            }

            delay(POLL_INTERVAL_MS.milliseconds)
        }

        Log.w(
            TAG,
            "Image generation timed out after " +
                    "$MAX_POLL_ATTEMPTS polling attempts"
        )

        onLog("WARNING: Generation timed out")

        throw BflApiException(
            "Image generation timed out"
        )
    }

    suspend fun getCredits(): Double = withContext(Dispatchers.IO) {
        val apiKey = apiKeyProvider.getBflApiKey()

        if (apiKey.isBlank()) {
            throw BflApiException("BFL API key is not configured")
        }

        val request = Request.Builder()
            .url(CREDITS_ENDPOINT)
            .addHeader("accept", "application/json")
            .addHeader("x-key", apiKey)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw BflApiException(
                    "Credits request failed: HTTP ${response.code}"
                )
            }

            val responseBody = response.body.string()

            val json = JSONObject(responseBody)

            if (!json.has("credits") || json.isNull("credits")) {
                throw BflApiException(
                    "BFL response did not contain credits"
                )
            }

            json.getDouble("credits")
        }
    }
}

class BflApiException(
    message: String
) : Exception(message)