package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.Difficulty
import com.example.data.model.DocumentEntity
import com.example.data.model.MistakeEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionSource
import com.example.data.model.TestAttemptEntity
import com.example.data.model.TestEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TestForgeRepository(private val database: AppDatabase) {

    private val questionDao = database.questionDao()
    private val documentDao = database.documentDao()
    private val testDao = database.testDao()
    private val mistakeDao = database.mistakeDao()

    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    val importantQuestions: Flow<List<QuestionEntity>> = questionDao.getImportantQuestions()
    val totalQuestionCount: Flow<Int> = questionDao.getQuestionCount()

    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments()

    val allTests: Flow<List<TestEntity>> = testDao.getAllTests()
    val allAttempts: Flow<List<TestAttemptEntity>> = testDao.getAllAttempts()
    val completedTestsCount: Flow<Int> = testDao.getCompletedTestsCount()
    val averageAccuracy: Flow<Float?> = testDao.getAverageAccuracy()

    val unresolvedMistakes: Flow<List<MistakeEntity>> = mistakeDao.getUnresolvedMistakes()
    val allMistakes: Flow<List<MistakeEntity>> = mistakeDao.getAllMistakes()
    val unresolvedMistakeCount: Flow<Int> = mistakeDao.getUnresolvedMistakeCount()

    suspend fun insertQuestion(question: QuestionEntity): Long = questionDao.insertQuestion(question)

    suspend fun insertQuestions(questions: List<QuestionEntity>): List<Long> = questionDao.insertQuestions(questions)

    suspend fun updateQuestion(question: QuestionEntity) = questionDao.updateQuestion(question)

    suspend fun deleteQuestion(question: QuestionEntity) = questionDao.deleteQuestion(question)

    suspend fun deleteQuestionById(id: Long) = questionDao.deleteQuestionById(id)

    suspend fun getQuestionById(id: Long): QuestionEntity? = questionDao.getQuestionById(id)

    suspend fun insertDocument(document: DocumentEntity): Long = documentDao.insertDocument(document)

    suspend fun updateDocument(document: DocumentEntity) = documentDao.updateDocument(document)

    suspend fun insertTest(test: TestEntity): Long = testDao.insertTest(test)

    suspend fun getTestById(id: Long): TestEntity? = testDao.getTestById(id)

    suspend fun insertAttempt(attempt: TestAttemptEntity): Long = testDao.insertAttempt(attempt)

    suspend fun getAttemptById(id: Long): TestAttemptEntity? = testDao.getAttemptById(id)

    suspend fun insertMistake(mistake: MistakeEntity): Long = mistakeDao.insertMistake(mistake)

    suspend fun insertMistakes(mistakes: List<MistakeEntity>): List<Long> = mistakeDao.insertMistakes(mistakes)

    suspend fun markMistakeResolved(id: Long) = mistakeDao.markResolved(id)

    suspend fun deleteMistake(mistake: MistakeEntity) = mistakeDao.deleteMistake(mistake)

    suspend fun seedInitialDataIfEmpty() {
        val existing = questionDao.getAllQuestions().first()
        if (existing.isEmpty()) {
            val starterQuestions = listOf(
                QuestionEntity(
                    questionText = "What is Newton's First Law of Motion, and what concept does it formulate?",
                    optionsJson = """["A. A body remains at rest or in uniform motion unless acted upon by a net force (Inertia)", "B. F = ma (Acceleration)", "C. Action = -Reaction (Recoil)", "D. Conservation of Angular Momentum"]""",
                    correctAnswer = "A. A body remains at rest or in uniform motion unless acted upon by a net force (Inertia)",
                    explanation = "Newton's first law states that an object will continue in its state of rest or uniform motion in a straight line unless an unbalanced external force acts upon it. It defines inertia.",
                    subject = "Physics",
                    chapter = "Laws of Motion",
                    topic = "Inertia & Momentum",
                    difficulty = "EASY",
                    difficultyReason = "Direct fundamental conceptual definition with no numerical calculations.",
                    questionType = "MCQ",
                    source = "PDF_UPLOAD",
                    sourceUrl = null,
                    isImportant = true
                ),
                QuestionEntity(
                    questionText = "A body of mass 5 kg is moving with an initial velocity of 10 m/s. A constant opposing force of 20 N acts on it. Find the time taken by the body to come to rest.",
                    optionsJson = """["A. 1.5 s", "B. 2.5 s", "C. 3.0 s", "D. 4.0 s"]""",
                    correctAnswer = "B. 2.5 s",
                    explanation = "Retardation a = F / m = 20 / 5 = 4 m/s². Using v = u - at => 0 = 10 - 4t => t = 10 / 4 = 2.5 seconds.",
                    subject = "Physics",
                    chapter = "Laws of Motion",
                    topic = "Newton's Second Law",
                    difficulty = "MEDIUM",
                    difficultyReason = "Combines Newton's second law calculation of deceleration with kinematic velocity-time equation.",
                    questionType = "MCQ",
                    source = "PDF_UPLOAD",
                    sourceUrl = null,
                    isImportant = true
                ),
                QuestionEntity(
                    questionText = "Two blocks of masses m₁ = 2 kg and m₂ = 3 kg are in contact on a frictionless horizontal table. A horizontal force F = 10 N is applied to m₁. Find the contact force between the blocks.",
                    optionsJson = """["A. 4 N", "B. 6 N", "C. 10 N", "D. 2 N"]""",
                    correctAnswer = "B. 6 N",
                    explanation = "Common acceleration a = F / (m₁ + m₂) = 10 / (2 + 3) = 2 m/s². The contact force on m₂ is F_c = m₂ * a = 3 kg * 2 m/s² = 6 N.",
                    subject = "Physics",
                    chapter = "Laws of Motion",
                    topic = "Normal Contact Forces",
                    difficulty = "MEDIUM",
                    difficultyReason = "Requires free body diagram (FBD) setup for connected bodies in contact.",
                    questionType = "MCQ",
                    source = "PDF_UPLOAD",
                    sourceUrl = null
                ),
                QuestionEntity(
                    questionText = "A block of mass 10 kg is placed on a rough horizontal surface with coefficient of static friction μ_s = 0.4 and kinetic friction μ_k = 0.3. A horizontal force of 35 N is applied. Find the frictional force acting on the block (g = 10 m/s²).",
                    optionsJson = """["A. 40 N", "B. 30 N", "C. 35 N", "D. 0 N"]""",
                    correctAnswer = "C. 35 N",
                    explanation = "Maximum limiting static friction f_s(max) = μ_s * N = 0.4 * (10 * 10) = 40 N. Since the applied force (35 N) is less than limiting friction (40 N), the block does not move and the static friction self-adjusts to equal the applied force = 35 N.",
                    subject = "Physics",
                    chapter = "Laws of Motion",
                    topic = "Static vs Kinetic Friction",
                    difficulty = "HARD",
                    difficultyReason = "Tests understanding of the self-adjusting nature of static friction and boundary condition analysis.",
                    questionType = "MCQ",
                    source = "PDF_UPLOAD",
                    sourceUrl = null,
                    isImportant = true
                ),
                QuestionEntity(
                    questionText = "Solve for x in the quadratic equation: x² - 5x + 6 = 0.",
                    optionsJson = """["A. x = 2, 3", "B. x = -2, -3", "C. x = 1, 6", "D. x = -1, -6"]""",
                    correctAnswer = "A. x = 2, 3",
                    explanation = "Factoring: (x - 2)(x - 3) = 0 => x = 2 or x = 3.",
                    subject = "Mathematics",
                    chapter = "Quadratic Equations",
                    topic = "Factorization Method",
                    difficulty = "EASY",
                    difficultyReason = "Basic quadratic factoring with integer roots.",
                    questionType = "MCQ",
                    source = "WEB_SEARCH",
                    sourceUrl = "https://khanacademy.org"
                ),
                QuestionEntity(
                    questionText = "If the roots of the equation x² - px + q = 0 differ by 1, which relation between p and q is correct?",
                    optionsJson = """["A. p² = 4q + 1", "B. p² = 4q - 1", "C. q² = 4p + 1", "D. p² = q + 4"]""",
                    correctAnswer = "A. p² = 4q + 1",
                    explanation = "Let roots be α and β with |α - β| = 1. (α - β)² = 1 => (α + β)² - 4αβ = 1. Since α + β = p and αβ = q, we get p² - 4q = 1 => p² = 4q + 1.",
                    subject = "Mathematics",
                    chapter = "Quadratic Equations",
                    topic = "Relations Between Roots",
                    difficulty = "MEDIUM",
                    difficultyReason = "Requires using algebraic identity for root difference and Vieta's relations.",
                    questionType = "MCQ",
                    source = "AI_GENERATED"
                ),
                QuestionEntity(
                    questionText = "Which orbital hybridization is present in the central carbon atom of methane (CH₄)?",
                    optionsJson = """["A. sp", "B. sp²", "C. sp³", "D. sp³d"]""",
                    correctAnswer = "C. sp³",
                    explanation = "In CH₄, Carbon forms 4 single sigma bonds with tetrahedral geometry and 109.5° bond angle, corresponding to sp³ hybridization.",
                    subject = "Chemistry",
                    chapter = "Chemical Bonding",
                    topic = "Hybridization",
                    difficulty = "EASY",
                    difficultyReason = "Standard conceptual identification of tetrahedral carbon geometry.",
                    questionType = "MCQ",
                    source = "AI_GENERATED"
                ),
                QuestionEntity(
                    questionText = "Calculate the molarity of a solution prepared by dissolving 4.0 g of NaOH in water to form 250 mL of solution (Molar mass of NaOH = 40 g/mol).",
                    optionsJson = """["A. 0.1 M", "B. 0.2 M", "C. 0.4 M", "D. 0.8 M"]""",
                    correctAnswer = "C. 0.4 M",
                    explanation = "Moles of NaOH = 4.0 g / 40 g/mol = 0.1 mol. Volume in liters = 250 / 1000 = 0.25 L. Molarity = 0.1 mol / 0.25 L = 0.4 M.",
                    subject = "Chemistry",
                    chapter = "Solutions & Stoichiometry",
                    topic = "Concentration Terms",
                    difficulty = "MEDIUM",
                    difficultyReason = "Requires two-step conversion from mass to moles and volume to liters.",
                    questionType = "MCQ",
                    source = "AI_GENERATED"
                ),
                QuestionEntity(
                    questionText = "What is the primary function of DNA Helicase during replication?",
                    optionsJson = """["A. Synthesizes RNA primers", "B. Unwinds and separates the DNA double helix", "C. Joins Okazaki fragments", "D. Proofreads base pairs"]""",
                    correctAnswer = "B. Unwinds and separates the DNA double helix",
                    explanation = "DNA Helicase breaks hydrogen bonds between nitrogenous bases to unwind and unzip the double helix at the replication fork.",
                    subject = "Biology",
                    chapter = "Molecular Basis of Inheritance",
                    topic = "DNA Replication Enzymes",
                    difficulty = "EASY",
                    difficultyReason = "Direct enzyme function identification.",
                    questionType = "MCQ",
                    source = "AI_GENERATED"
                ),
                QuestionEntity(
                    questionText = "A projectile is fired with velocity u at an angle θ with the horizontal. At what point in its trajectory is the speed minimum, and what is its value?",
                    optionsJson = """["A. At point of projection; u", "B. At maximum height; u cos θ", "C. At maximum height; 0", "D. Just before striking ground; u sin θ"]""",
                    correctAnswer = "B. At maximum height; u cos θ",
                    explanation = "At maximum height, the vertical component of velocity becomes zero (v_y = 0), leaving only the constant horizontal component v_x = u cos θ. Hence speed is minimum.",
                    subject = "Physics",
                    chapter = "Kinematics & Projectile Motion",
                    topic = "Projectile Trajectory",
                    difficulty = "MEDIUM",
                    difficultyReason = "Requires vector decomposition and understanding parabolic trajectory properties.",
                    questionType = "MCQ",
                    source = "AI_GENERATED",
                    isImportant = true
                )
            )

            val insertedIds = questionDao.insertQuestions(starterQuestions)

            // Create initial Starter Test
            val testId = testDao.insertTest(
                TestEntity(
                    title = "Physics & Mathematics Diagnostic Exam",
                    subject = "Physics & Mathematics",
                    targetQuestionsCount = 10,
                    durationMinutes = 15,
                    difficultyMode = "MIXED",
                    questionTypeMode = "MCQ",
                    sourceMode = "MIXED",
                    questionIdsJson = "[${insertedIds.joinToString(",")}]"
                )
            )

            // Add an initial sample document entry
            documentDao.insertDocument(
                DocumentEntity(
                    filename = "Class 11 Physics Chapter 5 - Laws of Motion.pdf",
                    fileType = "PDF",
                    pageCount = 4,
                    questionsExtractedCount = 4,
                    processingStatus = "COMPLETED"
                )
            )
        }
    }
}
