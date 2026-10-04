package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.TestAttemptEntity
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: TestForgeViewModel,
    onNavigateToUpload: () -> Unit,
    onNavigateToGenerate: () -> Unit,
    onNavigateToCreateTest: () -> Unit,
    onNavigateToWebSearch: () -> Unit,
    onNavigateToQuestionBank: () -> Unit,
    onNavigateToMistakes: () -> Unit,
    onNavigateToAttemptResult: (Long) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val allAttempts by viewModel.allAttempts.collectAsStateWithLifecycle()
    val unresolvedMistakes by viewModel.unresolvedMistakes.collectAsStateWithLifecycle()

    val totalQuestions = allQuestions.size
    val testsCount = allAttempts.size
    val avgAccuracy = if (allAttempts.isNotEmpty()) {
        allAttempts.map { it.accuracyPercent }.average().toInt()
    } else 78

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Header & Greeting
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Good Morning, ${userProfile.name.split(" ").first()}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text("👋", fontSize = 20.sp)
                        }
                        Text(
                            text = "${userProfile.classGrade} • Target: ${userProfile.targetExam}",
                            fontSize = 13.sp,
                            color = TextSecondaryDark
                        )
                    }

                    // Streak Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFEA580C).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFF97316).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🔥", fontSize = 14.sp)
                        Text(
                            text = "${userProfile.streakDays}d Streak",
                            color = Color(0xFFFB923C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Hero Dashboard Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(135.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                BrandPrimary.copy(alpha = 0.85f),
                                BrandTertiary.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "⚡ AI TEST SIMULATOR",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Forge Your Next Exam",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Extract PDF questions or generate adaptive drills instantly.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 14.sp
                        )
                    }

                    Button(
                        onClick = onNavigateToCreateTest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = BrandPrimaryDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Start Test", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Stats Row Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Question Bank",
                        value = "$totalQuestions",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        iconColor = BrandSecondary,
                        subtitle = "Active pool",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Tests Taken",
                        value = "$testsCount",
                        icon = Icons.Default.CheckCircleOutline,
                        iconColor = BrandPrimary,
                        subtitle = "Completed",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Avg Accuracy",
                        value = "$avgAccuracy%",
                        icon = Icons.Default.Analytics,
                        iconColor = DifficultyEasy,
                        subtitle = "Performance",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Mistakes",
                        value = "${unresolvedMistakes.size}",
                        icon = Icons.Default.ErrorOutline,
                        iconColor = DifficultyHard,
                        subtitle = "Need practice",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToMistakes() }
                    )
                }
            }
        }

        // Main Actions Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Core Actions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "Upload Questions",
                        description = "PDF, photos & scans",
                        icon = Icons.Default.UploadFile,
                        accentColor = BrandSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToUpload
                    )

                    ActionTile(
                        title = "Generate AI",
                        description = "Topic & class tests",
                        icon = Icons.Default.Psychology,
                        accentColor = BrandPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToGenerate
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "Create Test",
                        description = "10-Q custom timer",
                        icon = Icons.Default.Timer,
                        accentColor = BrandTertiary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCreateTest
                    )

                    ActionTile(
                        title = "Find Online",
                        description = "Educational web bank",
                        icon = Icons.Default.Language,
                        accentColor = Color(0xFF0EA5E9),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToWebSearch
                    )
                }
            }
        }

        // Recent Tests Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Test Attempts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    if (allAttempts.isNotEmpty()) {
                        Text(
                            text = "${allAttempts.size} total",
                            fontSize = 12.sp,
                            color = BrandSecondary
                        )
                    }
                }

                if (allAttempts.isEmpty()) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Quiz,
                                contentDescription = null,
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No test attempts yet",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryDark,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Start a 10-question test to track your accuracy and diagnostic feedback.",
                                color = TextSecondaryDark,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Button(
                                onClick = onNavigateToCreateTest,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                Text("Take First Test", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    allAttempts.take(3).forEach { attempt ->
                        RecentAttemptCard(
                            attempt = attempt,
                            onClick = { onNavigateToAttemptResult(attempt.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(14.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondaryDark,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun RecentAttemptCard(
    attempt: TestAttemptEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(attempt.completedAt))

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = attempt.subject,
                        fontSize = 11.sp,
                        color = BrandSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("•", color = TextSecondaryDark, fontSize = 10.sp)
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = TextSecondaryDark
                    )
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
                    text = "${attempt.accuracyPercent.toInt()}% accuracy",
                    fontSize = 11.sp,
                    color = TextSecondaryDark
                )
            }
        }
    }
}
