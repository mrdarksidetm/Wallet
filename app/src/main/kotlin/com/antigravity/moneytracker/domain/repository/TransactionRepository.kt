package com.antigravity.moneytracker.domain.repository

import com.antigravity.moneytracker.domain.entity.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getTotalIncome(): Flow<Double>
    fun getTotalExpense(): Flow<Double>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
}
