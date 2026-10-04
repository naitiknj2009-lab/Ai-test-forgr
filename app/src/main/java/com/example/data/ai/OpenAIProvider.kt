package com.example.data.ai

import android.graphics.Bitmap
import android.util.Base64
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

class OpenAIProvider(
    override val providerType: AIProviderType = AIProviderType.OPENAI
) : AIProvider {

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
        if (apiKey.isBlank()) return@withContext Result.failure(Exception("API Key is missing."))
        val selectedModel = model.ifBlank { if (providerType == AIProviderType.OPENAI) "gpt-4o-mini" else "llama-3.3-70b-versatile" }
        val effectiveBase = (baseUrl?.ifBlank { providerType.defaultBaseUrl } ?: providerType.defaultBaseUrl).trimEnd('/')
        val url = "$effectiveBase/chat/completions"

        val jsonBody = JSONObject().apply {
            put("model", selectedModel)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", "Ping. Respond with OK.")
                })
            })
            put("max_tokens", 10)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success("Connection successful! Model '$selectedModel' responded.")
            } else {
                Result.failure(Exception(parseErrorMessage(response.code, bodyString)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect"}"))
        }
    }

    override suspend fun fetchAvailableModels(
        apiKey: String,
        baseUrl: String?
    ): Result<List<AIModelInfo>> = withContext(Dispatchers.IO) {
        val defaultModels = if (providerType == AIProviderType.OPENAI) {
            listOf(
                AIModelInfo("gpt-4o", "GPT-4o (Omni Multimodal)", supportsVision = true, description = "High intelligence flagship model", provider = AIProviderType.OPENAI),
                AIModelInfo("gpt-4o-mini", "GPT-4o Mini (Fast & Affordable)", supportsVision = true, description = "Fast, lightweight model with vision support", provider = AIProviderType.OPENAI),
                AIModelInfo("o1-mini", "o1 Mini (Reasoning Model)", supportsVision = false, description = "High-depth reasoning model for STEM math", provider = AIProviderType.OPENAI)
            )
        } else {
            listOf(
                AIModelInfo("llama-3.3-70b-versatile", "Llama 3.3 70B Versatile", supportsVision = false, description = "High performance open model", provider = AIProviderType.CUSTOM_OPENAI),
                AIModelInfo("llama-3.2-11b-vision-preview", "Llama 3.2 11B Vision", supportsVision = true, description = "Vision capable open weights model", provider = AIProviderType.CUSTOM_OPENAI),
                AIModelInfo("mixtral-8x7b-32768", "Mixtral 8x7B", supportsVision = false, description = "Fast MoE inference", provider = AIProviderType.CUSTOM_OPENAI)
            )
        }

        if (apiKey.isBlank()) return@withContext Result.success(defaultModels)

        val effectiveBase = (baseUrl?.ifBlank { providerType.defaultBaseUrl } ?: providerType.defaultBaseUrl).trimEnd('/')
        val url = "$effectiveBase/models"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .get()
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val obj = JSONObject(bodyString)
                val dataArray = obj.optJSONArray("data")
                val fetched = mutableListOf<AIModelInfo>()
                if (dataArray != null) {
                    for (i in 0 until dataArray.length()) {
                        val m = dataArray.getJSONObject(i)
                        val id = m.optString("id", "")
                        if (id.isNotBlank()) {
                            val isVision = id.contains("4o", true) || id.contains("vision", true) || id.contains("claude-3", true)
                            fetched.add(
                                AIModelInfo(
                                    id = id,
                                    displayName = id,
                                    supportsVision = isVision,
                                    description = if (isVision) "Vision & text capable" else "Text model",
                                    provider = providerType
                                )
                            )
                        }
                    }
                }
                if (fetched.isNotEmpty()) {
                    return@withContext Result.success(fetched.take(25))
                }
            }
        } catch (e: Exception) {
            // Return defaults on error
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
        if (apiKey.isBlank()) return@withContext Result.failure(Exception("API Key is not configured."))
        val selectedModel = model.ifBlank { if (providerType == AIProviderType.OPENAI) "gpt-4o-mini" else "llama-3.3-70b-versatile" }
        val effectiveBase = (baseUrl?.ifBlank { providerType.defaultBaseUrl } ?: providerType.defaultBaseUrl).trimEnd('/')
        val url = "$effectiveBase/chat/completions"

        val jsonBody = JSONObject().apply {
            put("model", selectedModel)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are an expert exam generator. Always return valid JSON without Markdown blocks.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", temperature)
            put("max_tokens", maxTokens)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val parsed = JSONObject(bodyString)
                val text = parsed.optJSONArray("choices")
                    ?.optJSONObject(0)
                    ?.optJSONObject("message")
                    ?.optString("content", "") ?: ""

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
        if (apiKey.isBlank()) return@withContext Result.failure(Exception("API Key is not configured."))
        val selectedModel = model.ifBlank { "gpt-4o-mini" }
        val effectiveBase = (baseUrl?.ifBlank { providerType.defaultBaseUrl } ?: providerType.defaultBaseUrl).trimEnd('/')
        val url = "$effectiveBase/chat/completions"

        val base64Image = bitmapToBase64(bitmap)
        val dataUri = "data:image/jpeg;base64,$base64Image"

        val jsonBody = JSONObject().apply {
            put("model", selectedModel)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {
                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", prompt)
                        })
                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", dataUri)
                            })
                        })
                    })
                })
            })
            put("temperature", temperature)
            put("max_tokens", maxTokens)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val parsed = JSONObject(bodyString)
                val text = parsed.optJSONArray("choices")
                    ?.optJSONObject(0)
                    ?.optJSONObject("message")
                    ?.optString("content", "") ?: ""

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
                401 -> "Invalid API Key or Bearer Token."
                403 -> "Access Forbidden / Insufficient permissions for model."
                404 -> "Model not found or endpoint invalid."
                429 -> "Rate limit reached or quota exceeded."
                500, 503 -> "AI Provider service temporarily unavailable."
                else -> if (msg.isNotBlank()) msg else "Provider error ($code)"
            }
        } catch (e: Exception) {
            when (code) {
                401 -> "Invalid API Key"
                429 -> "Rate limit exceeded"
                404 -> "Model not found"
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
