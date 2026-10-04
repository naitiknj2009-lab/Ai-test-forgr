# AI TestForge ⚡

[![Android APK Build](https://github.com/aistudio/ai-testforge/actions/workflows/android-build.yml/badge.svg)](https://github.com/aistudio/ai-testforge/actions/workflows/android-build.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![BYOK AI](https://img.shields.io/badge/BYOK-Gemini%20%7C%20OpenAI%20%7C%20Custom-purple.svg)](#byok-ai-architecture)

**AI TestForge** is an AI-powered question extraction, question generation, and test-taking exam simulator designed for students and educators. It transforms PDFs, handwritten question papers, photos, and textbook pages into structured question banks, creates timed examinations, rates question difficulty, and provides diagnostic performance analysis with targeted weak area practice drills.

---

## 🌟 Key Features

1. **Multimodal Document Upload & OCR**:
   - Upload PDF test papers, single/multiple image worksheets, or scan directly with the Camera scanner.
   - Real-time progress detection for questions, mathematical formulas, MCQs, options (A/B/C/D), and answer keys.

2. **AI Difficulty Rating Engine**:
   - Classifies every question as **🟢 Easy**, **🟡 Medium**, or **🔴 Hard** with detailed *"Why is it Hard?"* conceptual breakdowns.

3. **Searchable & Filterable Question Bank**:
   - Instant search across topics, chapters, and keywords.
   - 1-tap **"🔄 Similar Question"** generator to create parallel concept drills with varied numerical values.
   - Star / Bookmark important questions.

4. **AI Exam Question Generator**:
   - Generate custom question sets by Subject, Class/Grade, Chapter, Difficulty (*Easy, Medium, Hard, Mixed*), and Count (*5 to 30*).

5. **Find Questions Online (Web Search)**:
   - Sourced from open educational repositories (*OpenStax, MIT OCW, Khan Academy, LibreTexts*) with verifiable source citations.

6. **Distraction-Free Exam Simulator**:
   - 10-question tests with configurable timers (5m to 60m).
   - Live countdown clock, question palette navigation drawer, review flags, and automatic submission.

7. **Diagnostic Performance Analysis & Mistake Notebook**:
   - Detailed accuracy breakdowns and difficulty-wise performance.
   - AI identification of **Strong Topics** and **Weak Areas**.
   - **"🎯 Practice My Weak Areas"**: Generates a targeted 5-question adaptive drill for struggling concepts.
   - Mistake Notebook archives incorrect answers for retry and mastery.

---

## 🔐 BYOK (Bring Your Own Key) AI Architecture

AI TestForge is designed with a zero hard-coded secret policy. Students and educators configure their own AI providers:

- **Supported Providers**:
  - **Google Gemini** (`gemini-3.5-flash`, `gemini-3.1-pro-preview`, `gemini-3.1-flash-lite-preview`, custom model ID)
  - **OpenAI** (`gpt-4o`, `gpt-4o-mini`, `o1-mini`)
  - **Custom / OpenAI-Compatible** (Groq, OpenRouter, Together AI, Ollama, LM Studio, or local servers)
- **Security & Storage**:
  - API keys are encrypted at rest using **Android Keystore (AES-GCM)**.
  - Keys are never logged, never exported in settings backups, and never stored in plain text.
- **Task-Specific Routing**:
  - Optional granular model assignment for OCR/Vision, Question Generation, and Step Explanations.
- **Connection Test**:
  - In-app **Test Ping** button validates API key status, model availability, and quotas before taking exams.

---

## 🛠 Tech Stack

- **UI Framework**: Jetpack Compose, Material 3, Dark Luxury / Glassmorphism theme
- **Language**: Kotlin with Coroutines & StateFlow
- **Database**: Room Database with offline-first persistence
- **Networking**: Retrofit, OkHttp (60s timeouts), Moshi
- **Camera & Media**: CameraX, Android Photo Picker
- **CI/CD**: GitHub Actions automated APK builds

---

## 🚀 CI/CD & Building from Source

### Automated GitHub Actions Build
Whenever code is pushed to `main` or triggered via `workflow_dispatch`, GitHub Actions automatically:
1. Sets up JDK 17 and Android SDK.
2. Runs unit and Robolectric tests (`./gradlew testDebugUnitTest`).
3. Compiles the debug APK (`./gradlew assembleDebug`).
4. Uploads `AI-TestForge-debug-apk` as an artifact under GitHub Actions.

### Local Development
```bash
# Clone the repository
git clone https://github.com/your-username/AI-TestForge.git
cd AI-TestForge

# Run unit tests
./gradlew testDebugUnitTest

# Build APK
./gradlew assembleDebug
```
The resulting APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License
Released under the Apache 2.0 License.
