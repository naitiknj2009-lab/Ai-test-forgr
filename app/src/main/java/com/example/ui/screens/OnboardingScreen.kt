package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val title: String,
    val headline: String,
    val description: String,
    val icon: ImageVector,
    val badge: String,
    val badgeColor: Color
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val pages = listOf(
        OnboardingPageData(
            title = "Welcome",
            headline = "Turn Your Questions Into Smart Tests",
            description = "Upload your PDFs, photos, worksheets, or question papers and let AI understand them automatically.",
            icon = Icons.Default.AutoAwesome,
            badge = "AI TESTFORGE",
            badgeColor = BrandPrimary
        ),
        OnboardingPageData(
            title = "Upload Anything",
            headline = "Upload Questions Easily",
            description = "Upload PDFs, photos, scanned question papers, phone screenshots, or multiple pages. AI understands both printed and handwritten questions seamlessly.",
            icon = Icons.Default.UploadFile,
            badge = "PDF • SCAN • OCR",
            badgeColor = BrandSecondary
        ),
        OnboardingPageData(
            title = "Question Intelligence",
            headline = "AI Understands Every Question",
            description = "AI extracts individual questions, identifies subjects & topics, verifies mathematical solutions, and automatically rates difficulty as Easy, Medium, or Hard.",
            icon = Icons.Default.Psychology,
            badge = "EASY • MEDIUM • HARD",
            badgeColor = DifficultyEasy
        ),
        OnboardingPageData(
            title = "Exam Simulator",
            headline = "Build Tests Your Way",
            description = "Create customizable 10-question tests. Choose subjects, difficulty blends, and durations, then take the exam with a real countdown timer.",
            icon = Icons.Default.Timer,
            badge = "10 QUESTIONS • TIMED",
            badgeColor = WarningAmber
        ),
        OnboardingPageData(
            title = "Diagnostic Analytics",
            headline = "Know Where You Need Practice",
            description = "After every test, AI analyzes your performance, identifies your weak and strong topics, archives errors to your Mistake Notebook, and generates personalized practice drills.",
            icon = Icons.Default.Insights,
            badge = "ADAPTIVE PRACTICE",
            badgeColor = BrandTertiary
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = pages[pagerState.currentPage].badge,
                        color = pages[pagerState.currentPage].badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(onClick = onFinishOnboarding) {
                        Text("Skip", color = TextSecondaryDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isCurrent = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isCurrent) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isCurrent) BrandPrimary else DarkBorder)
                        )
                    }
                }

                // Action Button
                if (pagerState.currentPage == pages.size - 1) {
                    Button(
                        onClick = onFinishOnboarding,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Get Started", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Next", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Interactive Visual Hero Card per step
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = DarkSurface.copy(alpha = 0.85f),
                    borderColor = page.badgeColor.copy(alpha = 0.5f),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    when (pageIndex) {
                        0 -> OnboardingVisualWelcome()
                        1 -> OnboardingVisualUpload()
                        2 -> OnboardingVisualDifficulty()
                        3 -> OnboardingVisualTest()
                        else -> OnboardingVisualAnalytics()
                    }
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = page.headline,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = page.description,
                    fontSize = 14.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingVisualWelcome() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(BrandPrimary.copy(alpha = 0.2f))
                .border(2.dp, BrandPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text("AI Exam Engine & Question Bank", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Text("Zero manual typing required", fontSize = 11.sp, color = TextSecondaryDark)
    }
}

@Composable
private fun OnboardingVisualUpload() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        UploadPill("PDF", Icons.Default.PictureAsPdf, Color(0xFFEF4444))
        UploadPill("Camera", Icons.Default.CameraAlt, Color(0xFF10B981))
        UploadPill("Photos", Icons.Default.Image, Color(0xFF06B6D4))
        UploadPill("Papers", Icons.Default.Article, Color(0xFF8B5CF6))
    }
}

@Composable
private fun UploadPill(label: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
    }
}

@Composable
private fun OnboardingVisualDifficulty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DifficultyBadge("EASY")
            DifficultyBadge("MEDIUM")
            DifficultyBadge("HARD")
        }
        Spacer(Modifier.height(14.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DarkSurfaceElevated,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text(
                text = "Q. Newton's Second Law: F = dp/dt\nAI Rate: 🟡 Medium • Multi-step",
                fontSize = 11.sp,
                color = TextSecondaryDark,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OnboardingVisualTest() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = BrandPrimary.copy(alpha = 0.2f)) {
                Text("📝 10 Questions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandPrimary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
            }
            Surface(shape = RoundedCornerShape(10.dp), color = WarningAmber.copy(alpha = 0.2f)) {
                Text("⏱ 20:00 Countdown", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarningAmber, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Distraction-Free Exam Simulator", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
    }
}

@Composable
private fun OnboardingVisualAnalytics() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(shape = RoundedCornerShape(8.dp), color = DifficultyEasy.copy(alpha = 0.15f)) {
                Text("💪 Strong: Kinematics", fontSize = 11.sp, color = DifficultyEasy, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = DifficultyHard.copy(alpha = 0.15f)) {
                Text("🎯 Weak: Friction", fontSize = 11.sp, color = DifficultyHard, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("Auto Mistake Notebook + Practice Drills", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
    }
}
