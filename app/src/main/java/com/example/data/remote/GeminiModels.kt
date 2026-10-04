package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    val inline_data: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    val mime_type: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = 0.4f,
    val topP: Float? = 0.95f,
    val maxOutputTokens: Int? = 4096,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiCandidateContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidateContent(
    val parts: List<GeminiPart>? = null
)

@JsonClass(generateAdapter = true)
data class ExtractedQuestionDto(
    val questionText: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val explanation: String = "",
    val subject: String = "General",
    val chapter: String = "General",
    val topic: String = "General",
    val difficulty: String = "MEDIUM", // EASY, MEDIUM, HARD
    val difficultyReason: String = "",
    val questionType: String = "MCQ"
)

@JsonClass(generateAdapter = true)
data class PerformanceAnalysisDto(
    val summary: String,
    val strongTopics: List<String> = emptyList(),
    val weakTopics: List<String> = emptyList(),
    val recommendedActions: List<String> = emptyList()
)
