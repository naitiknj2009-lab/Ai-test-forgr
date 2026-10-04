package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AIModelInfo
import com.example.data.ai.AIProviderConfig
import com.example.data.ai.AIProviderType
import com.example.data.ai.AIServiceManager
import com.example.data.local.AppDatabase
import com.example.data.model.DocumentEntity
import com.example.data.model.MistakeEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestAttemptEntity
import com.example.data.model.TestEntity
import com.example.data.model.UserProfile
import com.example.data.remote.PerformanceAnalysisDto
import com.example.data.remote.WebQuestionResult
import com.example.data.remote.WebSearchService
import com.example.data.repository.AIRepository
import com.example.data.repository.TestForgeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

class TestForgeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TestForgeRepository
    private val aiServiceManager = AIServiceManager(application)
    private val aiRepository = AIRepository(aiServiceManager)

    init {
        val database = AppDatabase.getInstance(application)
        repository = TestForgeRepository(database)
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // AI Provider State
    val aiConfig: StateFlow<AIProviderConfig> = aiServiceManager.configState

    private val _availableModels = MutableStateFlow<List<AIModelInfo>>(emptyList())
    val availableModels: StateFlow<List<AIModelInfo>> = _availableModels.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection = _isTestingConnection.asStateFlow()

    private val _connectionTestResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val connectionTestResult = _connectionTestResult.asStateFlow()

    // User Profile
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Core Data Streams from Room
    val allQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val importantQuestions: StateFlow<List<QuestionEntity>> = repository.importantQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTests: StateFlow<List<TestEntity>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<TestAttemptEntity>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unresolvedMistakes: StateFlow<List<MistakeEntity>> = repository.unresolvedMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMistakes: StateFlow<List<MistakeEntity>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter State in Question Bank
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedSubjectFilter = MutableStateFlow("All")
    val selectedSubjectFilter = _selectedSubjectFilter.asStateFlow()

    private val _selectedDifficultyFilter = MutableStateFlow("All")
    val selectedDifficultyFilter = _selectedDifficultyFilter.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("All")
    val selectedTypeFilter = _selectedTypeFilter.asStateFlow()

    val filteredQuestions: StateFlow<List<QuestionEntity>> = combine(
        allQuestions,
        _searchQuery,
        _selectedSubjectFilter,
        _selectedDifficultyFilter,
        _selectedTypeFilter
    ) { questions, query, subject, difficulty, type ->
        questions.filter { q ->
            val matchesQuery = query.isBlank() ||
                    q.questionText.contains(query, ignoreCase = true) ||
                    q.chapter.contains(query, ignoreCase = true) ||
                    q.topic.contains(query, ignoreCase = true) ||
                    q.subject.contains(query, ignoreCase = true)

            val matchesSubject = subject == "All" || q.subject.equals(subject, ignoreCase = true)
            val matchesDiff = difficulty == "All" || q.difficulty.equals(difficulty, ignoreCase = true)
            val matchesType = type == "All" || q.questionType.equals(type, ignoreCase = true)

            matchesQuery && matchesSubject && matchesDiff && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Document Processing State
    private val _isExtracting = MutableStateFlow(false)
    val isExtracting = _isExtracting.asStateFlow()

    private val _extractionProgress = MutableStateFlow(0f)
    val extractionProgress = _extractionProgress.asStateFlow()

    private val _extractionStatusText = MutableStateFlow("")
    val extractionStatusText = _extractionStatusText.asStateFlow()

    private val _lastExtractedQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val lastExtractedQuestions = _lastExtractedQuestions.asStateFlow()

    // AI Question Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _generatedQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val generatedQuestions = _generatedQuestions.asStateFlow()

    // Web Search State
    private val _isSearchingWeb = MutableStateFlow(false)
    val isSearchingWeb = _isSearchingWeb.asStateFlow()

    private val _webSearchResults = MutableStateFlow<List<WebQuestionResult>>(emptyList())
    val webSearchResults = _webSearchResults.asStateFlow()

    // Active Test Simulator State
    private val _activeTest = MutableStateFlow<TestEntity?>(null)
    val activeTest = _activeTest.asStateFlow()

    private val _testQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val testQuestions = _testQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Long, String>>(emptyMap())
    val userAnswers = _userAnswers.asStateFlow()

    private val _markedForReview = MutableStateFlow<Set<Long>>(emptySet())
    val markedForReview = _markedForReview.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds = _remainingSeconds.asStateFlow()

    private val _totalTestDurationSeconds = MutableStateFlow(0)
    val totalTestDurationSeconds = _totalTestDurationSeconds.asStateFlow()

    private val _isTestActive = MutableStateFlow(false)
    val isTestActive = _isTestActive.asStateFlow()

    private val _lastCompletedAttempt = MutableStateFlow<TestAttemptEntity?>(null)
    val lastCompletedAttempt = _lastCompletedAttempt.asStateFlow()

    private val _lastAnalysis = MutableStateFlow<PerformanceAnalysisDto?>(null)
    val lastAnalysis = _lastAnalysis.asStateFlow()

    private var timerJob: Job? = null

    // User Profile Actions
    fun updateUserProfile(name: String? = null, mobile: String? = null, classGrade: String? = null, examGoal: String? = null) {
        val current = _userProfile.value
        _userProfile.value = current.copy(
            name = name ?: current.name,
            mobileNumber = mobile ?: current.mobileNumber,
            classGrade = classGrade ?: current.classGrade,
            examGoal = examGoal ?: current.examGoal,
            targetExam = examGoal ?: current.targetExam,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun completeOnboarding() {
        val current = _userProfile.value
        _userProfile.value = current.copy(
            onboardingCompleted = true,
            updatedAt = System.currentTimeMillis()
        )
    }

    // UI Feedback
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    // AI BYOK Configuration methods
    fun selectAIProvider(providerType: AIProviderType) {
        aiServiceManager.setProvider(providerType)
        _connectionTestResult.value = null
        refreshAvailableModels()
    }

    fun saveApiKey(apiKey: String) {
        aiServiceManager.saveApiKey(apiKey)
        _connectionTestResult.value = null
        showMessage("API Key saved securely via Android Keystore")
        refreshAvailableModels()
    }

    fun removeApiKey() {
        aiServiceManager.removeApiKey()
        _connectionTestResult.value = null
        showMessage("API Key removed")
    }

    fun selectAIModel(modelId: String) {
        aiServiceManager.saveModel(modelId)
    }

    fun saveCustomBaseUrl(url: String) {
        aiServiceManager.saveCustomBaseUrl(url)
    }

    fun saveAIParameters(temperature: Float, maxTokens: Int) {
        aiServiceManager.saveParameters(temperature, maxTokens)
        showMessage("Parameters updated")
    }

    fun saveSeparateModelsConfig(
        useSeparate: Boolean,
        ocr: String,
        gen: String,
        expl: String,
        web: String
    ) {
        aiServiceManager.saveTaskModels(useSeparate, ocr, gen, expl, web)
        showMessage("Task model assignments saved")
    }

    fun testAIConnection(apiKeyInput: String? = null, modelInput: String? = null, baseUrlInput: String? = null) {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _connectionTestResult.value = null
            val result = aiServiceManager.testConnection(
                apiKeyInput = apiKeyInput,
                modelInput = modelInput,
                baseUrlInput = baseUrlInput
            )
            _isTestingConnection.value = false
            if (result.isSuccess) {
                _connectionTestResult.value = Pair(true, result.getOrNull() ?: "Connection successful!")
                showMessage("✓ Connection Verified")
            } else {
                val error = result.exceptionOrNull()?.message ?: "Connection failed"
                _connectionTestResult.value = Pair(false, error)
            }
        }
    }

    fun refreshAvailableModels() {
        viewModelScope.launch {
            val result = aiServiceManager.fetchModels()
            if (result.isSuccess) {
                _availableModels.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    fun exportSettings(): String {
        return aiServiceManager.exportSettingsJson()
    }

    fun importSettings(json: String): Boolean {
        val success = aiServiceManager.importSettingsJson(json)
        if (success) {
            showMessage("Settings imported successfully")
        } else {
            showMessage("Failed to parse settings JSON")
        }
        return success
    }

    // Question Bank Filters & Search
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubjectFilter(subject: String) {
        _selectedSubjectFilter.value = subject
    }

    fun setDifficultyFilter(diff: String) {
        _selectedDifficultyFilter.value = diff
    }

    fun setTypeFilter(type: String) {
        _selectedTypeFilter.value = type
    }

    fun toggleImportant(question: QuestionEntity) {
        viewModelScope.launch {
            repository.updateQuestion(question.copy(isImportant = !question.isImportant))
        }
    }

    fun deleteQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
            showMessage("Question removed from bank")
        }
    }

    fun saveQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            if (question.id == 0L) {
                repository.insertQuestion(question)
            } else {
                repository.updateQuestion(question)
            }
            showMessage("Question saved to Question Bank")
        }
    }

    fun saveAllExtractedQuestions() {
        viewModelScope.launch {
            val list = _lastExtractedQuestions.value
            if (list.isNotEmpty()) {
                repository.insertQuestions(list)
                showMessage("Added ${list.size} questions to Question Bank!")
            }
        }
    }

    // Document Processing (PDF / Image / Camera)
    fun processUploadedDocument(
        filename: String,
        fileType: String,
        rawText: String = "",
        bitmap: Bitmap? = null,
        pageCount: Int = 1,
        onComplete: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isExtracting.value = true
            _extractionProgress.value = 0.15f
            _extractionStatusText.value = "Reading document pages & detecting text..."

            delay(600)
            _extractionProgress.value = 0.45f
            _extractionStatusText.value = "Running multimodal AI OCR & question parsing..."

            delay(600)
            _extractionProgress.value = 0.75f
            _extractionStatusText.value = "Classifying difficulty & generating step-by-step solutions..."

            val extracted = aiRepository.extractQuestions(
                text = rawText.ifBlank { "Exam Paper: $filename\nContains multiple questions on Science and Mathematics." },
                bitmap = bitmap,
                documentName = filename
            )

            _extractionProgress.value = 1.0f
            _extractionStatusText.value = "Extraction complete! Found ${extracted.size} questions."

            val docId = repository.insertDocument(
                DocumentEntity(
                    filename = filename,
                    fileType = fileType,
                    pageCount = pageCount,
                    questionsExtractedCount = extracted.size,
                    processingStatus = "COMPLETED"
                )
            )

            val tagged = extracted.map { it.copy(documentId = docId) }
            _lastExtractedQuestions.value = tagged
            repository.insertQuestions(tagged)

            _isExtracting.value = false
            onComplete(docId)
        }
    }

    // AI Question Generation
    fun generateAIQuestions(
        subject: String,
        classGrade: String,
        chapter: String,
        difficulty: String,
        count: Int,
        questionType: String,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            val list = aiRepository.generateQuestions(
                subject = subject,
                classGrade = classGrade,
                chapter = chapter,
                difficulty = difficulty,
                count = count,
                questionType = questionType
            )
            _generatedQuestions.value = list
            _isGenerating.value = false
            onDone()
        }
    }

    fun saveGeneratedQuestionsToBank() {
        viewModelScope.launch {
            val list = _generatedQuestions.value
            if (list.isNotEmpty()) {
                repository.insertQuestions(list)
                showMessage("Saved ${list.size} AI-generated questions to Bank!")
            }
        }
    }

    // Generate Similar Question
    fun generateSimilarQuestion(original: QuestionEntity, onResult: (QuestionEntity) -> Unit) {
        viewModelScope.launch {
            showMessage("AI is crafting parallel concept drill...")
            val similar = aiRepository.generateSimilarQuestion(original)
            repository.insertQuestion(similar)
            showMessage("Similar question created & saved to Bank!")
            onResult(similar)
        }
    }

    // Web Question Search
    fun searchWebQuestions(query: String) {
        viewModelScope.launch {
            _isSearchingWeb.value = true
            delay(500)
            val results = WebSearchService.searchEducationalQuestions(query)
            _webSearchResults.value = results
            _isSearchingWeb.value = false
        }
    }

    fun addWebQuestionToBank(question: QuestionEntity) {
        viewModelScope.launch {
            repository.insertQuestion(question)
            showMessage("Added web question with source citation to Bank!")
        }
    }

    // Create & Configure Test
    fun createAndStartTest(
        title: String,
        subject: String,
        questionCount: Int = 10,
        durationMinutes: Int = 15,
        difficultyMode: String = "MIXED",
        questionTypeMode: String = "MCQ",
        sourceMode: String = "MIXED",
        onTestReady: () -> Unit
    ) {
        viewModelScope.launch {
            val available = allQuestions.value

            val pool = if (subject != "All" && subject != "General Science" && available.any { it.subject.equals(subject, ignoreCase = true) }) {
                available.filter { it.subject.equals(subject, ignoreCase = true) }
            } else {
                available
            }

            val selected = mutableListOf<QuestionEntity>()
            if (difficultyMode == "MIXED") {
                val easyCount = (questionCount * 0.25).toInt().coerceAtLeast(1)
                val hardCount = (questionCount * 0.25).toInt().coerceAtLeast(1)
                val medCount = (questionCount - easyCount - hardCount).coerceAtLeast(1)

                val easyPool = pool.filter { it.difficulty == "EASY" }.shuffled()
                val medPool = pool.filter { it.difficulty == "MEDIUM" }.shuffled()
                val hardPool = pool.filter { it.difficulty == "HARD" }.shuffled()

                selected.addAll(easyPool.take(easyCount))
                selected.addAll(medPool.take(medCount))
                selected.addAll(hardPool.take(hardCount))

                if (selected.size < questionCount) {
                    val remaining = pool.filterNot { selected.contains(it) }.shuffled()
                    selected.addAll(remaining.take(questionCount - selected.size))
                }
            } else {
                val match = pool.filter { it.difficulty.equals(difficultyMode, ignoreCase = true) }.shuffled()
                selected.addAll(match.take(questionCount))
                if (selected.size < questionCount) {
                    val remaining = pool.filterNot { selected.contains(it) }.shuffled()
                    selected.addAll(remaining.take(questionCount - selected.size))
                }
            }

            if (selected.size < questionCount) {
                val needed = questionCount - selected.size
                val generated = aiRepository.generateQuestions(
                    subject = if (subject == "All") "Physics" else subject,
                    classGrade = _userProfile.value.classGrade,
                    chapter = "Core Curriculum",
                    difficulty = difficultyMode,
                    count = needed,
                    questionType = questionTypeMode
                )
                repository.insertQuestions(generated)
                selected.addAll(generated)
            }

            val finalQuestions = selected.take(questionCount).shuffled()
            val questionIds = finalQuestions.map { it.id }

            val testEntity = TestEntity(
                title = title.ifBlank { "$subject Comprehensive Test" },
                subject = subject,
                targetQuestionsCount = finalQuestions.size,
                durationMinutes = durationMinutes,
                difficultyMode = difficultyMode,
                questionTypeMode = questionTypeMode,
                sourceMode = sourceMode,
                questionIdsJson = "[${questionIds.joinToString(",")}]"
            )

            val testId = repository.insertTest(testEntity)
            val savedTest = testEntity.copy(id = testId)

            startTestSession(savedTest, finalQuestions)
            onTestReady()
        }
    }

    // Start practice test directly from Weak Areas or Mistakes
    fun createWeakAreaPracticeTest(weakTopics: List<String>, onTestReady: () -> Unit) {
        viewModelScope.launch {
            val topicLabel = weakTopics.firstOrNull() ?: "Reinforcement"
            val practiceQuestions = aiRepository.generateQuestions(
                subject = "Science",
                classGrade = _userProfile.value.classGrade,
                chapter = topicLabel,
                difficulty = "MEDIUM",
                count = 5,
                questionType = "MCQ"
            )
            repository.insertQuestions(practiceQuestions)

            val testEntity = TestEntity(
                title = "Targeted Practice: $topicLabel",
                subject = "Adaptive Drill",
                targetQuestionsCount = practiceQuestions.size,
                durationMinutes = 8,
                difficultyMode = "MEDIUM",
                questionTypeMode = "MCQ",
                sourceMode = "AI_GENERATED",
                questionIdsJson = "[${practiceQuestions.map { it.id }.joinToString(",")}]"
            )
            val testId = repository.insertTest(testEntity)
            startTestSession(testEntity.copy(id = testId), practiceQuestions)
            onTestReady()
        }
    }

    private fun startTestSession(test: TestEntity, questions: List<QuestionEntity>) {
        _activeTest.value = test
        _testQuestions.value = questions
        _currentQuestionIndex.value = 0
        _userAnswers.value = emptyMap()
        _markedForReview.value = emptySet()
        val totalSecs = test.durationMinutes * 60
        _totalTestDurationSeconds.value = totalSecs
        _remainingSeconds.value = totalSecs
        _isTestActive.value = true

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0 && _isTestActive.value) {
                delay(1000)
                _remainingSeconds.value = (_remainingSeconds.value - 1).coerceAtLeast(0)
            }
            if (_remainingSeconds.value == 0 && _isTestActive.value) {
                submitTest()
            }
        }
    }

    fun selectAnswer(questionId: Long, answer: String) {
        val current = _userAnswers.value.toMutableMap()
        current[questionId] = answer
        _userAnswers.value = current
    }

    fun clearAnswer(questionId: Long) {
        val current = _userAnswers.value.toMutableMap()
        current.remove(questionId)
        _userAnswers.value = current
    }

    fun toggleMarkForReview(questionId: Long) {
        val current = _markedForReview.value.toMutableSet()
        if (current.contains(questionId)) current.remove(questionId) else current.add(questionId)
        _markedForReview.value = current
    }

    fun jumpToQuestion(index: Int) {
        if (index in 0 until _testQuestions.value.size) {
            _currentQuestionIndex.value = index
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _testQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun submitTest(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            timerJob?.cancel()
            _isTestActive.value = false

            val test = _activeTest.value ?: return@launch
            val questions = _testQuestions.value
            val answersMap = _userAnswers.value

            val totalTime = _totalTestDurationSeconds.value
            val timeTaken = (totalTime - _remainingSeconds.value).coerceAtLeast(1)

            var correctCount = 0
            var easyCorrect = 0; var easyTotal = 0
            var medCorrect = 0; var medTotal = 0
            var hardCorrect = 0; var hardTotal = 0

            val mistakeEntities = mutableListOf<MistakeEntity>()

            questions.forEach { q ->
                val selected = answersMap[q.id] ?: ""
                val isCorrect = isAnswerMatching(selected, q.correctAnswer)

                when (q.difficulty.uppercase()) {
                    "EASY" -> {
                        easyTotal++
                        if (isCorrect) easyCorrect++
                    }
                    "HARD" -> {
                        hardTotal++
                        if (isCorrect) hardCorrect++
                    }
                    else -> {
                        medTotal++
                        if (isCorrect) medCorrect++
                    }
                }

                if (isCorrect) {
                    correctCount++
                } else {
                    mistakeEntities.add(
                        MistakeEntity(
                            questionId = q.id,
                            questionText = q.questionText,
                            optionsJson = q.optionsJson,
                            subject = q.subject,
                            topic = q.topic,
                            selectedAnswer = selected.ifBlank { "Not Answered" },
                            correctAnswer = q.correctAnswer,
                            explanation = q.explanation,
                            difficulty = q.difficulty
                        )
                    )
                }
            }

            if (mistakeEntities.isNotEmpty()) {
                repository.insertMistakes(mistakeEntities)
            }

            val accuracy = if (questions.isNotEmpty()) (correctCount.toFloat() / questions.size.toFloat()) * 100f else 0f

            val ansJsonObj = JSONObject()
            answersMap.forEach { (qid, ans) -> ansJsonObj.put(qid.toString(), ans) }

            val attempt = TestAttemptEntity(
                testId = test.id,
                testTitle = test.title,
                subject = test.subject,
                totalQuestions = questions.size,
                correctAnswersCount = correctCount,
                score = correctCount,
                accuracyPercent = accuracy,
                timeTakenSeconds = timeTaken,
                easyScore = "$easyCorrect/$easyTotal",
                mediumScore = "$medCorrect/$medTotal",
                hardScore = "$hardCorrect/$hardTotal",
                aiAnalysis = "Calculating diagnostic breakdown...",
                strongTopicsJson = "[]",
                weakTopicsJson = "[]",
                userAnswersJson = ansJsonObj.toString()
            )

            val attemptId = repository.insertAttempt(attempt)
            val savedAttempt = attempt.copy(id = attemptId)
            _lastCompletedAttempt.value = savedAttempt

            val analysis = aiRepository.analyzePerformance(savedAttempt, questions, answersMap)
            _lastAnalysis.value = analysis

            val currentProfile = _userProfile.value
            _userProfile.value = currentProfile.copy(
                totalTestsTaken = currentProfile.totalTestsTaken + 1,
                streakDays = currentProfile.streakDays + 1
            )

            onDone()
        }
    }

    fun markMistakeResolved(mistake: MistakeEntity) {
        viewModelScope.launch {
            repository.markMistakeResolved(mistake.id)
            showMessage("Marked as master & resolved!")
        }
    }

    fun deleteMistake(mistake: MistakeEntity) {
        viewModelScope.launch {
            repository.deleteMistake(mistake)
            showMessage("Removed from Mistake Notebook")
        }
    }

    private fun isAnswerMatching(userSelected: String, correctOption: String): Boolean {
        if (userSelected.isBlank() || correctOption.isBlank()) return false
        val cleanUser = userSelected.trim()
        val cleanCorrect = correctOption.trim()
        if (cleanUser.equals(cleanCorrect, ignoreCase = true)) return true

        val userLetter = cleanUser.take(2).trim().removeSuffix(".")
        val correctLetter = cleanCorrect.take(2).trim().removeSuffix(".")
        return userLetter.equals(correctLetter, ignoreCase = true)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
