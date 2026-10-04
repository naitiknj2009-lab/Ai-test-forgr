package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Outlined.Home, Icons.Filled.Home)
    object Bank : Screen("bank", "Bank", Icons.AutoMirrored.Outlined.MenuBook, Icons.AutoMirrored.Filled.MenuBook)
    object CreateTest : Screen("create_test", "Test", Icons.Outlined.Quiz, Icons.Filled.Quiz)
    object History : Screen("history", "History", Icons.Outlined.Analytics, Icons.Filled.Analytics)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person, Icons.Filled.Person)

    // Sub-screens
    object Upload : Screen("upload", "Upload", Icons.Outlined.UploadFile, Icons.Filled.UploadFile)
    object Camera : Screen("camera", "Camera", Icons.Outlined.CameraAlt, Icons.Filled.CameraAlt)
    object Extracted : Screen("extracted/{docId}", "Extracted", Icons.AutoMirrored.Outlined.Article, Icons.AutoMirrored.Filled.Article) {
        fun createRoute(docId: Long) = "extracted/$docId"
    }
    object Generate : Screen("generate", "Generate", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome)
    object WebSearch : Screen("web_search", "Find Online", Icons.Outlined.Language, Icons.Filled.Language)
    object TestInstructions : Screen("test_instructions", "Instructions", Icons.Outlined.Info, Icons.Filled.Info)
    object TestActive : Screen("test_active", "Exam", Icons.Outlined.Timer, Icons.Filled.Timer)
    object TestResult : Screen("test_result", "Result", Icons.Outlined.EmojiEvents, Icons.Filled.EmojiEvents)
    object Mistakes : Screen("mistakes", "Mistakes", Icons.Outlined.ErrorOutline, Icons.Filled.ErrorOutline)
    object AIProviderSettings : Screen("ai_provider_settings", "AI Provider", Icons.Outlined.Key, Icons.Filled.Key)
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
    object Splash : Screen("splash", "Splash", Icons.Outlined.FlashOn, Icons.Filled.FlashOn)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome)
    object Registration : Screen("registration", "Registration", Icons.Outlined.PersonAdd, Icons.Filled.PersonAdd)
    object Preferences : Screen("preferences", "Preferences", Icons.Outlined.Tune, Icons.Filled.Tune)
    object SetupComplete : Screen("setup_complete", "Setup Complete", Icons.Outlined.CheckCircle, Icons.Filled.CheckCircle)
}
