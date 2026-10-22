# Block Puzzle Arcade 🧩

A responsive, mobile-first Block Puzzle Game built with React, TypeScript, and Vite. Features a 10×10 grid, typed block shapes, smooth tap/drag controls, line clearing, combo scoring, persistent stats, themes, synthetic audio effects, and robust game-over detection. Packaged for Android via Capacitor and automated with GitHub Actions.

---

## Features
- **10×10 Puzzle Board**: Clean grid with live placement preview.
- **Typed Block Shapes**: Classic single, domino, tromino, tetromino, and pentomino shapes with vibrant arcade color palettes.
- **Dual Controls**: Tap-to-select and tap-to-place with instant validation.
- **Simultaneous Row & Column Clearing**: Clear multiple lines at once for bonus score multipliers.
- **Audio & Haptics**: Web Audio API synthetic sound effects for arcade feedback.
- **Stats & Persistence**: High score, total score, lines cleared, and settings saved securely in `localStorage`.
- **Themes**: Multiple theme options (Neon Arcade, Cyberpunk, Midnight Glass, Sunset Glow).

---

## Setup & Development

1. **Install Dependencies**:
   ```bash
   npm install
   ```

2. **Run Development Server**:
   ```bash
   npm run dev
   ```

3. **Run Unit Tests**:
   ```bash
   npm test
   ```

4. **Production Build**:
   ```bash
   npm run build
   ```

---

## Android APK Generation (Capacitor & GitHub Actions)

### Android Configuration
- **Application ID**: `com.naitik.blockpuzzle` *(Change in `capacitor.config.ts` if publishing to Google Play Store)*.
- **App Name**: `Block Puzzle`
- **Web Output Directory**: `dist`

### Building the APK Locally
If you have Android Studio / Android SDK installed locally:
1. Sync web assets with Capacitor:
   ```bash
   npm run capacitor:sync
   ```
2. Open Android Studio:
   ```bash
   npx cap open android
   ```
3. Build the APK via **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

### Automated Cloud Build (GitHub Actions)
The repository includes a GitHub Actions workflow located at `.github/workflows/android-apk.yml` that automatically builds the debug APK on pushes to `main` or manual triggers.

#### How to Download the APK from GitHub:
1. Go to your repository on GitHub.
2. Click on the **Actions** tab.
3. Select the latest **Build Android APK** workflow run.
4. Scroll down to the **Artifacts** section and download `app-debug`.
5. Extract the ZIP file to get your `app-debug.apk` file ready to install on your Android device!

---

## Deployment Guide

### Deploying to Vercel (Recommended)
1. Push your repository to GitHub.
2. Import your repository into [Vercel](https://vercel.com).
3. Vercel automatically detects **Vite** and configures the build settings:
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. Click **Deploy**.

### Deploying to GitHub Pages
1. Install `gh-pages` or configure GitHub Actions to build and publish the `dist/` directory.
2. In `vite.config.ts`, set `base: '/your-repo-name/'`.
3. Build the project:
   ```bash
   npm run build
   ```
4. Deploy the contents of the `dist/` directory to your `gh-pages` branch.
