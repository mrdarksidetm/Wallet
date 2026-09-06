# Project Version History - Wallet (Native Jetpack Compose)

## Libraries & Tools
- **Android Gradle Plugin (AGP):** 8.7.3
- **Kotlin:** 2.0.21
- **Compose Compiler Plugin:** 2.0.21
- **Compose BOM:** 2024.12.01
- **Material 3:** 1.3.1 (via BOM)
- **Material Icons Extended:** 1.7.6
- **Room Database:** 2.6.1 (with KSP)
- **Lifecycle Runtime & ViewModel Compose:** 2.8.7
- **Navigation Compose:** 2.8.5
- **Font Family:** Google Sans Flex (Offline bundled in `res/font/google_sans_flex.ttf`)

---

## [2026-09-06 21:00] - Initial Native Compose Core Architecture
- **Action:** Initial setup of Android Jetpack Compose native architecture.
- **Components:**
  - Room database (`WalletDatabase.kt`) with DAOs and entities for Accounts, Categories, Goals, Budgets, People, and Recurring Rules.
  - Unified Repository (`WalletRepository.kt`) providing reactive `Flow` data streams.
  - `WalletViewModel.kt` combining flows with `stateIn(WhileSubscribed(5000))`.
  - Material 3 theme system (`Theme.kt`, `Color.kt`, `PaletteStyle.kt`, `Type.kt`) supporting 10 dynamic palette styles and dark/light/AMOLED modes.
- **Status:** 100% (Core backend and data layer initialized).

## [2026-09-06 22:18] - Material 3 Expressive UI/UX Transformation
- **Action:** Major UI/UX overhaul to bring native Jetpack Compose Wallet on par with and exceeding the Wallet-Flutter Improv sandbox.
- **Architectural & Design Alignments:**
  - Strictly followed Material 3 Expressive design language and Native Android Mandate.
  - Utilized native Android primitives and Compose `Canvas` for custom visualizations (60-120 FPS hardware acceleration).
- **Files Created:**
  - `app/src/main/java/com/darkytm/wallet/ui/components/HomeHeader.kt`:
    - Contextual dynamic greeting (`Good Morning / Good Afternoon / Good Evening, User`) based on local time.
    - Glowing shadow vector logo mark.
    - Theme customizer trigger and interactive user profile avatar.
  - `app/src/main/java/com/darkytm/wallet/ui/components/AnimatedBalanceHero.kt`:
    - Material 3 Expressive card container (32dp rounded radius).
    - Native Compose `Canvas` organic background blobs (Primary & Tertiary radial gradients animated with `rememberInfiniteTransition` and ease loops).
    - Info dialog explaining net worth aggregation.
    - Eye visibility toggle button with obscured dot masking (`••••••••`).
    - "This month" spending percentage tag and dynamic `LinearProgressIndicator` shifting from primary to error red above 90% utilization.
    - Mini stat pills for Income (green trending up) and Expense (red trending down).
  - `app/src/main/java/com/darkytm/wallet/ui/components/OverviewGridSection.kt`:
    - 2-Column Expressive Grid with 8 Financial Hubs: Accounts, Budgets, Goals, Loans, Recurring, Categories, Bill Splitter, People.
    - Squircle icon containers with color tints, bold titles, real-time stat subtitles, and chevron navigation indicators.
  - `app/src/main/java/com/darkytm/wallet/ui/components/ActivityInsightsSection.kt`:
    - **Calendar Heatmap Card**: Month navigation (`< Month YYYY >`), weekday row (`M T W T F S S`), intensity-shaded day cells based on daily transaction totals, and interactive day detail chip.
    - **Activity Trends Card (30 days)**: Income & Expense summary badges, native Compose `Canvas` cubic bezier sparkline curve with vertical gradient fill and peak markers.
  - `app/src/main/java/com/darkytm/wallet/ui/components/RecentTransactionsSection.kt`:
    - Segmented filter control (`All`, `Expense`, `Income`, `Transfer`) with animated pill indicator.
    - Expressive transaction item rows with squircle category badges and relative timestamps (`Today`, `Yesterday`, or formatted date).
    - Interactive transaction detail modal bottom sheet with complete entity relations and deletion confirmation.
    - Stylized empty state with quick action button.
  - `app/src/main/java/com/darkytm/wallet/ui/components/OverviewDetailSheets.kt`:
    - Interactive modal bottom sheets for all 8 categories: Accounts list, Budgets progress list, Goals targets, Loans/Debts ledger, Recurring rules list, Categories list, Contacts list.
    - Fully interactive Bill Splitter calculator with tip percentage selector (0%, 10%, 15%, 20%) and people stepper (+ / -).
- **Files Modified:**
  - `app/src/main/java/com/darkytm/wallet/data/repository/WalletRepository.kt`:
    - Added `observeMonthlyStats()` flow calculating monthly income and monthly expense.
    - Added `MonthlyStats` data model.
  - `app/src/main/java/com/darkytm/wallet/ui/WalletViewModel.kt`:
    - Added `allTransactions`, `recurringRules`, `monthlyIncome`, `monthlyExpense`, and `isBalanceVisible` to `WalletUiState`.
    - Added `toggleBalanceVisibility()` and `addRecurringRule()`.
  - `app/src/main/java/com/darkytm/wallet/ui/screens/HomeScreen.kt`:
    - Replaced basic flat UI with the complete Material 3 Expressive experience integrating all 6 new modular components.
    - Preserved `ThemeCustomizerDialog` for dynamic theme/palette customization.
- **Status:** 100% (Complete, validated, and verified).

## [2026-09-06 22:22] - Transfer Account Routing & Input Refinement
- **Action:** Resolved destination account routing for transfers and refined input ergonomics.
- **Files Modified:**
  - `app/src/main/java/com/darkytm/wallet/ui/screens/AddEntryScreen.kt`:
    - Added `selectedToAccountId` state and dynamic destination account selection chips for `TransactionType.TRANSFER`.
    - Integrated `KeyboardType.Decimal` on the amount field to ensure proper numeric entry.
    - Added dedicated note input field.
    - Refined Material 3 Expressive input shapes, button styling, and layout spacing.
- **Status:** 100% (Complete, verified, ready for GitHub remote verification).
