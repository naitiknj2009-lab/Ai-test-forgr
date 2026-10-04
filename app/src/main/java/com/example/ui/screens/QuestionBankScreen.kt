package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.QuestionEntity
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    viewModel: TestForgeViewModel,
    onNavigateToGenerate: () -> Unit,
    onNavigateToCreateTest: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubjectFilter.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficultyFilter.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
    val questions by viewModel.filteredQuestions.collectAsStateWithLifecycle()

    var selectedQuestionForDetail by remember { mutableStateOf<QuestionEntity?>(null) }
    var whyHardQuestion by remember { mutableStateOf<QuestionEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToGenerate,
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Text("AI Generator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header & Counter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Question Bank",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "${questions.size} questions available in bank",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }

                    Button(
                        onClick = onNavigateToCreateTest,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryDark),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Create Test", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search topics, chapters, formula keywords...", color = TextSecondaryDark, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondaryDark)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    singleLine = true
                )
            }

            // Filter Chips Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Subject filters
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Physics", "Chemistry", "Mathematics", "Biology").forEach { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { viewModel.setSubjectFilter(subj) },
                                label = { Text(subj, fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurfaceElevated,
                                    labelColor = TextSecondaryDark
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedSubject == subj,
                                    borderColor = DarkBorder,
                                    selectedBorderColor = BrandPrimary
                                )
                            )
                        }
                    }

                    // Difficulty filters
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "EASY", "MEDIUM", "HARD").forEach { diff ->
                            val label = if (diff == "All") "All Difficulties" else when (diff) {
                                "EASY" -> "🟢 Easy"
                                "MEDIUM" -> "🟡 Medium"
                                else -> "🔴 Hard"
                            }
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { viewModel.setDifficultyFilter(diff) },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandSecondary,
                                    selectedLabelColor = Color.Black,
                                    containerColor = DarkSurfaceElevated,
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }
            }

            // Questions List
            if (questions.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(28.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(40.dp))
                            Text("No questions matched filters", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("Try clearing search keywords or generate new questions with AI.", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                }
            } else {
                items(questions, key = { it.id }) { question ->
                    QuestionBankItemCard(
                        question = question,
                        onToggleImportant = { viewModel.toggleImportant(question) },
                        onDelete = { viewModel.deleteQuestion(question) },
                        onSimilar = {
                            viewModel.generateSimilarQuestion(question) {
                                // Handled in VM
                            }
                        },
                        onWhyHard = { whyHardQuestion = question }
                    )
                }
            }
        }
    }

    // "Why is it Hard?" AI Modal
    whyHardQuestion?.let { q ->
        AlertDialog(
            onDismissRequest = { whyHardQuestion = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = BrandPrimary)
                    Text("AI Difficulty Breakdown", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DifficultyBadge(difficulty = q.difficulty)

                    Text(
                        text = "Why classified as ${q.difficulty.uppercase()}:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = q.difficultyReason.ifBlank {
                                when (q.difficulty.uppercase()) {
                                    "EASY" -> "Direct recall of basic definition or single-variable formula."
                                    "HARD" -> "Requires multi-concept coupling, non-linear algebraic manipulation, or advanced calculus reasoning."
                                    else -> "Requires 2 distinct conceptual steps and calculation synthesis."
                                }
                            },
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Text(
                        text = "Topic: ${q.subject} → ${q.chapter} (${q.topic})",
                        fontSize = 11.sp,
                        color = BrandSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { whyHardQuestion = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Got it")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun QuestionBankItemCard(
    question: QuestionEntity,
    onToggleImportant: () -> Unit,
    onDelete: () -> Unit,
    onSimilar: () -> Unit,
    onWhyHard: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DifficultyBadge(difficulty = question.difficulty)

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceElevated
                    ) {
                        Text(
                            text = question.subject,
                            color = BrandSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceElevated
                    ) {
                        Text(
                            text = question.chapter,
                            color = TextSecondaryDark,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleImportant, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = if (question.isImportant) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star",
                            tint = if (question.isImportant) WarningAmber else TextSecondaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = DifficultyHard,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Question Text
            Text(
                text = question.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                lineHeight = 20.sp
            )

            // Options Preview or Expanded
            if (optionsList.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val displayOptions = if (expanded) optionsList else optionsList.take(2)
                    displayOptions.forEach { opt ->
                        val isCorrect = opt.trim().startsWith(question.correctAnswer.take(2).trim(), ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (expanded && isCorrect) DifficultyEasy.copy(alpha = 0.15f) else DarkSurfaceElevated.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                color = if (expanded && isCorrect) DifficultyEasy else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Expanded Explanation
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("💡 Correct Answer & Explanation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                            Text(question.correctAnswer, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DifficultyEasy)
                            Text(question.explanation, fontSize = 11.sp, color = TextSecondaryDark, lineHeight = 15.sp)
                        }
                    }
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(
                        onClick = onWhyHard,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("🧠 Why ${question.difficulty}?", fontSize = 11.sp, color = BrandSecondary)
                    }

                    TextButton(
                        onClick = onSimilar,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("🔄 Similar Q", fontSize = 11.sp, color = BrandPrimary)
                    }
                }

                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(if (expanded) "Show Less" else "Details ▼", fontSize = 11.sp, color = TextSecondaryDark)
                }
            }
        }
    }
}
