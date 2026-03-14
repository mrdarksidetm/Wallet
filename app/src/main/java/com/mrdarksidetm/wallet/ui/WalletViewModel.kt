package com.mrdarksidetm.wallet.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrdarksidetm.wallet.data.CategoryDao
import com.mrdarksidetm.wallet.data.CategoryEntity
import com.mrdarksidetm.wallet.data.TransactionDao
import com.mrdarksidetm.wallet.data.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Law 1: Meaningful Names - CategorySpending clearly defines its purpose.
 * Law 2: Single Responsibility - This class solely represents the UI state for category breakdown.
 * Compose Optimization: @Immutable ensures the compiler skips recomposition when the list instance is unchanged.
 */
@Immutable
data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Float
)

class WalletViewModel(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    // The default account UUID for offline-first local testing
    private val defaultAccountId = "default_account_id"

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    /**
     * Law 3: Clean Flow - Reactive streams map database state directly to UI state
     * seamlessly without blocking the main thread.
     */
    val categories: StateFlow<List<CategoryEntity>> = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = transactionDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun saveTransaction(amount: Double, note: String, type: String, category: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _error.value = null
                val transaction = TransactionEntity(
                    amount = amount,
                    date = System.currentTimeMillis(),
                    type = type,
                    note = note,
                    category = category,
                    accountId = defaultAccountId // Updated to String for UUID migration
                )
                transactionDao.insertTransaction(transaction)
                
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun addTransaction(amount: String, note: String, category: String, isIncome: Boolean, navigateBack: () -> Unit) {
        val parsedAmount = amount.toDoubleOrNull()
        if (parsedAmount == null || parsedAmount <= 0.0) {
            _error.value = "Please enter a valid amount."
            return
        }

        _isSaving.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _error.value = null
                val transaction = TransactionEntity(
                    amount = parsedAmount,
                    date = System.currentTimeMillis(),
                    type = if (isIncome) "Income" else "Expense",
                    note = note,
                    category = category,
                    accountId = defaultAccountId // Updated to String for UUID migration
                )
                transactionDao.insertTransaction(transaction)
                
                withContext(Dispatchers.Main) {
                    _isSaving.value = false
                    navigateBack()
                }
            } catch (e: Exception) {
                _isSaving.value = false
                _error.value = "Failed to save transaction: ${e.message}"
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                transactionDao.deleteTransaction(transaction)
            } catch (e: Exception) {
                _error.value = "Failed to delete transaction: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    class Factory(
        private val transactionDao: TransactionDao,
        private val categoryDao: CategoryDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(transactionDao, categoryDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
