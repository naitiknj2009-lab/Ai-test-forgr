package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    viewModel: TestForgeViewModel,
    onTestFinished: () -> Unit
) {
    val activeTest by viewModel.activeTest.collectAsStateWithLifecycle()
    val questions by viewModel.testQuestions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val userAnswers by viewModel.userAnswers.collectAsStateWithLifecycle()
    val markedForReview by viewModel.markedForReview.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val isTestActive by viewModel.isTestActive.collectAsStateWithLifecycle()

    var showSubmitConfirmation by remember { mutableStateOf(false) }
    var showPaletteSheet by remember { mutableStateOf(false) }

    // Intercept back button during exam
    BackHandler(enabled = isTestActive) {
        showSubmitConfirmation = true
    }

    val currentQuestion = questions.getOrNull(currentIndex) ?: return

    val optionsList = remember(currentQuestion.optionsJson) {
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(currentQuestion.optionsJson)
            for (i in 0 until arr.length()) list.add(arr.getString(i))
        } catch (e: Exception) { }
        list
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    val isTimeLow = remainingSeconds < 120 // < 2 mins

    val selectedAnswer = userAnswers[currentQuestion.id] ?: ""
    val isMarked = markedForReview.contains(currentQuestion.id)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = activeTest?.subject ?: "Exam Simulator",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandSecondary
                        )
                        Text(
                            text = "Question ${currentIndex + 1} of ${questions.size}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryDark
                        )
                    }

                    // Countdown Timer Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isTimeLow) DifficultyHard.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(1.dp, if (isTimeLow) DifficultyHard else BrandPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (isTimeLow) DifficultyHard else WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = formattedTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTimeLow) DifficultyHard else TextPrimaryDark
                        )
                    }
                }

                // Progress dot indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    questions.forEachIndexed { idx, q ->
                        val answered = userAnswers.containsKey(q.id)
                        val flagged = markedForReview.contains(q.id)
                        val isCurrent = idx == currentIndex

                        val dotColor = when {
                            isCurrent -> BrandPrimary
                            flagged -> WarningAmber
                            answered -> DifficultyEasy
                            else -> DarkBorder
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(dotColor)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    IconButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = currentIndex > 0,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = TextPrimaryDark)
                    }

                    // Mark for review toggle
                    OutlinedButton(
                        onClick = { viewModel.toggleMarkForReview(currentQuestion.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isMarked) WarningAmber.copy(alpha = 0.15f) else Color.Transparent,
                            contentColor = if (isMarked) WarningAmber else TextSecondaryDark
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isMarked) WarningAmber else DarkBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isMarked) Icons.Filled.Flag else Icons.Outlined.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (isMarked) "Flagged" else "Review", fontSize = 11.sp)
                    }

                    // Question Palette button
                    IconButton(
                        onClick = { showPaletteSheet = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = "Palette", tint = BrandSecondary)
                    }

                    // Next / Submit Button
                    if (currentIndex == questions.size - 1) {
                        Button(
                            onClick = { showSubmitConfirmation = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DifficultyEasy)
                        ) {
                            Text("Submit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("Next", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
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
            // Question Details Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DifficultyBadge(difficulty = currentQuestion.difficulty)

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkSurfaceElevated
                            ) {
                                Text(
                                    text = currentQuestion.topic,
                                    fontSize = 11.sp,
                                    color = BrandSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = currentQuestion.questionText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Options List
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    optionsList.forEach { optionText ->
                        val isSelected = selectedAnswer == optionText
                        QuestionOptionCard(
                            optionText = optionText,
                            isSelected = isSelected,
                            onSelect = {
                                viewModel.selectAnswer(currentQuestion.id, optionText)
                            }
                        )
                    }
                }
            }

            // Clear Answer Option
            if (selectedAnswer.isNotBlank()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.clearAnswer(currentQuestion.id) }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Clear Selection", color = TextSecondaryDark, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Question Palette Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Question Navigator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(questions) { index, q ->
                        val answered = userAnswers.containsKey(q.id)
                        val flagged = markedForReview.contains(q.id)
                        val isCurrent = index == currentIndex

                        val bg = when {
                            isCurrent -> BrandPrimary
                            flagged -> WarningAmber
                            answered -> DifficultyEasy
                            else -> DarkSurfaceElevated
                        }

                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    viewModel.jumpToQuestion(index)
                                    showPaletteSheet = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = bg.copy(alpha = if (isCurrent) 1f else 0.85f),
                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isCurrent || answered || flagged) Color.White else TextSecondaryDark
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { showPaletteSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Close Palette")
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitConfirmation) {
        val attemptedCount = userAnswers.size
        val totalCount = questions.size
        val unattemptedCount = totalCount - attemptedCount

        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            title = {
                Text(
                    text = "Submit Test?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to end your test session?",
                        fontSize = 14.sp,
                        color = TextSecondaryDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$attemptedCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DifficultyEasy)
                            Text("Answered", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$unattemptedCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (unattemptedCount > 0) WarningAmber else TextSecondaryDark)
                            Text("Unanswered", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmation = false
                        viewModel.submitTest {
                            onTestFinished()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Submit & View Results")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text("Resume Test", color = TextSecondaryDark)
                }
            },
            containerColor = DarkSurface
        )
    }
}
