package com.darkytm.wallet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkytm.wallet.data.model.Account
import com.darkytm.wallet.data.model.AccountWithBalance
import com.darkytm.wallet.data.model.Budget
import com.darkytm.wallet.data.model.BudgetWithProgress
import com.darkytm.wallet.data.model.Category
import com.darkytm.wallet.data.model.Goal
import com.darkytm.wallet.data.model.GoalWithProgress
import com.darkytm.wallet.data.model.Person
import com.darkytm.wallet.data.model.PersonWithDebt
import com.darkytm.wallet.data.model.RecurringRule
import com.darkytm.wallet.data.model.Transaction
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.data.model.TransactionWithDetails
import com.darkytm.wallet.data.repository.MonthlyStats
import com.darkytm.wallet.data.repository.WalletRepository
import com.darkytm.wallet.ui.theme.PaletteStyle
import com.darkytm.wallet.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WalletUiState(
    val totalBalance: Double = 0.0,
    val recentTransactions: List<TransactionWithDetails> = emptyList(),
    val allTransactions: List<TransactionWithDetails> = emptyList(),
    val accounts: List<AccountWithBalance> = emptyList(),
    val goals: List<GoalWithProgress> = emptyList(),
    val people: List<PersonWithDebt> = emptyList(),
    val budgets: List<BudgetWithProgress> = emptyList(),
    val categories: List<Category> = emptyList(),
    val recurringRules: List<RecurringRule> = emptyList(),
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val isBalanceVisible: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val paletteStyle: PaletteStyle = PaletteStyle.EXPRESSIVE,
    val isDynamicColor: Boolean = false
)

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {

    private val themeModeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    private val paletteStyleFlow = MutableStateFlow(PaletteStyle.EXPRESSIVE)
    private val dynamicColorFlow = MutableStateFlow(false)
    private val isBalanceVisibleFlow = MutableStateFlow(true)

    // Combine financial entities (5 flows)
    private val financialCoreFlow = combine(
        repository.observeTotalBalance(),
        repository.getRecentTransactionsWithDetails(30),
        repository.getAllTransactionsWithDetails(),
        repository.observeAccountsWithBalances(),
        repository.observeGoalsWithProgress()
    ) { totalBalance, recentTxs, allTxs, accounts, goals ->
        CoreData(totalBalance, recentTxs, allTxs, accounts, goals)
    }

    // Combine secondary subsystems (5 flows)
    private val secondaryDataFlow = combine(
        repository.observePeopleWithDebts(),
        repository.observeBudgetsWithProgress(),
        repository.getAllCategories(),
        repository.getAllRecurringRules(),
        repository.observeMonthlyStats()
    ) { people, budgets, categories, recurringRules, monthlyStats ->
        SecondaryData(people, budgets, categories, recurringRules, monthlyStats)
    }

    // Combine theme settings (4 flows)
    private val themeSettingsFlow = combine(
        themeModeFlow,
        paletteStyleFlow,
        dynamicColorFlow,
        isBalanceVisibleFlow
    ) { themeMode, paletteStyle, dynamicColor, isBalanceVisible ->
        ThemeData(themeMode, paletteStyle, dynamicColor, isBalanceVisible)
    }

    // Final UI State combination (3 typed flows)
    val uiState: StateFlow<WalletUiState> = combine(
        financialCoreFlow,
        secondaryDataFlow,
        themeSettingsFlow
    ) { core, secondary, theme ->
        WalletUiState(
            totalBalance = core.totalBalance,
            recentTransactions = core.recentTransactions,
            allTransactions = core.allTransactions,
            accounts = core.accounts,
            goals = core.goals,
            people = secondary.people,
            budgets = secondary.budgets,
            categories = secondary.categories,
            recurringRules = secondary.recurringRules,
            monthlyIncome = secondary.monthlyStats.income,
            monthlyExpense = secondary.monthlyStats.expense,
            isBalanceVisible = theme.isBalanceVisible,
            themeMode = theme.themeMode,
            paletteStyle = theme.paletteStyle,
            isDynamicColor = theme.dynamicColor
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WalletUiState()
    )

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        title: String = "",
        note: String = "",
        accountId: Long,
        toAccountId: Long? = null,
        categoryId: Long? = null,
        personId: Long? = null,
        goalId: Long? = null,
        dateMillis: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                Transaction(
                    amount = amount,
                    type = type,
                    title = title,
                    note = note,
                    dateMillis = dateMillis,
                    accountId = accountId,
                    toAccountId = toAccountId,
                    categoryId = categoryId,
                    personId = personId,
                    goalId = goalId
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun toggleBalanceVisibility() {
        isBalanceVisibleFlow.value = !isBalanceVisibleFlow.value
    }

    fun setThemeMode(mode: ThemeMode) {
        themeModeFlow.value = mode
    }

    fun setPaletteStyle(style: PaletteStyle) {
        paletteStyleFlow.value = style
    }

    fun setDynamicColor(enabled: Boolean) {
        dynamicColorFlow.value = enabled
    }

    fun addAccount(account: Account) {
        viewModelScope.launch { repository.addAccount(account) }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch { repository.addGoal(goal) }
    }

    fun addPerson(person: Person) {
        viewModelScope.launch { repository.addPerson(person) }
    }

    fun addBudget(budget: Budget) {
        viewModelScope.launch { repository.addBudget(budget) }
    }

    fun addRecurringRule(rule: RecurringRule) {
        viewModelScope.launch { repository.addRecurringRule(rule) }
    }

    private data class CoreData(
        val totalBalance: Double,
        val recentTransactions: List<TransactionWithDetails>,
        val allTransactions: List<TransactionWithDetails>,
        val accounts: List<AccountWithBalance>,
        val goals: List<GoalWithProgress>
    )

    private data class SecondaryData(
        val people: List<PersonWithDebt>,
        val budgets: List<BudgetWithProgress>,
        val categories: List<Category>,
        val recurringRules: List<RecurringRule>,
        val monthlyStats: MonthlyStats
    )

    private data class ThemeData(
        val themeMode: ThemeMode,
        val paletteStyle: PaletteStyle,
        val dynamicColor: Boolean,
        val isBalanceVisible: Boolean
    )
}
