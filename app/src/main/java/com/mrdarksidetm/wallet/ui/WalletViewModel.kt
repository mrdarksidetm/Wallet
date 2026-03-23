package com.mrdarksidetm.wallet.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrdarksidetm.wallet.data.*
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Law 1: Meaningful Names - CategorySpending clearly defines its purpose.
 */
@Immutable
data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Float
)

class WalletViewModel(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val personDao: PersonDao,
    private val loanDao: LoanDao,
    private val budgetDao: BudgetDao,
    private val goalDao: GoalDao,
    private val recurringDao: RecurringTransactionDao,
    private val labelDao: LabelDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val defaultAccountId = "default_cash"

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _userName = MutableStateFlow(sharedPreferences.getString("user_name", "User") ?: "User")        
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhotoPath = MutableStateFlow(sharedPreferences.getString("user_photo", null))
    val userPhotoPath: StateFlow<String?> = _userPhotoPath.asStateFlow()

    fun updateUserName(name: String) {
        _userName.value = name
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    fun updateUserPhoto(path: String?) {
        _userPhotoPath.value = path
        sharedPreferences.edit().putString("user_photo", path).apply()
    }

    val accounts: StateFlow<List<AccountEntity>> = accountDao.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = transactionDao.getActiveTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = budgetDao.getAllActiveBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = goalDao.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurringTransactions: StateFlow<List<RecurringTransactionEntity>> = recurringDao.getAllActiveRecurring()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val labels: StateFlow<List<LabelEntity>> = labelDao.getAllLabels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val persons: StateFlow<List<PersonEntity>> = personDao.getAllPersons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanEntity>> = loanDao.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun archiveTransaction(transactionId: String) {
        viewModelScope.launch(Dispatchers.IO) { transactionDao.archiveTransaction(transactionId) }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) { transactionDao.deleteTransaction(transaction) }
    }

    val thisMonthIncome: StateFlow<Double> = transactionDao.getTotalIncome(defaultAccountId)
        .combine(MutableStateFlow(0.0)) { income, _ -> income ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val thisMonthExpense: StateFlow<Double> = transactionDao.getTotalExpense(defaultAccountId)
        .combine(MutableStateFlow(0.0)) { expense, _ -> expense ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalBalance: StateFlow<Double> = combine(thisMonthIncome, thisMonthExpense) { income, expense ->       
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val spendingByCategory: StateFlow<List<CategorySpending>> = combine(recentTransactions, thisMonthExpense) { transactions, totalExpense ->
        if (totalExpense <= 0) return@combine emptyList<CategorySpending>()

        transactions
            .filter { it.type == "Expense" }
            .groupBy { it.category }
            .map { (category, list) ->
                val amount = list.sumOf { it.amount }
                CategorySpending(
                    category = category,
                    amount = amount,
                    percentage = (amount / totalExpense).toFloat()
                )
            }
            .sortedByDescending { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Goal Actions
    fun addGoal(name: String, target: Double, deadline: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.insertGoal(GoalEntity(name = name, targetAmount = target, deadline = deadline))
        }
    }

    fun updateGoalSavedAmount(goal: GoalEntity, newAmount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.updateGoal(goal.copy(savedAmount = newAmount))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.deleteGoal(goal)
        }
    }

    // Budget Actions
    fun addBudget(amount: Double, category: String, period: String, startDate: Long, endDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            budgetDao.insertBudget(BudgetEntity(amount = amount, category = category, period = period, startDate = startDate, endDate = endDate))
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch(Dispatchers.IO) { budgetDao.deleteBudget(budget) }
    }

    // Recurring Actions
    fun addRecurring(amount: Double, note: String, category: String, type: String, frequency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            recurringDao.insertRecurring(RecurringTransactionEntity(
                amount = amount, note = note, category = category, type = type, frequency = frequency,
                accountId = defaultAccountId, nextOccurrence = System.currentTimeMillis()
            ))
        }
    }

    fun deleteRecurring(recurring: RecurringTransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) { recurringDao.deleteRecurring(recurring) }
    }

    // Label Actions
    fun addLabel(name: String, color: String = "#2196F3") {
        viewModelScope.launch(Dispatchers.IO) {
            labelDao.insertLabel(LabelEntity(name = name, color = color))
        }
    }

    fun deleteLabel(label: LabelEntity) {
        viewModelScope.launch(Dispatchers.IO) { labelDao.deleteLabel(label) }
    }

    // Account Actions
    fun addAccount(name: String, type: String, initialBalance: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDao.insertAccount(AccountEntity(name = name, type = type, initialBalance = initialBalance))
        }
    }

    // Transaction Actions
    fun addTransaction(amount: Double, note: String, category: String, isIncome: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                transactionDao.insertTransaction(
                    TransactionEntity(
                        amount = amount,
                        note = note,
                        category = category,
                        type = if (isIncome) "Income" else "Expense",
                        accountId = defaultAccountId,
                        date = System.currentTimeMillis()
                    )
                )
                withContext(Dispatchers.Main) { onComplete() }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun addTransfer(amount: Double, note: String, fromAccountId: String, toAccountId: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                // Outgoing from source account
                transactionDao.insertTransaction(
                    TransactionEntity(
                        amount = amount,
                        note = "Transfer to account: $note",
                        category = "Transfer",
                        type = "Expense",
                        accountId = fromAccountId,
                        date = System.currentTimeMillis()
                    )
                )
                // Incoming to target account
                transactionDao.insertTransaction(
                    TransactionEntity(
                        amount = amount,
                        note = "Transfer from account: $note",
                        category = "Transfer",
                        type = "Income",
                        accountId = toAccountId,
                        date = System.currentTimeMillis()
                    )
                )
                withContext(Dispatchers.Main) { onComplete() }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    // Loan Actions
    fun addPerson(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            personDao.insertPerson(PersonEntity(name = name))
        }
    }

    fun deletePerson(person: PersonEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            personDao.deletePerson(person)
        }
    }

    fun addLoan(personId: String, amount: Double, type: String, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loanDao.insertLoan(LoanEntity(personId = personId, amount = amount, type = type, note = note))
        }
    }

    fun toggleLoanSettled(loan: LoanEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            loanDao.updateLoan(loan.copy(isSettled = !loan.isSettled))
        }
    }

    // Category Actions
    fun addCategory(name: String, icon: String = "List") {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.insertCategory(CategoryEntity(name = name, icon = icon))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.deleteCategory(category)
        }
    }

    fun clearError() { _error.value = null }

    class Factory(
        private val accountDao: AccountDao,
        private val transactionDao: TransactionDao,
        private val categoryDao: CategoryDao,
        private val personDao: PersonDao,
        private val loanDao: LoanDao,
        private val budgetDao: BudgetDao,
        private val goalDao: GoalDao,
        private val recurringDao: RecurringTransactionDao,
        private val labelDao: LabelDao,
        private val sharedPreferences: SharedPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(accountDao, transactionDao, categoryDao, personDao, loanDao, budgetDao, goalDao, recurringDao, labelDao, sharedPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
