package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TestForgeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TestForgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                TestForgeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TestForgeApp(viewModel: TestForgeViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // Hide bottom navigation during active test, camera, or onboarding flow
    val hideBottomNav = currentRoute == Screen.TestActive.route ||
            currentRoute == Screen.Camera.route ||
            currentRoute == Screen.TestInstructions.route ||
            currentRoute == Screen.Splash.route ||
            currentRoute == Screen.Onboarding.route ||
            currentRoute == Screen.Registration.route ||
            currentRoute == Screen.Preferences.route ||
            currentRoute == Screen.SetupComplete.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Bank,
        Screen.CreateTest,
        Screen.History,
        Screen.Profile
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DarkSurfaceElevated,
                    contentColor = TextPrimaryDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.border(1.dp, BrandPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                )
            }
        },
        bottomBar = {
            if (!hideBottomNav) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimaryDark,
                    tonalElevation = 8.dp,
                    modifier = Modifier.border(
                        androidx.compose.foundation.BorderStroke(1.dp, DarkBorder.copy(alpha = 0.5f))
                    )
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandPrimary,
                                selectedTextColor = BrandPrimary,
                                unselectedIconColor = TextSecondaryDark,
                                unselectedTextColor = TextSecondaryDark,
                                indicatorColor = BrandPrimary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    viewModel = viewModel,
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Onboarding 5-step Flow
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        navController.navigate(Screen.Registration.route)
                    }
                )
            }

            // Registration Screen
            composable(Screen.Registration.route) {
                RegistrationScreen(
                    viewModel = viewModel,
                    onNavigateToPreferences = {
                        navController.navigate(Screen.Preferences.route)
                    }
                )
            }

            // Learning Preferences Screen
            composable(Screen.Preferences.route) {
                PreferencesScreen(
                    viewModel = viewModel,
                    onNavigateToComplete = {
                        navController.navigate(Screen.SetupComplete.route)
                    }
                )
            }

            // Setup Complete Celebration
            composable(Screen.SetupComplete.route) {
                SetupCompleteScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Home Dashboard
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToUpload = { navController.navigate(Screen.Upload.route) },
                    onNavigateToGenerate = { navController.navigate(Screen.Generate.route) },
                    onNavigateToCreateTest = { navController.navigate(Screen.CreateTest.route) },
                    onNavigateToWebSearch = { navController.navigate(Screen.WebSearch.route) },
                    onNavigateToQuestionBank = { navController.navigate(Screen.Bank.route) },
                    onNavigateToMistakes = { navController.navigate(Screen.Mistakes.route) },
                    onNavigateToAttemptResult = { navController.navigate(Screen.TestResult.route) }
                )
            }

            // Question Bank
            composable(Screen.Bank.route) {
                QuestionBankScreen(
                    viewModel = viewModel,
                    onNavigateToGenerate = { navController.navigate(Screen.Generate.route) },
                    onNavigateToCreateTest = { navController.navigate(Screen.CreateTest.route) }
                )
            }

            // Create Test Builder
            composable(Screen.CreateTest.route) {
                TestBuilderScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartInstructions = { navController.navigate(Screen.TestInstructions.route) }
                )
            }

            // Test Instructions
            composable(Screen.TestInstructions.route) {
                TestInstructionsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onBeginTest = { navController.navigate(Screen.TestActive.route) }
                )
            }

            // Distraction-Free Active Exam Simulator
            composable(Screen.TestActive.route) {
                TestScreen(
                    viewModel = viewModel,
                    onTestFinished = {
                        navController.navigate(Screen.TestResult.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // Test Result & AI Analysis
            composable(Screen.TestResult.route) {
                TestResultScreen(
                    viewModel = viewModel,
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onStartPracticeTest = {
                        navController.navigate(Screen.TestInstructions.route)
                    }
                )
            }

            // Document Upload & OCR Screen
            composable(Screen.Upload.route) {
                UploadScreen(
                    viewModel = viewModel,
                    onNavigateToExtracted = { docId ->
                        navController.navigate(Screen.Extracted.createRoute(docId))
                    },
                    onNavigateToCamera = {
                        navController.navigate(Screen.Camera.route)
                    }
                )
            }

            // Live Camera Scanner
            composable(Screen.Camera.route) {
                CameraScannerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onScanComplete = { docId ->
                        navController.navigate(Screen.Extracted.createRoute(docId)) {
                            popUpTo(Screen.Upload.route) { inclusive = true }
                        }
                    }
                )
            }

            // Extracted Questions Review
            composable(
                route = Screen.Extracted.route,
                arguments = listOf(navArgument("docId") { type = NavType.LongType })
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getLong("docId") ?: 0L
                ExtractedQuestionsScreen(
                    viewModel = viewModel,
                    documentId = docId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTestBuilder = { navController.navigate(Screen.CreateTest.route) },
                    onNavigateToBank = { navController.navigate(Screen.Bank.route) }
                )
            }

            // AI Question Generator
            composable(Screen.Generate.route) {
                AiGeneratorScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTestBuilder = { navController.navigate(Screen.CreateTest.route) },
                    onNavigateToBank = { navController.navigate(Screen.Bank.route) }
                )
            }

            // Find Questions Online (Web Search)
            composable(Screen.WebSearch.route) {
                WebSearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Mistake Notebook
            composable(Screen.Mistakes.route) {
                MistakeNotebookScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartPractice = { navController.navigate(Screen.TestInstructions.route) }
                )
            }

            // History & Analytics
            composable(Screen.History.route) {
                HistoryAnalyticsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onViewAttempt = { navController.navigate(Screen.TestResult.route) }
                )
            }

            // Profile & Student Hub
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToMistakes = { navController.navigate(Screen.Mistakes.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToAIProvider = { navController.navigate(Screen.AIProviderSettings.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // AI Provider Settings (BYOK)
            composable(Screen.AIProviderSettings.route) {
                AIProviderSettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Settings & About
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAIProvider = { navController.navigate(Screen.AIProviderSettings.route) }
                )
            }
        }
    }
}
