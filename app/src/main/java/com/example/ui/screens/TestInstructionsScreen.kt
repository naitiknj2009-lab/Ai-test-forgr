package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestInstructionsScreen(
    viewModel: TestForgeViewModel,
    onNavigateBack: () -> Unit,
    onBeginTest: () -> Unit
) {
    val activeTest by viewModel.activeTest.collectAsStateWithLifecycle()
    val testQuestions by viewModel.testQuestions.collectAsStateWithLifecycle()

    val test = activeTest ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Instructions", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
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
                    onClick = onBeginTest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("I'M READY • BEGIN TEST", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
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
            // Test Overview Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = test.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryDark
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandPrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = test.subject,
                                    color = BrandPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            DifficultyBadge(difficulty = test.difficultyMode)
                        }

                        HorizontalDivider(color = DarkBorder, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            InstructionStat(label = "Questions", value = "${testQuestions.size}")
                            InstructionStat(label = "Time Limit", value = "${test.durationMinutes} min")
                            InstructionStat(label = "Max Score", value = "${testQuestions.size}")
                        }
                    }
                }
            }

            // Rules & Instructions Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Important Guidelines",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )

                        RuleItem(
                            number = "1",
                            text = "The countdown timer will start immediately upon clicking 'Begin Test'."
                        )
                        RuleItem(
                            number = "2",
                            text = "Timer runs in background; test auto-submits once the timer reaches 00:00."
                        )
                        RuleItem(
                            number = "3",
                            text = "You can flag questions using 'Mark for Review' and jump to any question via the question drawer."
                        )
                        RuleItem(
                            number = "4",
                            text = "Answers are auto-saved in real time. You can change or clear your choice before submitting."
                        )
                        RuleItem(
                            number = "5",
                            text = "Incorrect answers will be automatically archived into your Mistake Notebook for future adaptive practice."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimaryDark)
        Text(label, fontSize = 11.sp, color = TextSecondaryDark)
    }
}

@Composable
private fun RuleItem(number: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = DarkSurfaceElevated,
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
            }
        }
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 16.sp
        )
    }
}
