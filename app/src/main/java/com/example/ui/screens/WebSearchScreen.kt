package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.QuestionEntity
import com.example.data.remote.WebQuestionResult
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebSearchScreen(
    viewModel: TestForgeViewModel,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("Class 10 Maths Quadratic Equations") }
    val isSearching by viewModel.isSearchingWeb.collectAsStateWithLifecycle()
    val searchResults by viewModel.webSearchResults.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (searchResults.isEmpty()) {
            viewModel.searchWebQuestions("Class 10 Maths Quadratic Equations")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Questions Online", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
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
            // Search Bar & Topic Suggestions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Enter topic (e.g. Class 11 Physics Kinematics)", color = TextSecondaryDark, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandSecondary) },
                        trailingIcon = {
                            Button(
                                onClick = { viewModel.searchWebQuestions(searchQuery) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text("Search", fontSize = 12.sp)
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

                    // Suggestion pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Physics: Laws of Motion", "Maths: Quadratics", "Chem: Bonding").forEach { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.clickable {
                                    searchQuery = suggestion
                                    viewModel.searchWebQuestions(suggestion)
                                }
                            ) {
                                Text(
                                    text = suggestion,
                                    color = BrandSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isSearching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(color = BrandPrimary)
                            Text("Searching educational question repositories...", color = TextSecondaryDark, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                searchResults.forEach { group ->
                    item {
                        SourceGroupHeader(group = group)
                    }

                    items(group.questions) { question ->
                        WebQuestionItemCard(
                            question = question,
                            sourceName = group.sourceName,
                            sourceUrl = group.sourceUrl,
                            onAddToBank = { viewModel.addWebQuestionToBank(question) },
                            onSimilar = { viewModel.generateSimilarQuestion(question) {} }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceGroupHeader(group: WebQuestionResult) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceElevated.copy(alpha = 0.8f),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = DifficultyEasy, modifier = Modifier.size(16.dp))
                    Text(group.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                }
                Text("Source: ${group.sourceName}", fontSize = 11.sp, color = TextSecondaryDark)
            }
        }
    }
}

@Composable
private fun WebQuestionItemCard(
    question: QuestionEntity,
    sourceName: String,
    sourceUrl: String,
    onAddToBank: () -> Unit,
    onSimilar: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                DifficultyBadge(difficulty = question.difficulty)

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "🌐 Verified Public Source",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = question.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                lineHeight = 20.sp
            )

            // Solution preview
            if (expanded) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Answer: ${question.correctAnswer}", fontSize = 12.sp, color = DifficultyEasy, fontWeight = FontWeight.Bold)
                        Text(question.explanation, fontSize = 11.sp, color = TextSecondaryDark)
                        Text("Source URL: $sourceUrl", fontSize = 10.sp, color = BrandSecondary)
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onAddToBank,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add to Bank", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onSimilar,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark)
                    ) {
                        Text("🔄 Similar Q", fontSize = 11.sp)
                    }
                }

                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(if (expanded) "Hide" else "Solution ▼", fontSize = 11.sp, color = TextSecondaryDark)
                }
            }
        }
    }
}
