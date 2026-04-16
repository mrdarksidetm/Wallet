package com.darkside.wallet.data.domain

import com.darkside.wallet.data.TransactionDao
import com.darkside.wallet.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository layer acting as the single source of truth for transaction data.
 */
class TransactionRepository(private val dao: TransactionDao) {

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return dao.getAllTransactions()
    }

    fun getTransactionsForAccount(accountId: Long): Flow<List<TransactionEntity>> {
        return dao.getTransactionsForAccount(accountId)
    }

    suspend fun getTransactionById(id: Long): TransactionEntity? {
        return dao.getTransactionById(id)
    }

    fun getActiveTransactions(): Flow<List<TransactionEntity>> {
        return dao.getActiveTransactions()
    }

    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>> {
        return dao.getRecentTransactions(limit)
    }
}
