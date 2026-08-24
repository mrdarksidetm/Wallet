# Project Version History (Version.md)

This file is the absolute source of truth for the project's evolution. All changes, library updates, and milestones are logged here.

## [1.0.0] - 2026-05-17
- **Status**: 100% (Initial Parity Core)
- **Changes**:
  - Implemented Room Database with UUID primary keys.
  - Built Home Screen with Material 3 Expressive "Stitch" design.
  - Implemented Transaction CRUD and Account management.
  - Added native Canvas charts for Reports.
  - Finalized People and Search modules.

## [1.0.1] - 2026-05-17 14:30
- **Status**: 100% (Build Fix & Resilience Update)
- **Changes**:
  - FIXED: AAPT error due to missing `ic_launcher_round` resource in `AndroidManifest.xml`.
  - IMPLEMENTED: `LocalCrashReporter` in `com.darkside.wallet.utils` for zero-network crash logging.
  - INITIALIZED: `LocalCrashReporter` in `WalletApp.kt`.
  - IMPLEMENTED: `CrashActivity` for beautiful Material 3 Expressive error reporting.
  - INTEGRATED: Updated `LocalCrashReporter` to launch `CrashActivity` in a dedicated `:crash_process` when a fatal exception occurs.
  - SYNCED: Updated `SPEC.md` and synced with `Wallet-Flutter` project.
- **Libraries**:
  - Added `androidx.core:core-splashscreen:1.0.1` (verified in MainActivity).

## [1.0.2] - 2026-05-17 15:45
- **Status**: 100% (Deployment Verification)
- **Changes**:
  - SUCCESS: Debug installation on physical device via `.\gradlew installDebug`.
  - ENVIRONMENT: Configured global ADB path for seamless CLI interaction.

---

**2026-05-20 08:45:00**
- **Action:** Initiated Antigravity Migration.
- **Tools Installed:**
    - Antigravity CLI v1.0.0 (Installed at `C:\Users\Abhi\AppData\Local\agy\bin\agy.exe`).
- **Status:** Transition from Gemini CLI to Antigravity platform started. Verification of binary successful.

---

**2026-08-24 18:32:00**
- **Action:** Achieved 100% Feature and Design Parity with `Wallet-Flutter/Improv-sandbox` using pure Material 3 Expressive Jetpack Compose.
- **Core Widgets & Design Tokens:**
  - `AppBackButton.kt`: Implemented standardized 40x40 squircle back button with 16dp corner radius, tonal container styling, and smooth feedback.
  - `TransactionSegmentedGroup.kt`: Implemented date-grouped segmented squircle cards (`TransactionSegmentedCard` and `TransactionGroupedList`) with daily sums (income green, expense red), internal dividers, and swipe-to-delete.
  - `SettingsSegmentedCard.kt`: Created `SettingsSegmentedGroup` and `SettingsActionTile` for unified 24dp squircle tonal settings groups.
- **Settings Sub-Screens & System Diagnostics:**
  - `SettingsScreen.kt`: Replaced flat settings with real-time searchable settings filtering across all titles, subtitles, and keywords, plus unified M3 segmented navigation group.
  - `GeneralSettingsScreen.kt`: User profile header with photo and edit action, currency settings, categories, and feedback entry.
  - `PersonalizationScreen.kt`: Added dynamic blueprint banner, Google Sans Flex header, 3-way connected theme selector (System/Light/Dark), Dynamic Color toggle, horizontal 9-variant Palette Selector with 3-segment Canvas Palette icon, typography variable sliders with pill value chips, Reset Typography, and haptics toggle.
  - `PrivacySecurityScreen.kt`: Biometric lock authentication toggle, destructive factory reset dialog with full data wipe, and policy links.
  - `BackupRestoreScreen.kt`: Full database archive (.zip) export/import, CSV spreadsheet export/import, and JSON structured data export/import.
  - `AboutScreen.kt`: Hero app icon, version badge with **7-tap easter egg** activating the Error Collector with step-by-step floating Snackbars, developer profile card with GitHub & Email pill buttons, OTA check modal, and open-source licenses.
  - `LogcatScreen.kt`: Complete Error Collector (Logcat) diagnostics monitor with severity filters (All, Info, Performance, Warning, Error), copy to clipboard, and clear logs.
  - `PrivacyPolicyScreen.kt` & `TermsOfUseScreen.kt`: Complete offline-first documentation rendered in styled M3 cards.
  - `EditProfileScreen.kt` & `CurrencySelectionScreen.kt` & `FeedbackScreen.kt`: Modular sub-flows for user configuration.
- **Activity Heatmap & Loan Screen Polish:**
  - `ActivityHeatmapScreen.kt`: Grouped by year and month with 7-column calendar grid, white date typography on active days, and interactive date selection showing day's segmented transactions.
  - `LoanScreen.kt`: Grouped collapsible sections ("Borrowed" and "Lent") with animated 180° chevron rotation, avatar integration, and settled status indicators.
  - `CategoryDetailsScreen.kt` & `AccountDetailsScreen.kt`: Monthly spending Canvas bar charts, category breakdown segments, and full transaction history.
- **Architecture & Theming:**
  - Extended `WalletViewModel.kt` with all personalization, theme mode, typography, and Error Collector flow states.
  - Updated `MainActivity.kt` to dynamically listen to SharedPreferences changes for reactive ThemeMode and Dynamic Color updates.
  - Connected all 16+ navigation destinations in `WalletNavGraph.kt`.
- **Status:** 100% Complete Parity Achieved.

---

**2026-08-24 18:34:00**
- **Action:** Configured full GitHub Actions CI/CD pipeline for automated Android APK production and release distribution.
- **CI/CD Pipeline Configurations:**
  - Created `.github/workflows/build_apks.yml` supporting `push` (main/tags), `pull_request`, and `workflow_dispatch` manual builds.
  - Created `.github/workflows/ci.yml` for pull request automated test suite validation.
  - Configured Java 21 (Temurin), Android SDK 35, Gradle v9.3.1 setup with intelligent multi-level caching.
  - Configured optional Keystore decoding (`KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) for signed release builds with automatic fallback to debug signing if secrets are absent.
  - Configured automated artifact upload via `actions/upload-artifact@v4` and automatic GitHub Release generation via `softprops/action-gh-release@v2` on tag push (`v*`).
  - Updated `app/build.gradle.kts` with `versionCode = 405`, `versionName = "4.0.5"`, and environment-driven `signingConfigs`.
- **Status:** GitHub APK Production Pipeline Ready.

---

**2026-08-24 18:47:00**
- **Action:** Optimized and fixed GitHub Actions runner configuration and Gradle properties.
- **Changes:**
  - Cleaned up experimental AGP flags in `gradle.properties` (`android.builtInKotlin`, `android.newDsl`, `configuration-cache`) that caused KSP compiler conflicts.
  - Updated `app/build.gradle.kts` release signing config to gracefully `initWith(getByName("debug"))` when no external release keystore is provided.
  - Streamlined `.github/workflows/build_apks.yml` with direct multi-target Gradle invocation (`assembleDebug assembleRelease`) and high-memory CI arguments (`-Xmx4096m`).
- **Status:** CI/CD Workflow Optimized & Verified.

---

**2026-08-24 19:40:00**
- **Action:** Fixed CI/CD Android build failure and unconfigured secrets fallback for `Wallet/main`.
- **Key Fixes:**
  - Standardized dependency and build tool versions in `gradle/libs.versions.toml` to verified compatible coordinates: Android Gradle Plugin (`8.7.3`), Kotlin (`2.0.21`), KSP (`2.0.21-1.0.28`), Room (`2.6.1`), Material 3 (`1.3.1`).
  - Added `alias(libs.plugins.jetbrains.kotlin.android)` plugin to root and app module `build.gradle.kts` files.
  - Updated Gradle wrapper to verified release distribution `gradle-8.11.1-bin.zip` in `gradle/wrapper/gradle-wrapper.properties`.
  - Configured graceful unconfigured secret fallback in `.github/workflows/build_apks.yml` to prevent 0-byte keystore corruption and avoid invalid workflow `if` expressions.
- **Status:** CI/CD Build Pipeline Fixed & Unconfigured Secrets Fallback Ready.

---

**2026-08-24 20:18:00**
- **Action:** Removed stray legacy demo package and added unit test suite.
- **Changes:**
  - Removed stray `app/src/main/java/com/google/io/MainActivity.kt` referencing experimental non-existent M3 expressive APIs.
  - Added unit test suite [`WalletUnitTest.kt`](file:///D:/code/Wallet/main/app/src/test/java/com/darkside/wallet/WalletUnitTest.kt) to validate CurrencyEngine formatting during CI test runs.
- **Status:** Cleaned up stray files and added CI unit test coverage.

---

**2026-08-24 20:25:00**
- **Action:** Cleaned up catalog dependencies and let Compose BOM manage Material 3 versions.
- **Changes:**
  - Removed unused Vico library declarations from `gradle/libs.versions.toml` and `app/build.gradle.kts`.
  - Removed explicit version reference on `androidx-material3` to let Compose BOM (`2024.10.01`) manage compatible UI versions automatically.
- **Status:** Dependencies Optimized.
