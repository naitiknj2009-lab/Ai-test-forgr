package com.example.data.ai

enum class AIProviderType(val displayName: String, val defaultBaseUrl: String, val defaultModel: String) {
    GEMINI("Google Gemini", "https://generativelanguage.googleapis.com/", "gemini-3.5-flash"),
    OPENAI("OpenAI", "https://api.openai.com/v1/", "gpt-4o-mini"),
    CUSTOM_OPENAI("Custom / OpenAI Compatible", "https://api.groq.com/openai/v1/", "llama-3.3-70b-versatile")
}

data class AIModelInfo(
    val id: String,
    val displayName: String,
    val supportsVision: Boolean = true,
    val description: String = "",
    val provider: AIProviderType = AIProviderType.GEMINI
)

data class AIProviderConfig(
    val providerType: AIProviderType = AIProviderType.GEMINI,
    val hasSavedKey: Boolean = false,
    val maskedKey: String = "",
    val selectedModel: String = "gemini-3.5-flash",
    val customBaseUrl: String = "https://api.groq.com/openai/v1/",
    val temperature: Float = 0.4f,
    val maxTokens: Int = 4096,
    val useSeparateModels: Boolean = false,
    val ocrModel: String = "gemini-3.5-flash",
    val generationModel: String = "gemini-3.5-flash",
    val explanationModel: String = "gemini-3.5-flash",
    val webAnalysisModel: String = "gemini-3.5-flash"
)
