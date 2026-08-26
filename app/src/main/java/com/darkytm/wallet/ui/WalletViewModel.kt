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
import com.darkytm.wallet.data.model.Transaction
import com.darkytm.wallet.data.model.TransactionType
import com.darkytm.wallet.data.model.TransactionWithDetails
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
    val accounts: List<AccountWithBalance> = emptyList(),
    val goals: List<GoalWithProgress> = emptyList(),
    val people: List<PersonWithDebt> = emptyList(),
    val budgets: List<BudgetWithProgress> = emptyList(),
    val categories: List<Category> = emptyList(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val paletteStyle: PaletteStyle = PaletteStyle.EXPRESSIVE,
    val isDynamicColor: Boolean = false
)

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {

    private val themeModeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    private val paletteStyleFlow = MutableStateFlow(PaletteStyle.EXPRESSIVE)
    private val dynamicColorFlow = MutableStateFlow(false)

    val uiState: StateFlow<WalletUiState> = combine(
        repository.observeTotalBalance(),
        repository.getRecentTransactionsWithDetails(30),
        repository.observeAccountsWithBalances(),
        repository.observeGoalsWithProgress(),
        repository.observePeopleWithDebts(),
        repository.observeBudgetsWithProgress(),
        repository.getAllCategories(),
        themeModeFlow,
        paletteStyleFlow,
        dynamicColorFlow
    ) { totalBalance, txs, accounts, goals, people, budgets, categories, themeMode, paletteStyle, dynamicColor ->
        WalletUiState(
            totalBalance = totalBalance,
            recentTransactions = txs,
            accounts = accounts,
            goals = goals,
            people = people,
            budgets = budgets,
            categories = categories,
            themeMode = themeMode,
            paletteStyle = paletteStyle,
            isDynamicColor = dynamicColor
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
}
