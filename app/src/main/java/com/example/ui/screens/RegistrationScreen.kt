package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AIProviderType
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: TestForgeViewModel,
    onNavigateToPreferences: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf(AIProviderType.GEMINI) }
    var apiKey by remember { mutableStateOf("") }
    var showApiKey by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var isVerifyingKey by remember { mutableStateOf(false) }
    var keyVerificationError by remember { mutableStateOf<String?>(null) }

    var expandedProviderDropdown by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Create Your Profile",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Set up your profile to personalize your tests and progress.",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // Input Form Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Full Name
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Full Name *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    if (it.isNotBlank()) nameError = null
                                },
                                placeholder = { Text("Enter your name", color = TextSecondaryDark, fontSize = 13.sp) },
                                isError = nameError != null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )
                            nameError?.let {
                                Text(it, color = DifficultyHard, fontSize = 11.sp)
                            }
                        }

                        // Mobile Number (Indian +91)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Mobile Number *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                            OutlinedTextField(
                                value = mobileNumber,
                                onValueChange = {
                                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                        mobileNumber = it
                                        if (it.length == 10) mobileError = null
                                    }
                                },
                                placeholder = { Text("10-digit mobile number", color = TextSecondaryDark, fontSize = 13.sp) },
                                leadingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                                    ) {
                                        Text("🇮🇳 +91", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Box(modifier = Modifier.height(18.dp).width(1.dp).background(DarkBorder))
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                isError = mobileError != null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )
                            mobileError?.let {
                                Text(it, color = DifficultyHard, fontSize = 11.sp)
                            }
                        }

                        // AI Provider Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("AI Provider", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                            ExposedDropdownMenuBox(
                                expanded = expandedProviderDropdown,
                                onExpandedChange = { expandedProviderDropdown = !expandedProviderDropdown }
                            ) {
                                OutlinedTextField(
                                    value = selectedProvider.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProviderDropdown) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimaryDark,
                                        unfocusedTextColor = TextPrimaryDark,
                                        focusedBorderColor = BrandPrimary,
                                        unfocusedBorderColor = DarkBorder
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedProviderDropdown,
                                    onDismissRequest = { expandedProviderDropdown = false },
                                    modifier = Modifier.background(DarkSurfaceElevated)
                                ) {
                                    AIProviderType.values().forEach { provider ->
                                        DropdownMenuItem(
                                            text = { Text(provider.displayName, color = TextPrimaryDark, fontSize = 13.sp) },
                                            onClick = {
                                                selectedProvider = provider
                                                expandedProviderDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Optional API Key
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("API Key (Optional)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                                Text("Optional", fontSize = 10.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                            }

                            OutlinedTextField(
                                value = apiKey,
                                onValueChange = {
                                    apiKey = it
                                    keyVerificationError = null
                                },
                                placeholder = { Text("Enter your API key", color = TextSecondaryDark, fontSize = 13.sp) },
                                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { showApiKey = !showApiKey }) {
                                        Icon(
                                            imageVector = if (showApiKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle visibility",
                                            tint = TextSecondaryDark
                                        )
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )

                            Text(
                                text = "You can add or change your API key later from Settings → AI Provider.",
                                fontSize = 11.sp,
                                color = TextSecondaryDark,
                                lineHeight = 15.sp
                            )

                            keyVerificationError?.let {
                                Text(it, color = DifficultyHard, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Create Account Button
                    Button(
                        onClick = {
                            var hasError = false
                            if (name.isBlank()) {
                                nameError = "Please enter your full name"
                                hasError = true
                            }
                            if (mobileNumber.length < 10) {
                                mobileError = "Please enter a valid 10-digit mobile number"
                                hasError = true
                            }

                            if (!hasError) {
                                viewModel.selectAIProvider(selectedProvider)
                                if (apiKey.isNotBlank()) {
                                    viewModel.saveApiKey(apiKey.trim())
                                }

                                // Update Profile
                                viewModel.updateUserProfile(name = name.trim(), mobile = mobileNumber.trim())
                                // Proceed to Preferences
                                onNavigateToPreferences()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Create Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }

                    // Continue Without AI Key Option
                    OutlinedButton(
                        onClick = {
                            var hasError = false
                            if (name.isBlank()) {
                                nameError = "Please enter your full name"
                                hasError = true
                            }
                            if (mobileNumber.length < 10) {
                                mobileError = "Please enter a valid 10-digit mobile number"
                                hasError = true
                            }

                            if (!hasError) {
                                viewModel.selectAIProvider(selectedProvider)
                                viewModel.updateUserProfile(name = name.trim(), mobile = mobileNumber.trim())
                                onNavigateToPreferences()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Text("Continue Without AI Key", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
