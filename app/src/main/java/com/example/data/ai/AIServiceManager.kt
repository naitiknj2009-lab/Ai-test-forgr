package com.example.data.ai

import android.content.Context
import android.graphics.Bitmap
import com.example.BuildConfig
import com.example.data.security.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AITaskType {
    DOCUMENT_OCR,
    QUESTION_GENERATION,
    SIMILAR_QUESTION,
    EXPLANATION,
    PERFORMANCE_ANALYSIS,
    WEB_ANALYSIS
}

class AIServiceManager(context: Context) {

    private val secureStorage = SecureStorage(context)

    private val geminiProvider = GeminiProvider()
    private val openAIProvider = OpenAIProvider(AIProviderType.OPENAI)
    private val customOpenAIProvider = OpenAIProvider(AIProviderType.CUSTOM_OPENAI)

    private val _configState = MutableStateFlow(loadConfig())
    val configState: StateFlow<AIProviderConfig> = _configState.asStateFlow()

    init {
        // If BuildConfig has GEMINI_API_KEY and user hasn't saved one, we initialize it
        try {
            val defaultKey = BuildConfig.GEMINI_API_KEY
            if (defaultKey.isNotBlank() && defaultKey != "MY_GEMINI_API_KEY" && !secureStorage.hasApiKey(AIProviderType.GEMINI.name)) {
                secureStorage.saveApiKey(AIProviderType.GEMINI.name, defaultKey)
                _configState.value = loadConfig()
            }
        } catch (e: Exception) { }
    }

    private fun loadConfig(): AIProviderConfig {
        val providerStr = secureStorage.getString("pref_provider_type", AIProviderType.GEMINI.name)
        val providerType = try { AIProviderType.valueOf(providerStr) } catch (e: Exception) { AIProviderType.GEMINI }
        val hasKey = secureStorage.hasApiKey(providerType.name)
        val masked = secureStorage.getMaskedApiKey(providerType.name)
        val model = secureStorage.getString("pref_selected_model_${providerType.name}", providerType.defaultModel)
        val baseUrl = secureStorage.getString("pref_base_url_${providerType.name}", providerType.defaultBaseUrl)
        val temp = secureStorage.getFloat("pref_temperature", 0.4f)
        val maxTokens = secureStorage.getInt("pref_max_tokens", 4096)
        val useSeparate = secureStorage.getBoolean("pref_use_separate_models", false)
        val ocr = secureStorage.getString("pref_model_ocr", model)
        val gen = secureStorage.getString("pref_model_gen", model)
        val expl = secureStorage.getString("pref_model_expl", model)
        val web = secureStorage.getString("pref_model_web", model)

        return AIProviderConfig(
            providerType = providerType,
            hasSavedKey = hasKey,
            maskedKey = masked,
            selectedModel = model,
            customBaseUrl = baseUrl,
            temperature = temp,
            maxTokens = maxTokens,
            useSeparateModels = useSeparate,
            ocrModel = ocr,
            generationModel = gen,
            explanationModel = expl,
            webAnalysisModel = web
        )
    }

    fun getProvider(type: AIProviderType): AIProvider {
        return when (type) {
            AIProviderType.GEMINI -> geminiProvider
            AIProviderType.OPENAI -> openAIProvider
            AIProviderType.CUSTOM_OPENAI -> customOpenAIProvider
        }
    }

    fun setProvider(type: AIProviderType) {
        secureStorage.saveString("pref_provider_type", type.name)
        _configState.value = loadConfig()
    }

    fun saveApiKey(apiKey: String) {
        val currentProvider = _configState.value.providerType
        secureStorage.saveApiKey(currentProvider.name, apiKey)
        _configState.value = loadConfig()
    }

    fun removeApiKey() {
        val currentProvider = _configState.value.providerType
        secureStorage.removeApiKey(currentProvider.name)
        _configState.value = loadConfig()
    }

    fun saveModel(modelId: String) {
        val currentProvider = _configState.value.providerType
        secureStorage.saveString("pref_selected_model_${currentProvider.name}", modelId)
        _configState.value = loadConfig()
    }

    fun saveCustomBaseUrl(url: String) {
        val currentProvider = _configState.value.providerType
        secureStorage.saveString("pref_base_url_${currentProvider.name}", url)
        _configState.value = loadConfig()
    }

    fun saveParameters(temperature: Float, maxTokens: Int) {
        secureStorage.saveFloat("pref_temperature", temperature)
        secureStorage.saveInt("pref_max_tokens", maxTokens)
        _configState.value = loadConfig()
    }

    fun saveTaskModels(
        useSeparate: Boolean,
        ocr: String,
        gen: String,
        expl: String,
        web: String
    ) {
        secureStorage.saveBoolean("pref_use_separate_models", useSeparate)
        secureStorage.saveString("pref_model_ocr", ocr)
        secureStorage.saveString("pref_model_gen", gen)
        secureStorage.saveString("pref_model_expl", expl)
        secureStorage.saveString("pref_model_web", web)
        _configState.value = loadConfig()
    }

    suspend fun testConnection(
        providerType: AIProviderType = _configState.value.providerType,
        apiKeyInput: String? = null,
        modelInput: String? = null,
        baseUrlInput: String? = null
    ): Result<String> {
        val key = if (!apiKeyInput.isNullOrBlank()) apiKeyInput else secureStorage.getApiKey(providerType.name) ?: ""
        val model = if (!modelInput.isNullOrBlank()) modelInput else _configState.value.selectedModel
        val baseUrl = if (!baseUrlInput.isNullOrBlank()) baseUrlInput else _configState.value.customBaseUrl

        val provider = getProvider(providerType)
        return provider.testConnection(key, model, baseUrl)
    }

    suspend fun fetchModels(): Result<List<AIModelInfo>> {
        val config = _configState.value
        val key = secureStorage.getApiKey(config.providerType.name) ?: ""
        val provider = getProvider(config.providerType)
        return provider.fetchAvailableModels(key, config.customBaseUrl)
    }

    suspend fun executeTextTask(taskType: AITaskType, prompt: String): Result<String> {
        val config = _configState.value
        val key = secureStorage.getApiKey(config.providerType.name) ?: ""
        if (key.isBlank()) return Result.failure(Exception("No API key configured for ${config.providerType.displayName}."))

        val model = if (config.useSeparateModels) {
            when (taskType) {
                AITaskType.QUESTION_GENERATION -> config.generationModel
                AITaskType.SIMILAR_QUESTION -> config.generationModel
                AITaskType.EXPLANATION -> config.explanationModel
                AITaskType.PERFORMANCE_ANALYSIS -> config.explanationModel
                AITaskType.WEB_ANALYSIS -> config.webAnalysisModel
                else -> config.selectedModel
            }
        } else {
            config.selectedModel
        }

        val provider = getProvider(config.providerType)
        return provider.generateText(
            prompt = prompt,
            model = model,
            apiKey = key,
            temperature = config.temperature,
            maxTokens = config.maxTokens,
            baseUrl = config.customBaseUrl
        )
    }

    suspend fun executeImageTask(taskType: AITaskType, prompt: String, bitmap: Bitmap): Result<String> {
        val config = _configState.value
        val key = secureStorage.getApiKey(config.providerType.name) ?: ""
        if (key.isBlank()) return Result.failure(Exception("No API key configured for ${config.providerType.displayName}."))

        val model = if (config.useSeparateModels && taskType == AITaskType.DOCUMENT_OCR) {
            config.ocrModel
        } else {
            config.selectedModel
        }

        val provider = getProvider(config.providerType)
        return provider.generateWithImage(
            prompt = prompt,
            bitmap = bitmap,
            model = model,
            apiKey = key,
            temperature = config.temperature,
            maxTokens = config.maxTokens,
            baseUrl = config.customBaseUrl
        )
    }

    // Export non-sensitive settings (excluding API keys)
    fun exportSettingsJson(): String {
        val config = _configState.value
        val json = org.json.JSONObject().apply {
            put("providerType", config.providerType.name)
            put("selectedModel", config.selectedModel)
            put("customBaseUrl", config.customBaseUrl)
            put("temperature", config.temperature)
            put("maxTokens", config.maxTokens)
            put("useSeparateModels", config.useSeparateModels)
            put("ocrModel", config.ocrModel)
            put("generationModel", config.generationModel)
            put("explanationModel", config.explanationModel)
            put("webAnalysisModel", config.webAnalysisModel)
            put("exportedAt", System.currentTimeMillis())
        }
        return json.toString(2)
    }

    fun importSettingsJson(jsonString: String): Boolean {
        return try {
            val json = org.json.JSONObject(jsonString)
            val pTypeStr = json.optString("providerType", AIProviderType.GEMINI.name)
            val pType = AIProviderType.valueOf(pTypeStr)
            setProvider(pType)
            saveModel(json.optString("selectedModel", pType.defaultModel))
            saveCustomBaseUrl(json.optString("customBaseUrl", pType.defaultBaseUrl))
            saveParameters(
                json.optDouble("temperature", 0.4).toFloat(),
                json.optInt("maxTokens", 4096)
            )
            saveTaskModels(
                json.optBoolean("useSeparateModels", false),
                json.optString("ocrModel", pType.defaultModel),
                json.optString("generationModel", pType.defaultModel),
                json.optString("explanationModel", pType.defaultModel),
                json.optString("webAnalysisModel", pType.defaultModel)
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}
