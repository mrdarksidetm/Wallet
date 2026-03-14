package com.mrdarksidetm.wallet.data.domain

import kotlinx.coroutines.flow.Flow

/**
 * Repository layer acting as the single source of truth for transaction data.
 * 
 * Why use Flow for UI State?
 * Kotlin Flow provides a cold asynchronous data stream. Whenever a transaction is 
 * inserted, updated, or deleted in the Room database, Room automatically emits a new 
 * list through the Flow. Jetpack Compose observes this Flow using `collectAsState()`, 
 * which guarantees the UI instantly and reactively updates without manual refresh triggers.
 * This perfectly aligns with Unidirectional Data Flow architectures.
 */
class TransactionRepository(private val dao: TransactionDao) {

    suspend fun addTransaction(transaction: Transaction) {
        dao.add(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        dao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.delete(transaction)
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions()
    }

    fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>> {
        // Calculate the Unix timestamps for the start and end of the specified month
        val startOfMonth = getStartOfMonthTimestamp(month, year)
        val endOfMonth = getEndOfMonthTimestamp(month, year)
        
        return dao.getTransactionsByMonth(startOfMonth, endOfMonth)
    }
    
    // Helper functions for timestamp math
    private fun getStartOfMonthTimestamp(month: Int, year: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getEndOfMonthTimestamp(month: Int, year: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month - 1, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH), 23, 59, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}
