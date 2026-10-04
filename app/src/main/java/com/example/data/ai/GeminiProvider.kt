package com.example.data.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiProvider : AIProvider {

    override val providerType = AIProviderType.GEMINI

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    override suspend fun testConnection(
        apiKey: String,
        model: String,
        baseUrl: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext Result.failure(Exception("API Key is missing. Please save an API key first."))
        val selectedModel = model.ifBlank { "gemini-3.5-flash" }
        val effectiveBase = (baseUrl?.ifBlank { "https://generativelanguage.googleapis.com/" } ?: "https://generativelanguage.googleapis.com/").trimEnd('/')
        val url = "$effectiveBase/v1beta/models/$selectedModel:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", "Ping test. Respond with OK.") })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success("Connection successful! Model '$selectedModel' responded.")
            } else {
                val errorMsg = parseErrorMessage(response.code, bodyString)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect"}"))
        }
    }

    override suspend fun fetchAvailableModels(
        apiKey: String,
        baseUrl: String?
    ): Result<List<AIModelInfo>> = withContext(Dispatchers.IO) {
        val defaultModels = listOf(
            AIModelInfo("gemini-3.5-flash", "Gemini 3.5 Flash (Recommended Default)", supportsVision = true, description = "Fast, multimodal, high precision for STEM & OCR", provider = AIProviderType.GEMINI),
            AIModelInfo("gemini-3.1-pro-preview", "Gemini 3.1 Pro (Advanced Reasoning)", supportsVision = true, description = "Deep reasoning for complex math & derivations", provider = AIProviderType.GEMINI),
            AIModelInfo("gemini-3.1-flash-lite-preview", "Gemini 3.1 Flash Lite", supportsVision = true, description = "Ultra-fast low latency question generation", provider = AIProviderType.GEMINI),
            AIModelInfo("gemini-flash-latest", "Gemini Flash (Latest Stable)", supportsVision = true, description = "General multimodal question extraction", provider = AIProviderType.GEMINI)
        )

        if (apiKey.isBlank()) return@withContext Result.success(defaultModels)

        val effectiveBase = (baseUrl?.ifBlank { "https://generativelanguage.googleapis.com/" } ?: "https://generativelanguage.googleapis.com/").trimEnd('/')
        val url = "$effectiveBase/v1beta/models?key=$apiKey"
        val request = Request.Builder().url(url).get().build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val obj = JSONObject(bodyString)
                val modelsArray = obj.optJSONArray("models")
                val fetched = mutableListOf<AIModelInfo>()
                if (modelsArray != null) {
                    for (i in 0 until modelsArray.length()) {
                        val m = modelsArray.getJSONObject(i)
                        val name = m.optString("name", "").removePrefix("models/")
                        val displayName = m.optString("displayName", name)
                        val description = m.optString("description", "")
                        if (name.isNotBlank() && (name.contains("gemini", true))) {
                            fetched.add(
                                AIModelInfo(
                                    id = name,
                                    displayName = "$displayName ($name)",
                                    supportsVision = true,
                                    description = description,
                                    provider = AIProviderType.GEMINI
                                )
                            )
                        }
                    }
                }
                if (fetched.isNotEmpty()) {
                    return@withContext Result.success(fetched)
                }
            }
        } catch (e: Exception) {
            // Return defaults on network issue
        }

        Result.success(defaultModels)
    }

    override suspend fun generateText(
        prompt: String,
        model: String,
        apiKey: String,
        temperature: Float,
        maxTokens: Int,
        baseUrl: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext Result.failure(Exception("Gemini API Key is not configured."))
        val selectedModel = model.ifBlank { "gemini-3.5-flash" }
        val effectiveBase = (baseUrl?.ifBlank { "https://generativelanguage.googleapis.com/" } ?: "https://generativelanguage.googleapis.com/").trimEnd('/')
        val url = "$effectiveBase/v1beta/models/$selectedModel:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", maxTokens)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val parsed = JSONObject(bodyString)
                val text = parsed.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text", "") ?: ""

                Result.success(text)
            } else {
                Result.failure(Exception(parseErrorMessage(response.code, bodyString)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun generateWithImage(
        prompt: String,
        bitmap: Bitmap,
        model: String,
        apiKey: String,
        temperature: Float,
        maxTokens: Int,
        baseUrl: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext Result.failure(Exception("Gemini API Key is not configured."))
        val selectedModel = model.ifBlank { "gemini-3.5-flash" }
        val effectiveBase = (baseUrl?.ifBlank { "https://generativelanguage.googleapis.com/" } ?: "https://generativelanguage.googleapis.com/").trimEnd('/')
        val url = "$effectiveBase/v1beta/models/$selectedModel:generateContent?key=$apiKey"

        val base64Image = bitmapToBase64(bitmap)

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                        put(JSONObject().apply {
                            put("inline_data", JSONObject().apply {
                                put("mime_type", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", maxTokens)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val parsed = JSONObject(bodyString)
                val text = parsed.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text", "") ?: ""

                Result.success(text)
            } else {
                Result.failure(Exception(parseErrorMessage(response.code, bodyString)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    private fun parseErrorMessage(code: Int, body: String): String {
        return try {
            val json = JSONObject(body)
            val errorObj = json.optJSONObject("error")
            val msg = errorObj?.optString("message", "") ?: ""
            when (code) {
                400 -> if (msg.isNotBlank()) "Invalid Request: $msg" else "Bad Request / Invalid Model"
                401, 403 -> "Invalid API Key or Unauthorized Access."
                404 -> "Model not found or deprecated. Please choose another model."
                429 -> "Rate limit reached or API quota exceeded."
                500, 503 -> "Gemini service temporarily unavailable."
                else -> if (msg.isNotBlank()) msg else "Provider error ($code)"
            }
        } catch (e: Exception) {
            when (code) {
                401, 403 -> "Invalid API Key"
                429 -> "Rate limit exceeded"
                404 -> "Model unavailable"
                else -> "HTTP Error $code"
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
