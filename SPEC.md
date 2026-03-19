# SPEC.md — Wallet App Design Specification
# ═══════════════════════════════════════════
# This file is IDENTICAL in both the Compose and Flutter repos.
# It is the single source of truth for feature parity between both versions.
# When you update a feature, update THIS SPEC first, then implement in both codebases.
#
# Last synced: 2026-03-19

## 1. App Identity

| Field          | Value                                   |
|----------------|------------------------------------------|
| Name           | Wallet                                  |
| Package (Android) | `com.mrdarksidetm.wallet`            |
| Package (iOS)  | `com.mrdarksidetm.wallet`              |
| Min Android    | API 29 (Android 10)                     |
| Target Android | API 34 (Android 14)                     |
| Design System  | Google Material 3 (Stitch UI)           |
| Iconography    | Adaptive Icons (Android)                |
| Architecture   | MVVM + Unidirectional Data Flow         |
| Network        | **Offline-first. Zero network calls.**  |

### 1.1 Iconography
- **Foreground**: Wallet Logo (`Wallet - Transparent.svg`)
- **Background**: `#fff8f6` (Light), `#1a110e` (Dark)
- **Monochrome Support**: Logo with `#795548`

---

## 2. Data Model

> **V2 Data Architecture (In Progress):** Both Android and Flutter platforms are actively migrating towards a UUID-based schema (String) to replace `Long`/`Int` IDs. This highly scalable normalized schema isolates `Transaction`, `Category`, and `Account` entities, using UUIDs for relationships instead of hard foreign keys to support easier syncing and memory efficiency.

### 2.1 Account

| Field          | Type     | Kotlin (Room)     | Dart (Isar)            | Notes                         |
|----------------|----------|-------------------|------------------------|-------------------------------|
| id             | String   | `String` (UUID)   | `String` (UUID)        | Primary key                   |
| name           | String   | `String`          | `String`               | e.g. "Cash", "Bank"          |
| type           | String   | `String`          | `String`               | "Bank", "Cash", "Wallet"     |
| initialBalance | Decimal  | `Double`          | `double`               | Starting balance              |
| createdAt      | Datetime | `Long` (millis)   | `DateTime`             | Auto-set on creation          |

### 2.2 Transaction

| Field     | Type     | Kotlin (Room)     | Dart (Isar)            | Notes                           |
|-----------|----------|-------------------|------------------------|---------------------------------|
| id        | String   | `String` (UUID)   | `String` (UUID)        | Primary key                     |
| accountId | String   | `String` (FK)     | `String` (link)        | Foreign key → Account           |
| amount    | Decimal  | `Double`          | `double`               | Always positive                 |
| type      | String   | `String`          | `String`               | `"Income"` / `"Expense"`       |
| note      | String   | `String`          | `String`               | User description                |
| category  | String   | `String`          | `String` (via Category)| e.g. "Food", "Salary"          |
| date      | Datetime | `Long` (millis)   | `DateTime`             | Transaction date                |

### 2.3 Category

| Field | Type    | Kotlin (Room)     | Dart (Isar)           | Notes                    |
|-------|---------|-------------------|-----------------------|--------------------------|
| id    | String  | `String` (UUID)   | `String` (UUID)       | Primary key              |
| name  | String  | `String`          | `String`              | e.g. "Food", "Transport" |
| icon  | String  | `String`          | `int` (Material icon) | Icon identifier          |

### 2.4 Person (New)

| Field     | Type     | Kotlin (Room)     | Dart (Isar)            | Notes                           |
|-----------|----------|-------------------|------------------------|---------------------------------|
| id        | String   | `String` (UUID)   | `String` (UUID)        | Primary key                     |
| name      | String   | `String`          | `String`               | Person's name                   |
| photoPath | String   | `String?`         | `String?`              | Local path to profile photo     |

### 2.5 Loan (New)

| Field     | Type     | Kotlin (Room)     | Dart (Isar)            | Notes                           |
|-----------|----------|-------------------|------------------------|---------------------------------|
| id        | String   | `String` (UUID)   | `String` (UUID)        | Primary key                     |
| personId  | String   | `String` (FK)     | `String` (link)        | Foreign key → Person            |
| amount    | Decimal  | `Double`          | `double`               | Always positive                 |
| type      | String   | `String`          | `String`               | `"Lent"` / `"Borrowed"`        |
| note      | String   | `String`          | `String`               | User description                |
| date      | Datetime | `Long` (millis)   | `DateTime`             | Loan date                       |
| isSettled | Boolean  | `Boolean`         | `bool`                 | Status of the loan              |

---

## 3. Screen Inventory

### 3.1 Dashboard (Home)

- **Route:** `/home`
- **Design:** Stitch Dark-First (Dynamic)
- **Total Balance Card:**
  - Large balance display with currency formatting
  - Eye icon toggle to hide/show balance
  - "This month" breakdown: Income (green) / Expense (red)
- **Overview Grid:** 2x2 Grid
  - Cards: Budgets, Assets, Bill Splitter, Loans
- **Top App Bar:**
  - Left: Wallet Icon + Greeting text ("Good late night")
  - Right: User profile avatar (clickable to `/settings`)
- **Bottom Navigation Bar:**
  - Floating pill-shaped design (Rounded)
  - Items: Home, Accounts, Reports, Settings
  - **Contextual FAB:** Center-mounted, changes icon/action based on current screen (Add Transaction for Home, Add Account for Accounts).

### 3.2 Accounts / Ledger

- **Route:** `/accounts`
- **Transaction list:** `LazyColumn` / `ListView`
  - Each item: Category icon, Note, Date, Amount
  - Amount color: green = Income, red = Expense
  - **Swipe-to-dismiss** for deletion (M3 `SwipeToDismissBox` / `Dismissible`)
  - List item animation on add/delete (glide, not instant)
- **Account filter tabs** at top

### 3.3 Add Transaction

- **Route:** `/add-transaction`
- **Income/Expense toggle:** M3 `SingleChoiceSegmentedButtonRow` / `SegmentedButton`
- **Amount field:** Numeric `OutlinedTextField` / `TextFormField`
- **Note field:** Text `OutlinedTextField` / `TextFormField`
- **Category selector:** Dropdown or BottomSheet
- **Account selector:** Dropdown
- **Save button:** Transforms into `CircularProgressIndicator` while saving
- **Validation:** Amount > 0, Note not empty

### 3.4 Reports / Analytics

- **Route:** `/reports`
- **Donut Chart:** Native `Canvas` / `CustomPainter` — NO third-party chart libraries
  - Shows Expense vs. Income ratio
  - Animated on load (sweep animation)
  - Center: total or percentage
- **Spending Breakdown:** Category-based list with amounts and percentages
- **Summary Card:** ElevatedCard with period totals

### 3.5 Settings

- **Route:** `/settings`
- **Design:** Stitch Refined List
- **Profile Section:** Large avatar with edit button, name, and email.
- **Categories:** General (Accounts, Categories, Currencies), Security & Privacy (Biometrics, Privacy Policy).
- **Logout:** Outlined "Sign Out" button.

### 3.6 Profile / Manage Accounts

- **Route:** `/profile`
- Change user name and circular profile photo
- List of accounts
- Add another account button

### 3.7 Loans

- **Route:** `/loans`
- Tabs: Lent, Borrowed, People
- Manage persons (Add/Delete)
- Manage loans (Add/Settle/Delete)

---

## 4. Design Tokens

### 4.1 Typography

| Scale          | Font              | Fallback   |
|----------------|-------------------|------------|
| Display/Title  | Google Sans Flex   | Noto Sans  |
| Body/Label     | Google Sans        | Noto Sans  |

All fonts are **bundled locally** in the APK/app bundle. No network font loading.

### 4.2 Colors

| Semantic       | Light Mode     | Dark Mode          |
|----------------|----------------|--------------------|
| Background     | M3 surface     | `#121212` / dark   |
| Income         | Vibrant green  | Vibrant green      |
| Expense        | Soft alert red | Soft alert red     |
| Card surface   | Elevated tone  | Elevated dark tone |

Dynamic M3 color is preferred. Hardcode premium defaults as fallback.

### 4.3 Spacing & Shape

| Element        | Corner Radius  |
|----------------|----------------|
| Standard Card  | 16.dp–24.dp    |
| Bottom Sheet   | 16.dp–24.dp    |
| Buttons        | M3 default     |

---

## 5. Behavioral Rules

1. **Offline-first:** Zero network calls. All data in local database.
2. **No splash screen:** Use M3 `CircularProgressIndicator` for operations taking 200ms–5s.
3. **Feedback:** `SnackbarHost` for success/deletion messages (non-blocking).
4. **Animations:** No instant vanishing. Use spring physics (`spring(dampingRatio, stiffness)` / implicit animations).
5. **List animations:** Items glide in/out on add/delete.
6. **Architecture:** Strict MVVM with unidirectional data flow.
7. **DI:** Manual (no Hilt/Dagger in Compose; Provider/Riverpod in Flutter).
8. **Memory ceiling:** Designed for 4GB RAM devices. Keep APK lean. No heavy libraries.

---

## 6. Feature Parity Checklist

| Feature                       | Compose | Flutter | Notes                         |
|-------------------------------|---------|---------|-------------------------------|
| Room/Isar database            | ✅      | ✅      |                               |
| Accounts CRUD                 | ✅      | ✅      |                               |
| Transactions CRUD             | ✅      | ✅      |                               |
| Categories                    | ✅      | ✅      |                               |
| Dashboard (Total Balance)     | ✅      | ✅      |                               |
| Overview Grid                 | ✅      | ✅      |                               |
| Transaction List              | ✅      | ✅      |                               |
| Swipe-to-delete               | ✅      | ✅      |                               |
| Add Transaction Form          | ✅      | ✅      |                               |
| Donut Chart (Canvas)          | ✅      | ✅      |                               |
| Spending Breakdown            | ✅      | ✅      |                               |
| Spend Heatmap                 | ✅      | ✅      | Compose Canvas implemented    |
| Insights (AI / Charts)        | ✅      | ✅      | Vico & fl_chart integrated    |
| Budgets                       | ✅      | ✅      | SQLite SUM calculations       |
| Bill Splitter                 | ✅      | ✅      | TransactionSplit DB added     |
| Goals                         | ✅      | ✅      | DAO and Models created        |
| Loans (Lent/Borrowed/People)  | ✅      | ⬜      | Compose implementation finished|
| Recurring Transactions        | ✅      | ✅      | WorkManager scaffolding added |
| Search                        | ✅      | ✅      | M3 SearchBar implemented      |
| Settings                      | ✅      | ✅      | Stitch dark theme added       |
| CSV Export                    | ✅      | ✅      | MediaStore utility added      |
| People/Contacts               | ✅      | ⬜      | Compose People tab added      |

| On-Device NLP / Chat            | ✅      | ✅      | Compose ChatScreen built      |
| Crashlytics (Offline)         | ✅      | ✅      | LocalCrashReporter added      |
| Haptic Engine                 | ✅      | ✅      | Vibrate API wrapped safely    |
| DB Vacuuming (Defrag)         | ✅      | ✅      | SQLite Truncate & Vacuum      |
| Home Screen Shortcuts         | ✅      | ✅      | ShortcutsUtil added           |
| Accessibility & L10n          | ⬜      | ⬜      |                               |
| Automated Testing             | ⬜      | ⬜      |                               |
| Gamification & Streaks        | ✅      | ✅      | StreakEngine integrated       |
| P2P Offline Sync Prep         | ⬜      | ⬜      |                               |
| CI/CD Deployment              | ⬜      | ⬜      |                               |
| Advanced DB Migrations        | ⬜      | ⬜      |                               |
| Offline OCR Receipts          | ⬜      | ⬜      |                               |
| Local Notifications           | ✅      | ✅      |                               |
| QR Code Data Transfer         | ⬜      | ⬜      |                               |
| Offline Multi-Currency        | ✅      | ✅      | CurrencyEngine added          |
| Debt Payoff Calculators       | ✅      | ✅      | DebtPayoffEngine added        |
| Custom Home Screen Widgets    | ⬜      | ⬜      |                               |
| Privacy Masking               | ✅      | ✅      | Masking toggles added         |
| Secure Data Shredding         | ✅      | ✅      | DataShredder util added       |
| Wear OS & Apple Watch         | ⬜      | ⬜      |                               |
| Anti-Tampering & Secure DB    | ⬜      | ⬜      |                               |
| Offline PDF Reporting         | ⬜      | ⬜      |                               |
| Local Voice Expense Entry     | ⬜      | ⬜      |                               |
| Privacy Geofencing            | ⬜      | ⬜      |                               |
| Quick Settings Tiles          | ⬜      | ⬜      |                               |
| Dynamic Theming               | ⬜      | ⬜      |                               |
| Custom App Icon Switcher      | ⬜      | ⬜      |                               |
| On-Device ML Retraining       | ⬜      | ⬜      |                               |
| Offline Onboarding            | ⬜      | ⬜      |                               |
| Profiling & Memory Leaks      | ⬜      | ⬜      |                               |
| Foldable Adaptive Layouts     | ⬜      | ⬜      |                               |
| Subscription Calendar         | ✅      | ✅      | Matrix grid implemented       |
| Account Reconciliation        | ✅      | ✅      | Flow diff engine built        |
| Round-Up Savings Goals        | ✅      | ✅      | RoundUpEngine added           |
| Backup Reminders & TTL        | ✅      | ✅      | TTL evaluation logic added    |
| Dynamic Feature Modules       | ⬜      | ⬜      |                               |
| UI Choreography (Phase 52)    | ✅      | ✅      | Bouncy clicks & odometers     |

> ⬜ = Not yet implemented &nbsp; ✅ = Implemented

---

## 10. Debug History & Known Issues

### 2026-03-19: Architecture Refactor & Stitch UI Sync
- **Feature:** Completely replaced old UI screens with Stitch-inspired dynamic components.
- **Navigation:** Implemented a unified root `Scaffold` in both Compose and Flutter to prevent navigation bar overlaps.
- **UI:** Added a floating center-mounted contextual FAB that adapts its icon and action based on the active tab.
- **Theming:** Extracted tokens from Stitch exports and implemented a full light/dark mode system using semantic Material 3 colors.
- **Status:** Phase 3 (Dashboard) and Phase 5 (Settings) fully integrated and logically wired to existing ViewModels and Providers.

### 2026-03-18: Dependency & Navigation Fixes
- **Feature:** Added Coil 2.7.0 dependency to `libs.versions.toml` and `app/build.gradle.kts`.
- **UI:** Fixed missing `clickable` import and resolved `MainAppScreen.kt` navigation conflicts between local and external `HomeScreen` definitions.
- **Navigation:** Updated `NavHost` to pass correct parameters to `HomeScreen`, `AccountsScreen`, `ReportsScreen`, and `SettingsScreen`.
- **Status:** Compilation successful. Verified with `./gradlew :app:compileDebugKotlin`.

### 2026-03-18: Stitch UI Integration
- **Feature:** Replaced Compose and Flutter Home/Settings screens with exact Dark Theme replicas from Stitch exports.
- **Status:** Phase 3 (Dashboard) and Settings visually complete.

### 2026-03-17: Home UI & Navigation Updates
- **Feature:** Updated Home screen Overview Grid to a dynamic 15-item menu including Budgets, Assets, Bill Splitter, Loans, Goals, Labels, Analytics, Recurring, Categories, Weekly, Places, Person, Calendar heatmap, Trend, and Recent transactions.
- **UI:** Reintroduced Premium banner next to the User profile on TopAppBar. App Logo added to the left.
- **UI:** Transformed Bottom NavigationBar into a floating pill-shaped design.

### 2026-03-15: Loan & Settings Implementation (Compose)
- **Feature:** Implemented Loan management with Lending, Borrowing, and People sub-menus.
- **Feature:** Added Profile/Manage Accounts screen and a dedicated Settings page.
- **UI:** Removed Premium banner from Home screen. Made "U" icon clickable.
- **Database:** Added `PersonEntity` and `LoanEntity` with Room migration (destructive for simplicity).
- **Status:** Compose implementation complete. Syncing to Flutter requested.

### 2026-03-14: Version Catalog & Toolchain Sync
- **Issue:** Duplicate `[versions]` and `[libraries]` sections in `libs.versions.toml` causing Gradle build failure.
- **Status:** Fixed. Verified by running `.\gradlew compileDebugKotlin`.
...
