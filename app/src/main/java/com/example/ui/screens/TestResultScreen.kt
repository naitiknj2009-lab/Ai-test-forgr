package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuestionEntity
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.QuestionOptionCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
    viewModel: TestForgeViewModel,
    onNavigateHome: () -> Unit,
    onStartPracticeTest: () -> Unit
) {
    val attempt by viewModel.lastCompletedAttempt.collectAsStateWithLifecycle()
    val analysis by viewModel.lastAnalysis.collectAsStateWithLifecycle()
    val questions by viewModel.testQuestions.collectAsStateWithLifecycle()
    val userAnswers by viewModel.userAnswers.collectAsStateWithLifecycle()

    val currentAttempt = attempt ?: return

    val minutes = currentAttempt.timeTakenSeconds / 60
    val seconds = currentAttempt.timeTakenSeconds % 60
    val timeUsedFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Report", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                actions = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = TextPrimaryDark)
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
            // Hero Score Banner
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BrandPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "TEST COMPLETE 🎉",
                                color = BrandPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = currentAttempt.testTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScoreMetric(
                                title = "Score",
                                value = "${currentAttempt.score}/${currentAttempt.totalQuestions}",
                                color = if (currentAttempt.accuracyPercent >= 70) DifficultyEasy else DifficultyMedium
                            )
                            ScoreMetric(
                                title = "Accuracy",
                                value = "${currentAttempt.accuracyPercent.toInt()}%",
                                color = BrandSecondary
                            )
                            ScoreMetric(
                                title = "Time Used",
                                value = timeUsedFormatted,
                                color = WarningAmber
                            )
                        }

                        HorizontalDivider(color = DarkBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                        // Difficulty breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DifficultyScoreItem(label = "🟢 Easy", score = currentAttempt.easyScore)
                            DifficultyScoreItem(label = "🟡 Medium", score = currentAttempt.mediumScore)
                            DifficultyScoreItem(label = "🔴 Hard", score = currentAttempt.hardScore)
                        }
                    }
                }
            }

            // AI Diagnostic Performance Analysis Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0xFF0F172A).copy(alpha = 0.9f),
                    borderColor = BrandPrimary.copy(alpha = 0.4f)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(24.dp))
                            Text(
                                text = "AI Diagnostic Assessment",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }

                        Text(
                            text = analysis?.summary ?: "You demonstrated solid mastery on basic definitions and direct formulas, but multi-step numerical calculation steps require focused practice.",
                            fontSize = 13.sp,
                            color = TextSecondaryDark,
                            lineHeight = 18.sp
                        )

                        // Strong & Weak topics pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Strong
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DifficultyEasy.copy(alpha = 0.1f))
                                    .border(1.dp, DifficultyEasy.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("💪 Strong Topics", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DifficultyEasy)
                                val strongList = analysis?.strongTopics ?: listOf("Fundamental Concepts")
                                strongList.take(2).forEach { t ->
                                    Text("• $t", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                            }

                            // Weak
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DifficultyHard.copy(alpha = 0.1f))
                                    .border(1.dp, DifficultyHard.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🎯 Weak Areas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DifficultyHard)
                                val weakList = analysis?.weakTopics ?: listOf("Complex Calculations")
                                weakList.take(2).forEach { t ->
                                    Text("• $t", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                            }
                        }

                        // Practice My Weak Areas Button
                        Button(
                            onClick = {
                                val weak = analysis?.weakTopics ?: listOf("Kinematics & Friction")
                                viewModel.createWeakAreaPracticeTest(weak) {
                                    onStartPracticeTest()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Practice My Weak Areas (5-Q Adaptive Drill)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Question Review Section Header
            item {
                Text(
                    text = "Question-by-Question Review",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }

            // Question items review
            itemsIndexed(questions) { index, q ->
                val userAns = userAnswers[q.id] ?: "Not Attempted"
                val isCorrect = userAns.trim().startsWith(q.correctAnswer.take(2).trim(), ignoreCase = true) ||
                        userAns.trim().equals(q.correctAnswer.trim(), ignoreCase = true)

                ReviewQuestionCard(
                    index = index + 1,
                    question = q,
                    userAnswer = userAns,
                    isCorrect = isCorrect
                )
            }
        }
    }
}

@Composable
private fun ScoreMetric(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(title, fontSize = 11.sp, color = TextSecondaryDark)
    }
}

@Composable
private fun DifficultyScoreItem(label: String, score: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TextSecondaryDark)
        Text(score, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
    }
}

@Composable
private fun ReviewQuestionCard(
    index: Int,
    question: QuestionEntity,
    userAnswer: String,
    isCorrect: Boolean
) {
    val optionsList = remember(question.optionsJson) {
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(question.optionsJson)
            for (i in 0 until arr.length()) list.add(arr.getString(i))
        } catch (e: Exception) { }
        list
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
        borderColor = if (isCorrect) DifficultyEasy.copy(alpha = 0.5f) else DifficultyHard.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCorrect) DifficultyEasy.copy(alpha = 0.2f) else DifficultyHard.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isCorrect) "Q$index • Correct ✅" else "Q$index • Incorrect ❌",
                            color = if (isCorrect) DifficultyEasy else DifficultyHard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    DifficultyBadge(difficulty = question.difficulty)
                }

                Text(
                    text = question.topic,
                    fontSize = 10.sp,
                    color = BrandSecondary
                )
            }

            Text(
                text = question.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            // User Answer vs Correct Answer
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCorrect) DifficultyEasy.copy(alpha = 0.1f) else DifficultyHard.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your Answer: $userAnswer",
                        color = if (isCorrect) DifficultyEasy else DifficultyHard,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                if (!isCorrect) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DifficultyEasy.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Correct Answer: ${question.correctAnswer}",
                            color = DifficultyEasy,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Step-by-Step Explanation
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("💡 Step-by-Step Explanation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                    Text(question.explanation, fontSize = 11.sp, color = TextSecondaryDark, lineHeight = 15.sp)
                }
            }
        }
    }
}
