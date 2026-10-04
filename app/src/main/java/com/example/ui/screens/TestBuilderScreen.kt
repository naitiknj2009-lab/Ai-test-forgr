package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestBuilderScreen(
    viewModel: TestForgeViewModel,
    onNavigateBack: () -> Unit,
    onStartInstructions: () -> Unit
) {
    var testName by remember { mutableStateOf("Physics & Mechanics Diagnostic Test") }
    var subject by remember { mutableStateOf("Physics") }
    var questionCount by remember { mutableIntStateOf(10) }
    var durationMinutes by remember { mutableIntStateOf(15) }
    var difficultyMode by remember { mutableStateOf("MIXED") }
    var questionTypeMode by remember { mutableStateOf("MCQ") }
    var sourceMode by remember { mutableStateOf("MIXED") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Test", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        viewModel.createAndStartTest(
                            title = testName,
                            subject = subject,
                            questionCount = questionCount,
                            durationMinutes = durationMinutes,
                            difficultyMode = difficultyMode,
                            questionTypeMode = questionTypeMode,
                            sourceMode = sourceMode,
                            onTestReady = onStartInstructions
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("START TEST ($questionCount Questions • $durationMinutes min)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
            // Test Name Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Test Name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        OutlinedTextField(
                            value = testName,
                            onValueChange = { testName = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                    }
                }
            }

            // Subject Selector
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Subject", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Physics", "Mathematics", "Chemistry", "Biology", "All Subjects").forEach { s ->
                                FilterChip(
                                    selected = subject == s,
                                    onClick = {
                                        subject = s
                                        testName = if (s == "All Subjects") "Comprehensive Science Test" else "$s Diagnostic Exam"
                                    },
                                    label = { Text(s, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Number of Questions (Default 10)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Number of Questions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("$questionCount Questions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5, 10, 15, 20, 30).forEach { count ->
                                FilterChip(
                                    selected = questionCount == count,
                                    onClick = { questionCount = count },
                                    label = { Text(if (count == 10) "10 (Default)" else "$count", fontSize = 11.sp) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Test Duration
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Test Duration", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("$durationMinutes Minutes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5, 10, 15, 20, 30, 45, 60).forEach { mins ->
                                FilterChip(
                                    selected = durationMinutes == mins,
                                    onClick = { durationMinutes = mins },
                                    label = { Text("$mins min", fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Difficulty Setting
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Difficulty Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "MIXED" to "⚡ Mixed (2E • 5M • 3H)",
                                "EASY" to "🟢 Easy",
                                "MEDIUM" to "🟡 Medium",
                                "HARD" to "🔴 Hard"
                            ).forEach { (mode, label) ->
                                FilterChip(
                                    selected = difficultyMode == mode,
                                    onClick = { difficultyMode = mode },
                                    label = { Text(label, fontSize = 10.sp) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Question Source Mode
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Question Source", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "MIXED" to "All Sources (Auto-Balance)",
                                "PDF_UPLOAD" to "Uploaded PDFs / Scans",
                                "BANK" to "Question Bank Only",
                                "AI_GENERATED" to "AI Fresh Questions"
                            ).forEach { (src, label) ->
                                FilterChip(
                                    selected = sourceMode == src,
                                    onClick = { sourceMode = src },
                                    label = { Text(label, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
