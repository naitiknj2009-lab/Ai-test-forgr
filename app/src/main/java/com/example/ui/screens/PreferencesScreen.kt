package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun PreferencesScreen(
    viewModel: TestForgeViewModel,
    onNavigateToComplete: () -> Unit
) {
    var selectedClass by remember { mutableStateOf("Class 11") }
    var selectedExamGoal by remember { mutableStateOf("JEE / Competitive Exams") }

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Customize Your Learning",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Optional: Select your grade level and target goals so AI generates appropriately calibrated questions.",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // Class / Grade Selector
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Select Class / Grade Level", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                        val classOptions = listOf(
                            "Class 6", "Class 7", "Class 8", "Class 9",
                            "Class 10", "Class 11", "Class 12", "College / University", "Other"
                        )

                        // 3 column chip layout
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            classOptions.chunked(3).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { item ->
                                        val isSelected = selectedClass == item
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedClass = item },
                                            label = { Text(item, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BrandPrimary,
                                                selectedLabelColor = Color.White,
                                                containerColor = DarkSurfaceElevated,
                                                labelColor = TextSecondaryDark
                                            )
                                        )
                                    }
                                    if (rowItems.size < 3) {
                                        Spacer(Modifier.weight((3 - rowItems.size).toFloat()))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Exam Goal Selector
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Target Exam Goal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                        val examOptions = listOf(
                            "School / Board Exams", "JEE Main & Advanced",
                            "NEET Medical", "UPSC / Civil Services",
                            "SAT / AP STEM", "Other Competitive Exams"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            examOptions.chunked(2).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { item ->
                                        val isSelected = selectedExamGoal == item
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedExamGoal = item },
                                            label = { Text(item, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f),
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
                    }
                }
            }

            // Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            viewModel.updateUserProfile(classGrade = selectedClass, examGoal = selectedExamGoal)
                            onNavigateToComplete()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Save & Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.updateUserProfile(classGrade = selectedClass, examGoal = selectedExamGoal)
                            onNavigateToComplete()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Text("Skip for Now", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
