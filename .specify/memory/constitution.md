<!--
Sync Impact Report
- Version change: 0.0.0 → 1.0.0
- List of modified principles:
  - PRINCIPLE_1: Compose MVVM Architecture (New)
  - PRINCIPLE_2: Synchronization Protocol (New)
  - PRINCIPLE_3: Local Database Mandate (New)
  - PRINCIPLE_4: Native UI Performance (New)
- Added sections:
  - Technical Stack (Jetpack Compose)
- Removed sections: None
- Templates requiring updates: None
-->

# Wallet (Compose) Constitution

## Core Principles

### I. Compose MVVM Architecture
The application must follow a strict Unidirectional Data Flow.
- **Rule**: Use MVVM (Model-View-ViewModel).
- **Rule**: State flows down from ViewModels as StateFlows; Events flow up from the UI.
- **Rationale**: Ensures predictable UI states and easier debugging.

### II. Synchronization Protocol (CRITICAL)
The Android Compose and Flutter codebases must remain in perfect feature parity.
- **Rule**: Whenever a new feature, UI change, or database alteration is made, update `SPEC.md`.
- **Rule**: The `SPEC.md` file MUST be identical in BOTH repositories (`Wallet` and `Wallet-Flutter`).
- **Rationale**: Single Source of Truth across platforms.

### III. Local Database Mandate
User data is private and offline-first.
- **Rule**: 100% Local data storage using **Room Database (SQLite)**.
- **Rule**: Use Kotlin Coroutines & Flow for reactive UI updates from Room.
- **Rationale**: Privacy is a core feature.

### IV. Native UI Performance
The app must feel fluid, tactile, and native to Android 14.
- **Rule**: Use Jetpack Compose (Material 3 Expressive).
- **Rule**: Charts must be built using native Compose `Canvas` (no heavy 3rd-party charting libraries).
- **Rule**: Dependency Injection is Manual (Custom ViewModelProviders) to save compilation memory; no Hilt/Dagger.
- **Rationale**: Minimizes APK bloat and ensures maximum runtime performance.

## Technical Stack

- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Unidirectional Data Flow
- **Database**: Room Database (SQLite)
- **Navigation**: `androidx.navigation:navigation-compose`
- **Charts**: Native Compose `Canvas`

## Governance

- **Supremacy**: This Constitution supersedes all other development practices in the `Wallet` (Compose) project.
- **Amendments**: Changes require a version bump and an update to the Sync Impact Report.
- **Parity Checks**: All PRs must verify compliance with the shared `SPEC.md`.

**Version**: 1.0.0 | **Ratified**: 2026-04-04 | **Last Amended**: 2026-04-04
