package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class Difficulty(val label: String, val emoji: String) {
    EASY("Easy", "🟢"),
    MEDIUM("Medium", "🟡"),
    HARD("Hard", "🔴"),
    MIXED("Mixed", "⚡")
}

enum class QuestionType(val label: String) {
    MCQ("Multiple Choice"),
    NUMERICAL("Numerical"),
    SUBJECTIVE("Subjective"),
    TRUE_FALSE("True / False"),
    SHORT_ANSWER("Short Answer")
}

enum class QuestionSource(val label: String) {
    PDF_UPLOAD("PDF Upload"),
    IMAGE_SCAN("Image Upload"),
    CAMERA_OCR("Camera Scanner"),
    AI_GENERATED("AI Generated"),
    WEB_SEARCH("Web Search"),
    MANUAL("Manual Entry")
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionText: String,
    val optionsJson: String = "[]", // JSON array of options for MCQ e.g. ["A. Option 1", "B. Option 2", ...]
    val correctAnswer: String,
    val explanation: String = "",
    val subject: String = "General Science",
    val chapter: String = "General",
    val topic: String = "Concepts",
    val difficulty: String = "MEDIUM", // EASY, MEDIUM, HARD
    val difficultyReason: String = "Requires multi-step conceptual application.",
    val questionType: String = "MCQ", // MCQ, NUMERICAL, etc.
    val source: String = "AI_GENERATED",
    val sourceUrl: String? = null,
    val isImportant: Boolean = false,
    val tagsJson: String = "[]",
    val documentId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filename: String,
    val fileType: String, // PDF, IMAGE, CAMERA
    val pageCount: Int = 1,
    val questionsExtractedCount: Int = 0,
    val uploadDate: Long = System.currentTimeMillis(),
    val processingStatus: String = "COMPLETED" // PROCESSING, COMPLETED, FAILED
)

@JsonClass(generateAdapter = true)
@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val targetQuestionsCount: Int = 10,
    val durationMinutes: Int = 15,
    val difficultyMode: String = "MIXED",
    val questionTypeMode: String = "MCQ",
    val sourceMode: String = "MIXED",
    val questionIdsJson: String = "[]", // JSON array of Long IDs
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val testId: Long,
    val testTitle: String,
    val subject: String,
    val totalQuestions: Int,
    val correctAnswersCount: Int,
    val score: Int,
    val accuracyPercent: Float,
    val timeTakenSeconds: Int,
    val easyScore: String = "0/0",
    val mediumScore: String = "0/0",
    val hardScore: String = "0/0",
    val aiAnalysis: String = "",
    val strongTopicsJson: String = "[]",
    val weakTopicsJson: String = "[]",
    val userAnswersJson: String = "{}", // Map of questionId -> selectedAnswer
    val completedAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val questionText: String,
    val optionsJson: String = "[]",
    val subject: String,
    val topic: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val explanation: String,
    val difficulty: String,
    val attemptsCount: Int = 1,
    val isResolved: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: Long = 1,
    val name: String = "Student",
    val mobileNumber: String = "",
    val classGrade: String = "Class 11",
    val examGoal: String = "JEE / College Entrance",
    val targetExam: String = "JEE / College Entrance",
    val selectedAIProvider: String = "Google Gemini",
    val selectedAIModel: String = "gemini-3.5-flash",
    val onboardingCompleted: Boolean = false,
    val streakDays: Int = 1,
    val totalTestsTaken: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
