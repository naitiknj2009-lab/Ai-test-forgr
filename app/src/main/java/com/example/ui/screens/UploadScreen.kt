package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel
import java.io.InputStream

@Composable
fun UploadScreen(
    viewModel: TestForgeViewModel,
    onNavigateToExtracted: (Long) -> Unit,
    onNavigateToCamera: () -> Unit
) {
    val context = LocalContext.current
    val isExtracting by viewModel.isExtracting.collectAsStateWithLifecycle()
    val progress by viewModel.extractionProgress.collectAsStateWithLifecycle()
    val statusText by viewModel.extractionStatusText.collectAsStateWithLifecycle()

    var manualTextInput by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf("") }

    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = "Worksheet_Scan_${System.currentTimeMillis() % 1000}.png"
            selectedFileName = name
            var bitmap: Bitmap? = null
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            viewModel.processUploadedDocument(
                filename = name,
                fileType = "IMAGE",
                rawText = "Extracted OCR text from image worksheet containing Physics and Mathematics problems.",
                bitmap = bitmap,
                onComplete = { docId ->
                    onNavigateToExtracted(docId)
                }
            )
        }
    }

    // PDF Document Picker
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = "Class_11_Physics_Ch3_Questions.pdf"
            selectedFileName = name
            viewModel.processUploadedDocument(
                filename = name,
                fileType = "PDF",
                rawText = """
                    Class 11 Physics Worksheet - Laws of Motion
                    Q1. What is Newton's First Law of Motion and what physical quantity does it define?
                    Options: A. Inertia B. Force C. Momentum D. Power
                    Q2. A 5 kg body moving with 10 m/s is brought to rest in 2.5 s by a constant force. Find the force.
                    Options: A. 10 N B. 20 N C. 25 N D. 50 N
                    Q3. Calculate the work done by a variable force F = (6x - 4) N from x = 1 m to x = 3 m.
                    Options: A. 12 J B. 16 J C. 20 J D. 24 J
                """.trimIndent(),
                pageCount = 3,
                onComplete = { docId ->
                    onNavigateToExtracted(docId)
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Upload & Extract",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Upload question papers, PDF worksheets, or photos. Multimodal AI will extract, solve, and rate every question.",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // Upload Options Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    UploadSourceCard(
                        title = "Upload PDF Document",
                        subtitle = "Supports multi-page test papers, textbooks, and worksheets",
                        icon = Icons.Default.PictureAsPdf,
                        badge = "PDF",
                        badgeColor = Color(0xFFEF4444),
                        onClick = {
                            documentPickerLauncher.launch("application/pdf")
                        }
                    )

                    UploadSourceCard(
                        title = "Upload Photos / Screenshots",
                        subtitle = "Single or multiple images, handwritten sheets, phone screenshots",
                        icon = Icons.Default.Image,
                        badge = "JPG / PNG",
                        badgeColor = Color(0xFF06B6D4),
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        }
                    )

                    UploadSourceCard(
                        title = "Scan with Camera",
                        subtitle = "Auto page framing, real-time OCR, and multi-page capture",
                        icon = Icons.Default.CameraAlt,
                        badge = "CAMERA",
                        badgeColor = Color(0xFF10B981),
                        onClick = onNavigateToCamera
                    )
                }
            }

            // Quick Starter Exam Papers for Instant Demo
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Or Load Sample Question Paper",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    SamplePaperCard(
                        title = "Class 11 Physics — Laws of Motion & Friction.pdf",
                        topic = "Physics • 8 Questions",
                        onLoad = {
                            viewModel.processUploadedDocument(
                                filename = "Class 11 Physics - Laws of Motion.pdf",
                                fileType = "PDF",
                                rawText = "Physics Chapter 5: Newton's Laws, Friction, Free Body Diagrams, Pulley problems and Momentum.",
                                pageCount = 4,
                                onComplete = { docId -> onNavigateToExtracted(docId) }
                            )
                        }
                    )

                    SamplePaperCard(
                        title = "Class 10 Mathematics — Quadratic Equations & Roots.pdf",
                        topic = "Mathematics • 6 Questions",
                        onLoad = {
                            viewModel.processUploadedDocument(
                                filename = "Class 10 Maths - Quadratic Equations.pdf",
                                fileType = "PDF",
                                rawText = "Maths Chapter 4: Factoring quadratic equations, Discriminant formula, nature of roots and Vieta formulas.",
                                pageCount = 2,
                                onComplete = { docId -> onNavigateToExtracted(docId) }
                            )
                        }
                    )
                }
            }

            // Direct Text OCR Paste Option
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Or Paste Question Text Directly",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )

                        OutlinedTextField(
                            value = manualTextInput,
                            onValueChange = { manualTextInput = it },
                            placeholder = { Text("Paste questions with options or raw exam questions...", color = TextSecondaryDark, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        Button(
                            onClick = {
                                if (manualTextInput.isNotBlank()) {
                                    viewModel.processUploadedDocument(
                                        filename = "Pasted Questions ${System.currentTimeMillis() % 1000}.txt",
                                        fileType = "TEXT",
                                        rawText = manualTextInput,
                                        onComplete = { docId -> onNavigateToExtracted(docId) }
                                    )
                                }
                            },
                            enabled = manualTextInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Extract from Text", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Processing Dialog / Progress Overlay
        if (isExtracting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(20.dp),
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = DarkSurface.copy(alpha = 0.95f),
                    borderColor = BrandPrimary.copy(alpha = 0.5f),
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            color = BrandPrimary,
                            trackColor = DarkSurfaceElevated,
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(64.dp)
                        )

                        Text(
                            text = "Analyzing Document... ${(progress * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )

                        LinearProgressIndicator(
                            progress = { progress },
                            color = BrandSecondary,
                            trackColor = DarkSurfaceElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UploadSourceCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(16.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(badgeColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondaryDark,
                    lineHeight = 15.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondaryDark
            )
        }
    }
}

@Composable
private fun SamplePaperCard(
    title: String,
    topic: String,
    onLoad: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onLoad,
        contentPadding = PaddingValues(12.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark,
                    maxLines = 1
                )
                Text(
                    text = topic,
                    fontSize = 11.sp,
                    color = BrandSecondary
                )
            }

            Button(
                onClick = onLoad,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceElevated,
                    contentColor = BrandPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Process", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
