package com.antigravity.moneytracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.moneytracker.domain.ai.SpendingAnalyzer
import com.antigravity.moneytracker.domain.entity.Transaction
import com.antigravity.moneytracker.domain.entity.TransactionType
import com.antigravity.moneytracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val aiSuggestion: String = "Analyzing...",
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: TransactionRepository,
    private val spendingAnalyzer: SpendingAnalyzer
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            repository.getAllTransactions().collectLatest { transactions ->
                val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                val balance = income - expense
                val suggestion = spendingAnalyzer.analyze(transactions)

                _uiState.value = HomeUiState(
                    totalBalance = balance,
                    income = income,
                    expense = expense,
                    recentTransactions = transactions.take(5), // Top 5 recent
                    aiSuggestion = suggestion
                )
            }
        }
    }

    fun addTransaction(amount: Double, description: String, type: TransactionType) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                description = description,
                date = Date(),
                type = type,
                categoryId = null,
                accountId = null
            )
            repository.addTransaction(transaction)
        }
    }
}
