package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuestionEntity
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiGeneratorScreen(
    viewModel: TestForgeViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTestBuilder: () -> Unit,
    onNavigateToBank: () -> Unit
) {
    var subject by remember { mutableStateOf("Physics") }
    var classGrade by remember { mutableStateOf("Class 11") }
    var chapter by remember { mutableStateOf("Laws of Motion & Friction") }
    var difficulty by remember { mutableStateOf("MIXED") }
    var questionCount by remember { mutableIntStateOf(10) }
    var questionType by remember { mutableStateOf("MCQ") }

    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generatedQuestions by viewModel.generatedQuestions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Question Generator", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            if (generatedQuestions.isNotEmpty() && !isGenerating) {
                Surface(
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.saveGeneratedQuestionsToBank()
                                onNavigateToBank()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark)
                        ) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Save All", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onNavigateToTestBuilder,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Start Test", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
            // Configuration Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = BrandPrimary)
                            Text(
                                text = "Exam Synthesis Parameters",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }

                        // Subject Selector
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Subject", fontSize = 12.sp, color = TextSecondaryDark, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Physics", "Mathematics", "Chemistry", "Biology", "Computer Science").forEach { s ->
                                    FilterChip(
                                        selected = subject == s,
                                        onClick = {
                                            subject = s
                                            if (s == "Mathematics") chapter = "Quadratic Equations"
                                            else if (s == "Chemistry") chapter = "Chemical Bonding"
                                            else if (s == "Biology") chapter = "Cell Biology & Genetics"
                                            else chapter = "Laws of Motion & Friction"
                                        },
                                        label = { Text(s, fontSize = 12.sp) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }

                        // Class Grade & Chapter Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = classGrade,
                                onValueChange = { classGrade = it },
                                label = { Text("Class / Level") },
                                modifier = Modifier.weight(0.45f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark
                                )
                            )

                            OutlinedTextField(
                                value = chapter,
                                onValueChange = { chapter = it },
                                label = { Text("Chapter / Topic") },
                                modifier = Modifier.weight(0.55f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark
                                )
                            )
                        }

                        // Difficulty Selection
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Target Difficulty", fontSize = 12.sp, color = TextSecondaryDark, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("EASY" to "🟢 Easy", "MEDIUM" to "🟡 Med", "HARD" to "🔴 Hard", "MIXED" to "⚡ Mixed").forEach { (d, label) ->
                                    FilterChip(
                                        selected = difficulty == d,
                                        onClick = { difficulty = d },
                                        label = { Text(label, fontSize = 11.sp) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Question Count Picker
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Number of Questions: $questionCount", fontSize = 12.sp, color = TextSecondaryDark, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 10, 15, 20).forEach { cnt ->
                                    FilterChip(
                                        selected = questionCount == cnt,
                                        onClick = { questionCount = cnt },
                                        label = { Text("$cnt Qs", fontSize = 12.sp) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Generate Button
                        Button(
                            onClick = {
                                viewModel.generateAIQuestions(
                                    subject = subject,
                                    classGrade = classGrade,
                                    chapter = chapter,
                                    difficulty = difficulty,
                                    count = questionCount,
                                    questionType = questionType
                                )
                            },
                            enabled = !isGenerating && chapter.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(10.dp))
                                Text("Synthesizing $questionCount Questions with AI...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Generate $questionCount $subject Questions", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Results Section
            if (generatedQuestions.isNotEmpty()) {
                item {
                    Text(
                        text = "Generated Questions (${generatedQuestions.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                itemsIndexed(generatedQuestions) { index, q ->
                    GeneratedItemCard(index = index + 1, question = q)
                }
            }
        }
    }
}

@Composable
private fun GeneratedItemCard(
    index: Int,
    question: QuestionEntity
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
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
                        color = BrandPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Q$index",
                            color = BrandPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    DifficultyBadge(difficulty = question.difficulty)
                }

                Text(
                    text = question.topic,
                    fontSize = 10.sp,
                    color = BrandSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = question.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Answer: ${question.correctAnswer}", fontSize = 12.sp, color = DifficultyEasy, fontWeight = FontWeight.Bold)
                    Text(question.explanation, fontSize = 11.sp, color = TextSecondaryDark)
                }
            }
        }
    }
}
