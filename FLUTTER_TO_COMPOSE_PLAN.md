# Flutter to Jetpack Compose Migration Plan

## Objective
Port the fully featured Flutter implementation (`Wallet-Flutter`) to the native Android Jetpack Compose implementation (`Wallet`).

## Phase 1: Data Layer Parity
1. **Entities:** Copy models from Flutter Isar models to Compose Room entities (`AccountEntity`, `TransactionEntity`, `CategoryEntity`, `BudgetEntity`, `GoalEntity`, `PersonEntity`, `LoanEntity`, `RecurringEntity`).
2. **DAOs:** Create Room DAOs for CRUD operations corresponding to the Isar services.
3. **Database Setup:** Configure `WalletDatabase.kt` and run a test build.
4. **Repository Layer:** Create Repository interfaces and implementations for all data sources.

## Phase 2: State Management & ViewModels
1. **DI Setup:** Ensure Hilt/Dagger is configured.
2. **ViewModels:** Create ViewModels for all features (Home, Accounts, Transactions, People, Reports, Settings) using Kotlin Flow / StateFlow to match Riverpod's reactivity.
3. **Formatters/Engines:** Port `CurrencyEngine` and formatting utilities from Dart to Kotlin.

## Phase 3: Core UI Components (Design System)
1. **Theme:** Port `color_schemes.dart` to `Color.kt` and `Theme.kt` (Material 3).
2. **Typography:** Port `Google Sans Flex` setup.
3. **Atomic Widgets:** Build compose equivalents for `PrimaryAtelierButton`, `ExpressiveBottomSheet`, `IconPickerWidget`, `PersonAvatar`.
4. **Scaffold & Navigation:** Implement `MainAppScreen` with floating bottom bar and contextual FAB (mirroring `app/router.dart` from Flutter).

## Phase 4: Screen-by-Screen Porting
1. **Home Tab:** `HomePage`, `BudgetsPage`, `BillSplitterPage`, `GoalsPage`, `LoansPage`, `ActivityHeatmapPage`.
2. **Accounts Tab:** `AccountsPage`, `AccountDetailsPage`, Add/Edit forms.
3. **Transactions:** `AddTransactionPage`, `AllTransactionsPage`.
4. **Reports Tab:** `ReportsPage`, `PaisaDonutChart`, Spending breakdowns.
5. **People Tab:** `PeoplePage`, `PersonDetailsPage`.
6. **Settings Tab:** Port all personalization, categories, currency, and profile pages.

## Phase 5: Polish & Testing
1. Ensure M3 geometry and tactile animations (Haptics) are intact.
2. Build, run, and resolve compile errors.
3. Final Git Push to GitHub.

---
**Status**: Initiating Phase 1.