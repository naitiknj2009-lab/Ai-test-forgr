package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TestForgeViewModel,
    onNavigateToMistakes: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAIProvider: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val allAttempts by viewModel.allAttempts.collectAsStateWithLifecycle()
    val aiConfig by viewModel.aiConfig.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Profile & Settings", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimaryDark)
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
            // Profile Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary.copy(alpha = 0.25f))
                                .border(2.dp, BrandPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(36.dp))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(profile.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text(profile.classGrade, fontSize = 12.sp, color = BrandSecondary)
                            Text("Target: ${profile.targetExam}", fontSize = 11.sp, color = TextSecondaryDark)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥 ${profile.streakDays} Days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFB923C))
                                Text("Current Streak", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${allAttempts.size}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                Text("Tests Completed", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${allQuestions.size}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DifficultyEasy)
                                Text("Bank Questions", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                        }
                    }
                }
            }

            // AI Provider BYOK Shortcut Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToAIProvider,
                    contentPadding = PaddingValues(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    borderColor = BrandPrimary.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BrandPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("BYOK AI Provider", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandSecondary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = aiConfig.providerType.displayName,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Model: ${aiConfig.selectedModel} (${if (aiConfig.hasSavedKey) "Key Configured" else "No Key Set"})",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
                    }
                }
            }

            // Quick Access Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Study Tools", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                    SettingActionRow(
                        title = "Mistake Notebook",
                        subtitle = "Review and resolve questions you answered incorrectly",
                        icon = Icons.Default.ErrorOutline,
                        iconColor = DifficultyHard,
                        onClick = onNavigateToMistakes
                    )

                    SettingActionRow(
                        title = "Test History & Score Analytics",
                        subtitle = "View difficulty mastery and subject progression",
                        icon = Icons.Default.Analytics,
                        iconColor = BrandSecondary,
                        onClick = onNavigateToHistory
                    )

                    SettingActionRow(
                        title = "App Settings & Backup",
                        subtitle = "Configuration export/import and version info",
                        icon = Icons.Default.Settings,
                        iconColor = WarningAmber,
                        onClick = onNavigateToSettings
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(14.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Text(subtitle, fontSize = 11.sp, color = TextSecondaryDark, lineHeight = 14.sp)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
        }
    }
}
