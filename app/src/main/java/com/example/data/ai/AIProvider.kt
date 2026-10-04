package com.example.data.ai

import android.graphics.Bitmap

interface AIProvider {
    val providerType: AIProviderType

    suspend fun testConnection(
        apiKey: String,
        model: String,
        baseUrl: String?
    ): Result<String>

    suspend fun fetchAvailableModels(
        apiKey: String,
        baseUrl: String?
    ): Result<List<AIModelInfo>>

    suspend fun generateText(
        prompt: String,
        model: String,
        apiKey: String,
        temperature: Float,
        maxTokens: Int,
        baseUrl: String?
    ): Result<String>

    suspend fun generateWithImage(
        prompt: String,
        bitmap: Bitmap,
        model: String,
        apiKey: String,
        temperature: Float,
        maxTokens: Int,
        baseUrl: String?
    ): Result<String>
}
