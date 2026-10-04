package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.AIModelInfo
import com.example.data.ai.AIProviderType
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIProviderSettingsScreen(
    viewModel: TestForgeViewModel,
    onNavigateBack: () -> Unit
) {
    val config by viewModel.aiConfig.collectAsStateWithLifecycle()
    val availableModels by viewModel.availableModels.collectAsStateWithLifecycle()
    val isTesting by viewModel.isTestingConnection.collectAsStateWithLifecycle()
    val testResult by viewModel.connectionTestResult.collectAsStateWithLifecycle()

    var apiKeyInput by remember { mutableStateOf("") }
    var showApiKey by remember { mutableStateOf(false) }
    var customModelInput by remember { mutableStateOf("") }
    var customBaseUrlInput by remember { mutableStateOf(config.customBaseUrl) }
    var temperature by remember { mutableFloatStateOf(config.temperature) }
    var maxTokens by remember { mutableIntStateOf(config.maxTokens) }

    var useSeparate by remember { mutableStateOf(config.useSeparateModels) }
    var ocrModel by remember { mutableStateOf(config.ocrModel) }
    var genModel by remember { mutableStateOf(config.generationModel) }
    var explModel by remember { mutableStateOf(config.explanationModel) }
    var webModel by remember { mutableStateOf(config.webAnalysisModel) }

    LaunchedEffect(config.providerType) {
        viewModel.refreshAvailableModels()
        customBaseUrlInput = config.customBaseUrl
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Provider & BYOK", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Bring Your Own Key (BYOK)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Connect Google Gemini, OpenAI, or local/compatible models. Keys are encrypted on-device via Android Keystore.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 16.sp
                    )
                }
            }

            // Provider Selector Tabs
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Active AI Provider", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AIProviderType.values().forEach { provider ->
                                val isSelected = config.providerType == provider
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectAIProvider(provider) },
                                    label = {
                                        Text(
                                            text = when (provider) {
                                                AIProviderType.GEMINI -> "Gemini"
                                                AIProviderType.OPENAI -> "OpenAI"
                                                AIProviderType.CUSTOM_OPENAI -> "Custom"
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = DarkSurfaceElevated,
                                        labelColor = TextSecondaryDark
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Custom Base URL if Custom/OpenAI Compatible
            if (config.providerType == AIProviderType.CUSTOM_OPENAI) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("API Base URL", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            OutlinedTextField(
                                value = customBaseUrlInput,
                                onValueChange = {
                                    customBaseUrlInput = it
                                    viewModel.saveCustomBaseUrl(it)
                                },
                                placeholder = { Text("https://api.groq.com/openai/v1/", color = TextSecondaryDark, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )
                            Text("Supports Groq, OpenRouter, Together AI, Ollama, LM Studio, or local servers.", fontSize = 10.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }

            // API Key Input Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("API Key", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (config.hasSavedKey) DifficultyEasy.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (config.hasSavedKey) "✓ Key Saved (${config.maskedKey})" else "No Key Set",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (config.hasSavedKey) DifficultyEasy else WarningAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            placeholder = { Text(if (config.hasSavedKey) "Enter new key to replace..." else "Paste your ${config.providerType.displayName} API Key...", color = TextSecondaryDark, fontSize = 12.sp) },
                            visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showApiKey = !showApiKey }) {
                                    Icon(
                                        imageVector = if (showApiKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle key visibility",
                                        tint = TextSecondaryDark
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = DarkBorder
                            )
                        )

                        // Action buttons: Save, Test, Remove
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (apiKeyInput.isNotBlank()) {
                                        viewModel.saveApiKey(apiKeyInput.trim())
                                        apiKeyInput = ""
                                    }
                                },
                                enabled = apiKeyInput.isNotBlank(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.testAIConnection(
                                        apiKeyInput = if (apiKeyInput.isNotBlank()) apiKeyInput.trim() else null,
                                        modelInput = config.selectedModel,
                                        baseUrlInput = customBaseUrlInput
                                    )
                                },
                                enabled = !isTesting && (config.hasSavedKey || apiKeyInput.isNotBlank()),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isTesting) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Test Ping", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }

                            if (config.hasSavedKey) {
                                OutlinedButton(
                                    onClick = { viewModel.removeApiKey() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DifficultyHard),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DifficultyHard.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Test Connection Result Badge
                        testResult?.let { (success, msg) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (success) DifficultyEasy.copy(alpha = 0.15f) else DifficultyHard.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (success) DifficultyEasy else DifficultyHard),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = if (success) DifficultyEasy else DifficultyHard,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = msg,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (success) DifficultyEasy else DifficultyHard
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Model Selection Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Active Model: ${config.selectedModel}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                            IconButton(
                                onClick = { viewModel.refreshAvailableModels() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh models", tint = BrandSecondary, modifier = Modifier.size(18.dp))
                            }
                        }

                        // Available models chips
                        if (availableModels.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                availableModels.forEach { modelInfo ->
                                    val isSelected = config.selectedModel == modelInfo.id
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) BrandPrimary.copy(alpha = 0.2f) else DarkSurfaceElevated,
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary) else null,
                                        onClick = { viewModel.selectAIModel(modelInfo.id) }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = modelInfo.displayName,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = TextPrimaryDark
                                                )
                                                if (modelInfo.description.isNotBlank()) {
                                                    Text(modelInfo.description, fontSize = 11.sp, color = TextSecondaryDark)
                                                }
                                            }

                                            if (modelInfo.supportsVision) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF0284C7).copy(alpha = 0.2f)
                                                ) {
                                                    Text("Vision", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Manual Custom Model Entry
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customModelInput,
                                onValueChange = { customModelInput = it },
                                placeholder = { Text("Manual model ID (e.g. gemini-3.5-flash)", color = TextSecondaryDark, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )

                            Button(
                                onClick = {
                                    if (customModelInput.isNotBlank()) {
                                        viewModel.selectAIModel(customModelInput.trim())
                                        customModelInput = ""
                                    }
                                },
                                enabled = customModelInput.isNotBlank(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                                Text("Set")
                            }
                        }
                    }
                }
            }

            // Generation Parameters Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Model Hyperparameters", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Temperature: ${String.format("%.2f", temperature)}", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(if (temperature <= 0.3f) "Precise / Deterministic" else "Creative / Varied", fontSize = 11.sp, color = BrandSecondary)
                        }

                        Slider(
                            value = temperature,
                            onValueChange = {
                                temperature = it
                                viewModel.saveAIParameters(it, maxTokens)
                            },
                            valueRange = 0.0f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = BrandPrimary, activeTrackColor = BrandPrimary)
                        )
                    }
                }
            }

            // Task-Specific Model Customization
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Separate Models for Tasks", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                Text("Assign specialized models per task", fontSize = 11.sp, color = TextSecondaryDark)
                            }

                            Switch(
                                checked = useSeparate,
                                onCheckedChange = {
                                    useSeparate = it
                                    viewModel.saveSeparateModelsConfig(it, ocrModel, genModel, explModel, webModel)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = BrandPrimary, checkedTrackColor = BrandPrimary.copy(alpha = 0.3f))
                            )
                        }

                        AnimatedVisibility(visible = useSeparate) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TaskModelRow("OCR & Vision", ocrModel) {
                                    ocrModel = it
                                    viewModel.saveSeparateModelsConfig(true, it, genModel, explModel, webModel)
                                }
                                TaskModelRow("Question Generation", genModel) {
                                    genModel = it
                                    viewModel.saveSeparateModelsConfig(true, ocrModel, it, explModel, webModel)
                                }
                                TaskModelRow("Step Explanations", explModel) {
                                    explModel = it
                                    viewModel.saveSeparateModelsConfig(true, ocrModel, genModel, it, webModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskModelRow(
    taskName: String,
    currentModel: String,
    onModelChange: (String) -> Unit
) {
    var text by remember(currentModel) { mutableStateOf(currentModel) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(taskName, fontSize = 12.sp, color = TextPrimaryDark, modifier = Modifier.weight(0.45f))
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onModelChange(it)
            },
            modifier = Modifier.weight(0.55f),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
            ),
            singleLine = true
        )
    }
}
