# System Instructions: Project Wallet (Paisa Clone)

## 1. Project Overview
Build a premium, offline-first personal finance dashboard for Android 14. The app allows users to monitor their financial health, view transaction history, manage accounts (wallets), and track spending visually. The app must feel fluid, tactile, and native.

## 2. Technical Stack & Libraries
* **UI Framework:** Jetpack Compose (Material 3)
* **Architecture:** MVVM (Model-View-ViewModel) with strict Unidirectional Data Flow.
* **Database:** Room Database (SQLite) with Kotlin Coroutines & Flow for reactive UI.
* **Navigation:** `androidx.navigation:navigation-compose`
* **Dependency Injection:** Manual (Custom ViewModelProviders) to save compilation memory. Do not use Hilt/Dagger.
* **Charts:** Native Compose `Canvas` (avoid heavy 3rd-party charting libraries to minimize APK bloat and memory usage).

## 3. Data Model (Room Entities)

### AccountEntity

```kotlin
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g., "Cash", "Bank"
    val initialBalance: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions", 
    foreignKeys = [ForeignKey(entity = AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"])]
)
```

### TransactionEntity

```kotlin
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val amount: Double,
    val type: String, // "Income" or "Expense"
    val note: String,
    val category: String, // e.g., "Food", "Salary"
    val dateMillis: Long = System.currentTimeMillis()
)
```

## 4. UI/Screen Structure

* Top App Bar (Global)
* Placeholder profile icon (Left)
* Greeting Text "Good evening, [User]"
* Premium Badge/Icon (Right)
* Dashboard (HomeScreen)
  
* **Total Balance Card:** Massive balance display, eye icon to hide balance, "This month" breakdown for Income/Expense with percentage indicators.

* **Overview Grid:** LazyVerticalGrid (2 columns) showing summary cards for Budgets, Assets, Bill Splitter, and Loans.

* **Ledger (AccountsScreen):**
LazyColumn listing all transactions.

Transaction Item: Icon, Note, Date, Amount (Green for Income, Red for Expense).

Swipe-to-Dismiss functionality for deletion.

Input Form (AddTransactionScreen)
Material 3 SingleChoiceSegmentedButtonRow for Income/Expense toggle.

Numeric OutlinedTextField for Amount.

Text OutlinedTextField for Note.

Category Selector (Dropdown or BottomSheet).

Save Button (transforms into CircularProgressIndicator while saving).

Analytics (ReportsScreen)
Native Compose Donut Chart showing Expense vs. Income ratio.

<br>

## Breakdown list of spending by category.

### 1. Execution Roadmap & Rules
**Rule:** You will execute this project phase by phase. Do not build the entire app at once. Before starting a phase, read the existing project files. If a phase is already clearly implemented and fully functional, acknowledge it and skip to the next.

**Search Rule:** Always use the internet to search https://developer.android.com/reference/kotlin/androidx/compose/material3/ to fetch the most up-to-date syntax for M3 components before implementing them.

<br>

Phase 1: The Engine - Implement Room Database, Entities, and DAOs.

Phase 2: The Skeleton - Implement Jetpack Navigation Compose and the Bottom Navigation Bar.

Phase 3: The Dashboard - Build HomeScreen and map it to WalletViewModel StateFlows.

Phase 4: The Ledger - Build AccountsScreen history list.

Phase 5: The Input Pipeline - Build AddTransactionScreen and wire data insertion.

Phase 6: Visualizations - Implement native Canvas charts in ReportsScreen.