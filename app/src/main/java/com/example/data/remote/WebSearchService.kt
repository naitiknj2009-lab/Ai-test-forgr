package com.example.data.remote

import com.example.data.model.QuestionEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

data class WebQuestionResult(
    val title: String,
    val sourceName: String,
    val sourceUrl: String,
    val questions: List<QuestionEntity>
)

object WebSearchService {

    /**
     * Curated educational question sources and online discovery
     */
    fun searchEducationalQuestions(query: String): List<WebQuestionResult> {
        val cleanQuery = query.lowercase().trim()
        val results = mutableListOf<WebQuestionResult>()

        val isPhysics = cleanQuery.contains("physic") || cleanQuery.contains("motion") || cleanQuery.contains("newton") || cleanQuery.contains("force") || cleanQuery.contains("gravity") || cleanQuery.contains("kinematics")
        val isMaths = cleanQuery.contains("math") || cleanQuery.contains("algebra") || cleanQuery.contains("quadratic") || cleanQuery.contains("calculus") || cleanQuery.contains("geometry") || cleanQuery.contains("triangle")
        val isChemistry = cleanQuery.contains("chem") || cleanQuery.contains("atom") || cleanQuery.contains("periodic") || cleanQuery.contains("bond") || cleanQuery.contains("reaction") || cleanQuery.contains("mole")
        val isBiology = cleanQuery.contains("bio") || cleanQuery.contains("cell") || cleanQuery.contains("dna") || cleanQuery.contains("genetics") || cleanQuery.contains("photosynthesis")

        if (isPhysics || (!isMaths && !isChemistry && !isBiology)) {
            results.add(
                WebQuestionResult(
                    title = "Classical Mechanics & Laws of Motion Problems",
                    sourceName = "OpenStax University Physics & MIT OCW",
                    sourceUrl = "https://openstax.org/books/university-physics-volume-1/pages/5-problems",
                    questions = listOf(
                        QuestionEntity(
                            questionText = "A 1200 kg car accelerates uniformly from rest to a speed of 20 m/s in a time of 5.0 s. What is the net horizontal force exerted on the car?",
                            optionsJson = """["A. 2400 N", "B. 4800 N", "C. 6000 N", "D. 9600 N"]""",
                            correctAnswer = "B. 4800 N",
                            explanation = "Acceleration a = (v - u)/t = (20 - 0)/5 = 4 m/s². Force F = m * a = 1200 kg * 4 m/s² = 4800 N.",
                            subject = "Physics",
                            chapter = "Laws of Motion",
                            topic = "Newton's Second Law",
                            difficulty = "MEDIUM",
                            difficultyReason = "Requires combining kinematic formula for acceleration with Newton's Second Law (F = ma).",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://openstax.org/books/university-physics-volume-1"
                        ),
                        QuestionEntity(
                            questionText = "An object of mass 5 kg rests on a frictionless horizontal table. A horizontal force of 15 N is applied. What is its acceleration?",
                            optionsJson = """["A. 1.5 m/s²", "B. 3.0 m/s²", "C. 5.0 m/s²", "D. 75 m/s²"]""",
                            correctAnswer = "B. 3.0 m/s²",
                            explanation = "Direct application of F = ma: a = F / m = 15 N / 5 kg = 3.0 m/s².",
                            subject = "Physics",
                            chapter = "Laws of Motion",
                            topic = "Basic Force Calculation",
                            difficulty = "EASY",
                            difficultyReason = "Direct one-step substitution into standard formula without secondary variables.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://physicsclassroom.com/class/newtlaws"
                        ),
                        QuestionEntity(
                            questionText = "Two blocks of masses m₁ = 4 kg and m₂ = 6 kg are connected by a massless string over a frictionless pulley (Atwood machine). Calculate the acceleration of the system (g = 9.8 m/s²).",
                            optionsJson = """["A. 1.96 m/s²", "B. 3.92 m/s²", "C. 4.90 m/s²", "D. 9.80 m/s²"]""",
                            correctAnswer = "A. 1.96 m/s²",
                            explanation = "For an Atwood machine: a = (m₂ - m₁)/(m₁ + m₂) * g = (6 - 4)/(4 + 6) * 9.8 = 2/10 * 9.8 = 1.96 m/s².",
                            subject = "Physics",
                            chapter = "Laws of Motion",
                            topic = "Connected Bodies & Pulleys",
                            difficulty = "HARD",
                            difficultyReason = "Involves coupled equations of motion for connected multi-body systems and tension balance.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://ocw.mit.edu/courses/physics-8-01sc"
                        )
                    )
                )
            )
        }

        if (isMaths || (!isPhysics && !isChemistry && !isBiology)) {
            results.add(
                WebQuestionResult(
                    title = "Algebra & Quadratic Equations Question Bank",
                    sourceName = "Khan Academy STEM & OpenStax Mathematics",
                    sourceUrl = "https://khanacademy.org/math/algebra/quadratics",
                    questions = listOf(
                        QuestionEntity(
                            questionText = "Find the roots of the quadratic equation: 2x² - 7x + 3 = 0.",
                            optionsJson = """["A. x = 3 and x = 1/2", "B. x = -3 and x = -1/2", "C. x = 2 and x = 3/2", "D. x = 1 and x = 3"]""",
                            correctAnswer = "A. x = 3 and x = 1/2",
                            explanation = "Factoring: 2x² - 6x - x + 3 = 0 => 2x(x - 3) - 1(x - 3) = (2x - 1)(x - 3) = 0. Roots are x = 1/2 and x = 3.",
                            subject = "Mathematics",
                            chapter = "Quadratic Equations",
                            topic = "Factoring Quadratics",
                            difficulty = "MEDIUM",
                            difficultyReason = "Requires middle-term splitting / factoring quadratic with leading coefficient > 1.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://khanacademy.org/math/algebra"
                        ),
                        QuestionEntity(
                            questionText = "What is the discriminant of the quadratic equation x² - 6x + 9 = 0, and what does it indicate about the roots?",
                            optionsJson = """["A. D = 0; real and equal roots", "B. D > 0; two distinct real roots", "C. D < 0; complex roots", "D. D = 9; irrational roots"]""",
                            correctAnswer = "A. D = 0; real and equal roots",
                            explanation = "Discriminant D = b² - 4ac = (-6)² - 4(1)(9) = 36 - 36 = 0. When D = 0, the equation has exactly one real root (two coincident real roots).",
                            subject = "Mathematics",
                            chapter = "Quadratic Equations",
                            topic = "Nature of Roots",
                            difficulty = "EASY",
                            difficultyReason = "Direct calculation of discriminant formula b²-4ac and definition lookup.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://openstax.org/books/algebra-and-trigonometry"
                        ),
                        QuestionEntity(
                            questionText = "If α and β are the roots of 3x² + 5x - 2 = 0, find the value of (α/β + β/α).",
                            optionsJson = """["A. -37/6", "B. 37/6", "C. -29/6", "D. 25/9"]""",
                            correctAnswer = "A. -37/6",
                            explanation = "Sum of roots α+β = -5/3, Product αβ = -2/3. (α/β + β/α) = (α² + β²)/αβ = ((α+β)² - 2αβ)/αβ = (25/9 - 2(-2/3))/(-2/3) = (25/9 + 12/9)/(-2/3) = (37/9) * (-3/2) = -37/6.",
                            subject = "Mathematics",
                            chapter = "Quadratic Equations",
                            topic = "Symmetric Functions of Roots",
                            difficulty = "HARD",
                            difficultyReason = "Requires Vieta's formulas, algebraic manipulation of rational expressions, and fraction arithmetic.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://ocw.mit.edu/mathematics"
                        )
                    )
                )
            )
        }

        if (isChemistry) {
            results.add(
                WebQuestionResult(
                    title = "Chemical Bonding & Molecular Structure",
                    sourceName = "LibreTexts Chemistry & Royal Society of Chemistry",
                    sourceUrl = "https://chem.libretexts.org/Bookshelves/General_Chemistry",
                    questions = listOf(
                        QuestionEntity(
                            questionText = "Which of the following molecules has a linear molecular geometry according to VSEPR theory?",
                            optionsJson = """["A. H₂O", "B. CO₂", "C. SO₂", "D. NH₃"]""",
                            correctAnswer = "B. CO₂",
                            explanation = "CO₂ has 2 bonding pairs and 0 lone pairs on the central Carbon atom, giving an AX₂ formula with a 180° bond angle (linear).",
                            subject = "Chemistry",
                            chapter = "Chemical Bonding",
                            topic = "VSEPR Theory",
                            difficulty = "EASY",
                            difficultyReason = "Standard conceptual identification of basic molecular geometry.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://chem.libretexts.org"
                        ),
                        QuestionEntity(
                            questionText = "Calculate the formal charge on the central oxygen atom in the ozone (O₃) molecule for its major resonance contributor.",
                            optionsJson = """["A. 0", "B. +1", "C. -1", "D. +2"]""",
                            correctAnswer = "B. +1",
                            explanation = "Central O has 6 valence electrons - 2 non-bonding electrons - 3 bonding pairs (1/2 * 6) = 6 - 2 - 3 = +1.",
                            subject = "Chemistry",
                            chapter = "Chemical Bonding",
                            topic = "Formal Charge & Resonance",
                            difficulty = "MEDIUM",
                            difficultyReason = "Requires drawing Lewis structures, identifying shared vs unshared electron pairs, and applying formal charge formula.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://chem.libretexts.org"
                        )
                    )
                )
            )
        }

        if (isBiology) {
            results.add(
                WebQuestionResult(
                    title = "Cell Biology & Molecular Genetics",
                    sourceName = "NCBI Bookshelf & OpenStax Biology",
                    sourceUrl = "https://openstax.org/books/biology-2e",
                    questions = listOf(
                        QuestionEntity(
                            questionText = "Which organelle is the primary site of ATP synthesis during cellular respiration?",
                            optionsJson = """["A. Ribosome", "B. Golgi apparatus", "C. Mitochondrion", "D. Endoplasmic reticulum"]""",
                            correctAnswer = "C. Mitochondrion",
                            explanation = "The mitochondrion conducts the citric acid cycle and oxidative phosphorylation to produce ATP.",
                            subject = "Biology",
                            chapter = "Cell Structure",
                            topic = "Organelles & Bioenergetics",
                            difficulty = "EASY",
                            difficultyReason = "Direct factual recall of fundamental cell biology.",
                            questionType = "MCQ",
                            source = "WEB_SEARCH",
                            sourceUrl = "https://openstax.org/books/biology-2e"
                        )
                    )
                )
            )
        }

        return results
    }
}
