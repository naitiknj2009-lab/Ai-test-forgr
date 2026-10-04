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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MistakeEntity
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakeNotebookScreen(
    viewModel: TestForgeViewModel,
    onNavigateBack: () -> Unit,
    onStartPractice: () -> Unit
) {
    val mistakes by viewModel.unresolvedMistakes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mistake Notebook", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text("${mistakes.size} questions needing revision", fontSize = 12.sp, color = DifficultyHard)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            if (mistakes.isNotEmpty()) {
                Surface(
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val topics = mistakes.map { it.topic }.distinct()
                            viewModel.createWeakAreaPracticeTest(topics) {
                                onStartPractice()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Practice All Weak Areas (${mistakes.size})", fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (mistakes.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = DifficultyEasy, modifier = Modifier.size(48.dp))
                            Text("Mistake Notebook Clear!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)
                            Text(
                                "No active mistake entries. Great job maintaining high accuracy on your exam simulations!",
                                fontSize = 12.sp,
                                color = TextSecondaryDark,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(mistakes, key = { it.id }) { mistake ->
                    MistakeItemCard(
                        mistake = mistake,
                        onResolve = { viewModel.markMistakeResolved(mistake) },
                        onDelete = { viewModel.deleteMistake(mistake) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MistakeItemCard(
    mistake: MistakeEntity,
    onResolve: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
        borderColor = DifficultyHard.copy(alpha = 0.4f)
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
                    DifficultyBadge(difficulty = mistake.difficulty)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceElevated
                    ) {
                        Text(
                            text = "${mistake.subject} • ${mistake.topic}",
                            fontSize = 11.sp,
                            color = BrandSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onResolve, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Resolve", tint = DifficultyEasy, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Text(
                text = mistake.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DifficultyHard.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Your Error: ${mistake.selectedAnswer}",
                        fontSize = 11.sp,
                        color = DifficultyHard,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DifficultyEasy.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Correct: ${mistake.correctAnswer}",
                        fontSize = 11.sp,
                        color = DifficultyEasy,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }
            }

            // Explanation
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("💡 Concept Derivation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                    Text(mistake.explanation, fontSize = 11.sp, color = TextSecondaryDark)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onResolve,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Mark Mastered & Solved ✓", fontSize = 11.sp, color = DifficultyEasy, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
