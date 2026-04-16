package com.darkside.wallet.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkside.wallet.data.domain.*
import com.darkside.wallet.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class CategorySpending(
    val categoryId: Long,
    val categoryName: String,
    val amount: Double,
    val percentage: Float,
    val color: String
)

class WalletViewModel(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val personRepository: PersonRepository,
    private val loanRepository: LoanRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: GoalRepository,
    private val recurringRepository: RecurringRepository,
    private val transactionService: TransactionService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

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

    fun updateCurrency(code: String) {
        _currencyCode.value = code
        sharedPreferences.edit().putString("currency_code", code).apply()
    }

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = transactionRepository.getActiveTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = budgetRepository.getAllBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = goalRepository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurringTransactions: StateFlow<List<RecurringEntity>> = recurringRepository.getAllRecurring()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val persons: StateFlow<List<PersonEntity>> = personRepository.getAllPeople()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanEntity>> = loanRepository.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                transactionService.deleteTransaction(transaction)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    val totalIncome: StateFlow<Double> = recentTransactions.map { txs ->
        txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = recentTransactions.map { txs ->
        txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalBalance: StateFlow<Double> = accounts.map { accs ->
        accs.sumOf { it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val spendingByCategory: StateFlow<List<CategorySpending>> = combine(recentTransactions, categories, totalExpense) { transactions, cats, expense ->
        if (expense <= 0) return@combine emptyList<CategorySpending>()

        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, list) ->
                val category = cats.find { it.id == categoryId } ?: return@mapNotNull null
                val amount = list.sumOf { it.amount }
                CategorySpending(
                    categoryId = categoryId,
                    categoryName = category.name,
                    amount = amount,
                    percentage = (amount / expense).toFloat(),
                    color = category.color
                )
            }
            .sortedByDescending { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(
        amount: Double,
        note: String?,
        categoryId: Long,
        type: TransactionType,
        accountId: Long,
        transferAccountId: Long? = null,
        personId: Long = 0,
        date: Long = System.currentTimeMillis(),
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            try {
                transactionService.addTransaction(
                    amount = amount,
                    note = note,
                    categoryId = categoryId,
                    type = type,
                    accountId = accountId,
                    transferAccountId = transferAccountId,
                    personId = personId,
                    date = date
                )
                withContext(Dispatchers.Main) { onComplete() }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun addLoan(personId: Long, amount: Double, type: LoanType, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            loanRepository.insertLoan(LoanEntity(personId = personId, amount = amount, type = type, note = note))
        }
    }

    fun toggleLoanSettled(loan: LoanEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            loanRepository.updateLoan(loan.copy(isPaid = !loan.isPaid))
        }
    }

    fun addPerson(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            personRepository.insertPerson(PersonEntity(name = name))
        }
    }

    fun deletePerson(person: PersonEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            personRepository.deletePerson(person)
        }
    }

    fun addAccount(name: String, type: AccountType, balance: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.insertAccount(AccountEntity(name = name, type = type, balance = balance))
        }
    }

    fun clearError() { _error.value = null }

    class Factory(
        private val accountRepository: AccountRepository,
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository,
        private val personRepository: PersonRepository,
        private val loanRepository: LoanRepository,
        private val budgetRepository: BudgetRepository,
        private val goalRepository: GoalRepository,
        private val recurringRepository: RecurringRepository,
        private val transactionService: TransactionService,
        private val sharedPreferences: SharedPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(
                    accountRepository, transactionRepository, categoryRepository,
                    personRepository, loanRepository, budgetRepository,
                    goalRepository, recurringRepository, transactionService, sharedPreferences
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
