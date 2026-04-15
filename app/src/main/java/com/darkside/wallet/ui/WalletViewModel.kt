package com.darkside.wallet.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkside.wallet.data.*
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
    private val transactionService: com.darkside.wallet.data.domain.TransactionService,
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

    private val _currencyCode = MutableStateFlow(sharedPreferences.getString("currency_code", "INR") ?: "INR")
    val currencyCode: StateFlow<String> = _currencyCode.asStateFlow()

    fun updateUserName(name: String) {
        _userName.value = name
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    fun updateUserPhoto(context: Context, uri: android.net.Uri) {
        val permanentPath = com.darkside.wallet.utils.FileUtils.saveImagePermanently(context, uri)
        _userPhotoPath.value = permanentPath
        sharedPreferences.edit().putString("user_photo", permanentPath).apply()
    }

    fun updateUserPhoto(path: String?) {
        _userPhotoPath.value = path
        sharedPreferences.edit().putString("user_photo", path).apply()
    }

    fun updatePersonPhoto(context: Context, person: PersonEntity, uri: android.net.Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val permanentPath = com.darkside.wallet.utils.FileUtils.saveImagePermanently(context, uri)
            val updatedPerson = person.copy(avatar = permanentPath)
            personDao.updatePerson(updatedPerson)
        }
    }

    fun updateCurrency(code: String) {
        _currencyCode.value = code
        sharedPreferences.edit().putString("currency_code", code).apply()
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
        viewModelScope.launch(Dispatchers.IO) { 
            _isSaving.value = true
            try {
                transactionService.deleteTransaction(transaction.id)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    val totalIncome: StateFlow<Double> = transactionDao.getTotalIncome(defaultAccountId)
        .combine(MutableStateFlow(0.0)) { income, _ -> income ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = transactionDao.getTotalExpense(defaultAccountId)
        .combine(MutableStateFlow(0.0)) { expense, _ -> expense ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalBalance: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->       
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val spendingByCategory: StateFlow<List<CategorySpending>> = combine(recentTransactions, totalExpense) { transactions, expense ->
        if (expense <= 0) return@combine emptyList<CategorySpending>()

        transactions
            .filter { it.type == "expense" }
            .groupBy { it.categoryId }
            .map { (categoryId, list) ->
                val amount = list.sumOf { it.amount }
                CategorySpending(
                    category = categoryId,
                    amount = amount,
                    percentage = (amount / expense).toFloat()
                )
            }
            .sortedByDescending { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val heatmapData: StateFlow<Map<Long, Int>> = recentTransactions.map { transactions ->
        val map = mutableMapOf<Long, Int>()
        transactions.forEach { tx ->
            val calendar = java.util.Calendar.getInstance().apply { 
                timeInMillis = tx.date
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val date = calendar.timeInMillis
            map[date] = (map[date] ?: 0) + 1
        }
        map
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val incomeTrends: StateFlow<List<Pair<Long, Double>>> = recentTransactions.map { transactions ->
        val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        val daily = mutableMapOf<Long, Double>()
        transactions.filter { it.type == "income" && it.date >= thirtyDaysAgo }.forEach { tx ->
            val calendar = java.util.Calendar.getInstance().apply { 
                timeInMillis = tx.date
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val date = calendar.timeInMillis
            daily[date] = (daily[date] ?: 0.0) + tx.amount
        }
        daily.entries.sortedBy { it.key }.map { it.key to it.value }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseTrends: StateFlow<List<Pair<Long, Double>>> = recentTransactions.map { transactions ->
        val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        val daily = mutableMapOf<Long, Double>()
        transactions.filter { it.type == "expense" && it.date >= thirtyDaysAgo }.forEach { tx ->
            val calendar = java.util.Calendar.getInstance().apply { 
                timeInMillis = tx.date
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val date = calendar.timeInMillis
            daily[date] = (daily[date] ?: 0.0) + tx.amount
        }
        daily.entries.sortedBy { it.key }.map { it.key to it.value }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Goal Actions
    fun addGoal(name: String, target: Double, deadline: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.insertGoal(GoalEntity(name = name, targetAmount = target, deadline = deadline))
        }
    }

    fun updateGoalAmount(goal: GoalEntity, newAmount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.updateGoal(goal.copy(currentAmount = newAmount))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.deleteGoal(goal)
        }
    }

    // Budget Actions
    fun addBudget(amount: Double, categoryId: String, period: String, startDate: Long, endDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            budgetDao.insertBudget(BudgetEntity(amount = amount, categoryId = categoryId, period = period, startDate = startDate, endDate = endDate))
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch(Dispatchers.IO) { budgetDao.deleteBudget(budget) }
    }

    // Recurring Actions
    fun addRecurring(amount: Double, note: String, categoryId: String, type: String, frequency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            recurringDao.insertRecurring(RecurringTransactionEntity(
                amount = amount, note = note, categoryId = categoryId, type = type, frequency = frequency,
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
            accountDao.insertAccount(AccountEntity(name = name, type = type, initialBalance = initialBalance, balance = initialBalance))
        }
    }

    // Transaction Actions
    fun addTransaction(amount: Double, note: String, categoryId: String, type: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                transactionService.addTransaction(
                    amount = amount,
                    note = note,
                    categoryId = categoryId,
                    type = type.lowercase(),
                    accountId = defaultAccountId,
                    date = System.currentTimeMillis()
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
                transactionService.addTransaction(
                    amount = amount,
                    note = note,
                    type = "transfer",
                    accountId = fromAccountId,
                    transferAccountId = toAccountId,
                    categoryId = "transfer",
                    date = System.currentTimeMillis()
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
    fun addPerson(context: Context, name: String, photoUri: android.net.Uri? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val avatarPath = photoUri?.let { com.darkside.wallet.utils.FileUtils.saveImagePermanently(context, it) }
            personDao.insertPerson(PersonEntity(name = name, avatar = avatarPath))
        }
    }

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

    fun toggleLoanActive(loan: LoanEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            loanDao.updateLoan(loan.copy(isActive = !loan.isActive))
        }
    }

    // Category Actions
    fun addCategory(name: String, icon: String = "category") {
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
        private val transactionService: com.darkside.wallet.data.domain.TransactionService,
        private val performanceAuditService: com.darkside.wallet.data.domain.PerformanceAuditService,
        private val sharedPreferences: SharedPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(
                    accountDao, transactionDao, categoryDao, personDao, loanDao, 
                    budgetDao, goalDao, recurringDao, labelDao, 
                    transactionService, performanceAuditService, sharedPreferences
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
 
                    budgetDao, goalDao, recurringDao, labelDao, 
                    transactionService, sharedPreferences
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
