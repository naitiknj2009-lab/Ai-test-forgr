package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TestAttemptEntity
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryAnalyticsScreen(
    viewModel: TestForgeViewModel,
    onNavigateBack: () -> Unit,
    onViewAttempt: (Long) -> Unit
) {
    val allAttempts by viewModel.allAttempts.collectAsStateWithLifecycle()

    val totalTests = allAttempts.size
    val averageScore = if (allAttempts.isNotEmpty()) allAttempts.map { it.score.toFloat() / it.totalQuestions }.average() * 100f else 80f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test History & Analytics", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
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
            // Overall Analytics Banner
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Performance Progression", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalTests", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = BrandPrimary)
                                Text("Tests Taken", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${averageScore.toInt()}%", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = DifficultyEasy)
                                Text("Avg Accuracy", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Tier 1", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = WarningAmber)
                                Text("Mastery Level", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                        }

                        HorizontalDivider(color = DarkBorder, thickness = 1.dp)

                        // Visual Mastery Progress Bars
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            MasteryBar(label = "🟢 Easy Questions", percentage = 0.92f, color = DifficultyEasy)
                            MasteryBar(label = "🟡 Medium Questions", percentage = 0.78f, color = DifficultyMedium)
                            MasteryBar(label = "🔴 Hard Questions", percentage = 0.58f, color = DifficultyHard)
                        }
                    }
                }
            }

            // Test History Section Header
            item {
                Text("All Completed Attempts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            }

            if (allAttempts.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(36.dp))
                            Text("No test history recorded yet", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("Complete your first test to see your history and trends.", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                }
            } else {
                items(allAttempts, key = { it.id }) { attempt ->
                    HistoryItemCard(
                        attempt = attempt,
                        onClick = { onViewAttempt(attempt.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MasteryBar(label: String, percentage: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = TextSecondaryDark)
            Text("${(percentage * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = { percentage },
            color = color,
            trackColor = DarkSurfaceElevated,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

@Composable
private fun HistoryItemCard(
    attempt: TestAttemptEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(attempt.completedAt))

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = attempt.testTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(attempt.subject, fontSize = 11.sp, color = BrandSecondary, fontWeight = FontWeight.SemiBold)
                    Text("•", color = TextSecondaryDark, fontSize = 10.sp)
                    Text(dateStr, fontSize = 11.sp, color = TextSecondaryDark)
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${attempt.score}/${attempt.totalQuestions}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (attempt.accuracyPercent >= 70) DifficultyEasy else DifficultyMedium
                )
                Text(
                    text = "${attempt.accuracyPercent.toInt()}%",
                    fontSize = 11.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
